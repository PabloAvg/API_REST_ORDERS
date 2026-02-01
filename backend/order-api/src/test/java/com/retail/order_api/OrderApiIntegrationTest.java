package com.retail.order_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void createOrder_calculatesTotalWithVatAndPendingStatus() throws Exception {
        // neto: 2*25.00 + 1*35.50 = 85.50 -> bruto: 85.50*1.21 = 103.46
        String body = """
                {
                  "items": [
                    { "productId": 1, "quantity": 2 },
                    { "productId": 4, "quantity": 1 }
                  ]
                }
                """;

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalPrice").value(103.46))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].unitPrice").value(25.00))
                .andExpect(jsonPath("$.items[0].subtotal").value(50.00));
    }

    @Test
    void createOrder_unknownProduct_returns404() throws Exception {
        String body = """
                {
                  "items": [
                    { "productId": 999, "quantity": 1 }
                  ]
                }
                """;

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createOrder_invalidQuantity_returns400() throws Exception {
        String body = """
                {
                  "items": [
                    { "productId": 1, "quantity": 0 }
                  ]
                }
                """;

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
