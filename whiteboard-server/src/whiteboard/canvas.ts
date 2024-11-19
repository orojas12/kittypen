// width * height * 4
// each pixel in the canvas is represented by four values in the array,
// each an 8 bit unsigned integer representing a component of rgba
// data (red, green, blue, or alpha)
const DEFAULT_CANVAS_DATA_ARRAY_SIZE = 100 * 100 * 4;

/**
 * Data representation of an HTML canvas element
 */
export class Canvas {
  private data = new Uint8ClampedArray(DEFAULT_CANVAS_DATA_ARRAY_SIZE);

  drawLine = (
    start: [number, number],
    end: [number, number],
    rgba?: [number, number, number, number],
  ): void => {};

  getData = (): Readonly<Uint8ClampedArray> => {
    return this.data;
  };
}
