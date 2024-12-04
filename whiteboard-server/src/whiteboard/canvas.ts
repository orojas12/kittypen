export type Rgba = {
  r: number;
  g: number;
  b: number;
  a: number;
};

const DEFAULT_WIDTH = 100;
const DEFAULT_HEIGHT = 100;

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
  private width: number;
  private height: number;
  private data: Uint8ClampedArray;

  constructor(width?: number, height?: number, data?: Uint8ClampedArray) {
    this.width = width || DEFAULT_WIDTH;
    this.height = height || DEFAULT_HEIGHT;
    this.data =
      data?.slice() || new Uint8ClampedArray(this.width * this.height * 4);
  }

  drawLine = (
    startX: number,
    startY: number,
    endX: number,
    endY: number,
    rgba: Rgba,
  ): void => {
    const dx = endX - startX;
    const dy = endY - startY;
    const step = Math.max(Math.abs(dx), Math.abs(dy));
    if (step === 0) {
      this.drawPixel(startX, startY, rgba);
    } else {
      const stepX = dx / step;
      const stepY = dy / step;
      for (let i = 0; i < step + 1; i++) {
        this.drawPixel(
          Math.round(startX + i * stepX),
          Math.round(startY + i * stepY),
          rgba,
        );
      }
    }
  };

  drawPixel = (x: number, y: number, rgba: Rgba): void => {
    const index = (x + y * this.width) * 4;
    this.data[index] = rgba.r; // red
    this.data[index + 1] = rgba.g; // green
    this.data[index + 2] = rgba.b; // blue
    this.data[index + 3] = rgba.a; // alpha
  };

  reset = (): void => {
    this.data = new Uint8ClampedArray(this.width * this.height * 4);
  };

  /**
   * Returns a copy of the underlying Uint8ClampedArray representing canvas data.
   */
  getData = (): Uint8ClampedArray => {
    return this.data.slice();
  };
}
