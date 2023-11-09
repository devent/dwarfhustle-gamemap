package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngReader;
import lombok.RequiredArgsConstructor;

/**
 * Loads the terrain from a set of images.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
public class TerrainLoadImage {

    private final int depth;

    private final int height;

    private final int width;

    private final int columns;

    private final int rows;

    public CompletionStage<long[][][]> loadAsync(File file) {
        return CompletableFuture.supplyAsync(() -> load(file));
    }

    public long[][][] load(URL file) throws IOException {
        return load(file.openStream());
    }

    public long[][][] load(File file) {
        return load(PngHelperInternal.istreamFromFile(file));
    }

    public long[][][] load(InputStream stream) {
        long[][][] terrain = new long[depth][height][width];
        try (var reader = new PngReader(stream)) {
            int channels = reader.imgInfo.channels;
            if (channels < 3 || reader.imgInfo.bitDepth != 8) {
                throw new RuntimeException("This method is for RGB8/RGBA8 images");
            }
            readImage(reader, channels, terrain);
        }
        return terrain;
    }

    private void readImage(PngReader reader, int channels, long[][][] terrain) {
        int x = 0, y = 0, z = 0, zoff = 0;
        for (int r = 0; r < reader.imgInfo.rows; r++) {
            var line = (ImageLineInt) reader.readRow();
            for (int c = 0, j = 0; c < reader.imgInfo.cols; c++) {
                int[] scanline = line.getScanline();
                int red = scanline[j++];
                int green = scanline[j++];
                int blue = scanline[j++];
                if (channels > 3) {
                    j++;
                }
                long id = convert2Id(red, green, blue);
                terrain[z + zoff][y][x++] = id;
                if (x == width) {
                    x = 0;
                    z++;
                }
            }
            x = 0;
            z = 0;
            y++;
            if (y > 0 && y % height == 0) {
                y = 0;
                zoff += columns;
            }
        }
    }

    public static long convert2Id(int r, int g, int b) {
        return r + (g << 8) + (b << 16);
    }

}
