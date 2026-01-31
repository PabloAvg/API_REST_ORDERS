import { grossTotal, netTotal, vatAmount } from './money';

describe('money utils', () => {
  it('calculates net/vat/gross with rounding', () => {
    const net = netTotal([
      { unitPrice: 25.00, quantity: 1 },
      { unitPrice: 8.75, quantity: 3 },
    ]);

    expect(net).toBe(51.25);
    expect(vatAmount(net)).toBe(10.76);
    expect(grossTotal(net)).toBe(62.01);
  });
});
