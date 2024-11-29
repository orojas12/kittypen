/**
 * Data representation of an HTML canvas element.
 *
   Each pixel in the canvas is represented by four values in an 8-bit
   unsigned integer array. Each value represents a component of rgba
   data (red, green, blue, or alpha)

   Example:

   [[255, 0, 0, 1, 0, 255, 0, 1]]

   The example above represents two pixels, one red and one green.
   The first four values in the array is the first pixel, the next
   four values is the second pixel.
 */
export class Canvas {
  private width = 100;
  private height = 100;
  private data = new Uint8ClampedArray(this.width * this.height * 4);

  constructor(width?: number, height?: number, data?: Uint8ClampedArray) {
    this.width = width || this.width;
    this.height = height || this.height;
    this.data = data?.slice() || this.data;
  }

  drawLine = (
    startX: number,
    startY: number,
    endX: number,
    endY: number,
    rgba?: [number, number, number, number],
  ): void => {
    const dx = endX - startX;
    const dy = endY - startY;
    const step = Math.max(Math.abs(dx), Math.abs(dy));
    if (step !== 0) {
      const stepX = dx / step;
      const stepY = dy / step;
      for (let i = 0; i < step + 1; i++) {
        this.drawPixel(
          Math.round(startX + i * stepX),
          Math.round(startY + i * stepY),
        );
      }
    }
  };

  drawPixel = (x: number, y: number): void => {
    const index = (x + y * this.width) * 4;
    this.data[index] = 0; // red
    this.data[index + 1] = 0; // green
    this.data[index + 2] = 0; // blue
    this.data[index + 3] = 1; // alpha
  };

  /**
   * Returns a copy of the underlying Uint8ClampedArray representing canvas data.
   */
  getData = (): Uint8ClampedArray => {
    return this.data.slice();
  };
}
