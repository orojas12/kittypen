package dev.oscarrojas.drawandguess.core.lobby.canvas;

import java.util.Arrays;
import java.util.UUID;

public class Canvas {

    private final String id = UUID.randomUUID().toString();
    private final int width;
    private final int height;
    private final byte[] data;

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new byte[width * height * 4];
    }

    public Canvas(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Inserts pixel data from a canvas frame into the canvas' data array
     *
     * @param frame canvas frame
     */
    public void putFrame(CanvasFrame frame) {
        copyToDst(
                frame.getData(), this.data,
                frame.getStartX(), frame.getStartY(),
                frame.getWidth(), frame.getHeight(),
                this.width, this.height
        );
    }

    /**
     * Gets a frame or rectangle representing a portion of the canvas.
     *
     * @param startX starting x position
     * @param startY starting y position
     * @param width  rectangle width
     * @param height rectangle height
     * @return A frame representing a portion of the canvas
     */
    public CanvasFrame getFrame(int startX, int startY, int width, int height) {
        byte[] frameData = new byte[width * height * 4];

        copyFromSrc(
                this.data, frameData,
                startX, startY,
                this.width, this.height,
                width, height
        );

        return new CanvasFrame(
                0,
                0,
                width,
                height,
                frameData
        );
    }

    private static void copyToDst(
            byte[] src, byte[] dst,
            int dstX, int dstY,
            int srcWidth, int srcHeight,
            int dstWidth, int dstHeight
    ) {
        int bytesPerPixel = 4;

        // src or dst have incorrect buffer size
        if (src.length < srcWidth * srcHeight * bytesPerPixel ||
                dst.length < dstWidth * dstHeight * bytesPerPixel
        ) {
            throw new IllegalArgumentException("Invalid src or dst buffer size");
        }

        // frame is not within dst dimensions
        if (dstX < 0 || dstY < 0 ||
                dstX > dstWidth - 1 || dstY > dstHeight - 1 ||
                dstX + srcWidth > dstWidth ||
                dstY + srcHeight > dstHeight
        ) {
            throw new IllegalArgumentException("Source data does not fit in destination");
        }

        for (int srcRow = 0; srcRow < srcHeight; srcRow++) {
            // copy one row at a time
            int srcPos = srcRow * srcWidth * bytesPerPixel;
            int dstPos = (((dstY + srcRow) * dstWidth) + dstX) * bytesPerPixel;
            System.arraycopy(src, srcPos, dst, dstPos, srcWidth * bytesPerPixel);
        }
    }

    private static void copyFromSrc(
            byte[] src, byte[] dst,
            int srcX, int srcY,
            int srcWidth, int srcHeight,
            int dstWidth, int dstHeight
    ) {
        int bytesPerPixel = 4;

        // src or dst have incorrect buffer size
        if (src.length != srcWidth * srcHeight * bytesPerPixel ||
                dst.length != dstWidth * dstHeight * bytesPerPixel
        ) {
            throw new IllegalArgumentException("Invalid src or dst buffer size");
        }

        // frame is not within src dimensions
        if (srcX < 0 || srcY < 0 ||
                srcX > srcWidth - 1 || srcY > srcHeight - 1 ||
                srcX + dstWidth > srcWidth ||
                srcY + dstHeight > srcHeight
        ) {
            throw new IllegalArgumentException("Source data does not fit in destination");
        }

        for (int dstRow = 0; dstRow < dstHeight; dstRow++) {
            // copy one row at a time
            int srcPos = (((srcY + dstRow) * srcWidth) + srcX) * bytesPerPixel;
            int dstPos = dstRow * dstWidth * bytesPerPixel;
            System.arraycopy(src, srcPos, dst, dstPos, dstWidth * bytesPerPixel);
        }
    }

    void reset() {
        Arrays.fill(data, (byte) 0);
    }

}
