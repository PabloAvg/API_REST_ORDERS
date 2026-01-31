export function round2(value: number): number {
  return Math.round((value + Number.EPSILON) * 100) / 100;
}

export function netTotal(lines: Array<{ unitPrice: number; quantity: number }>): number {
  return round2(lines.reduce((acc, l) => acc + l.unitPrice * l.quantity, 0));
}

export function vatAmount(net: number, vatRate = 0.21): number {
  return round2(net * vatRate);
}

export function grossTotal(net: number, vatRate = 0.21): number {
  return round2(net * (1 + vatRate));
}
