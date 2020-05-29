package Visualization;

/**
 * Using NIO e JCodec to convert multiple sequence png images to mp4 video file
 * Copyright (C) 2019  Leonardo Pereira (www.leonardopereira.com.br)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Leonardo Pereira
 * 18/03/2019 23:01
 */
public class JCodecPNGtoMP4 {

    static void sortByNumber(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                return n1 - n2;
            }
            private int extractNumber(String name) {
                int i = 0;
                try {
                    int s = name.lastIndexOf('_')+1;
                    int e = name.lastIndexOf('.');
                    String number = name.substring(s, e);
                    i = Integer.parseInt(number);
                } catch(Exception e) {
                    i = 0; // if filename does not match the format then default to 0
                }
                return i;
            }
        });
    }

    static void generateVideoBySequenceImages(File videoFile, String pathImages, String imageExt,
                                              int fps, int imgToFpsIncrease, int maxFps) throws Exception {
        SeekableByteChannel out = null;
        try {
            out = NIOUtils.writableFileChannel(videoFile.getCanonicalPath());

            AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(maxFps, 1));

            Path directoryPath = Paths.get(new File(pathImages).toURI());

            int encodedImages = 0;

            if (Files.isDirectory(directoryPath)) {
                DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, "*." + imageExt);

                List<File> filesList = new ArrayList<>();
                for (Path path : stream) {
                    filesList.add(path.toFile());
                }
                File[] files = new File[filesList.size()];
                filesList.toArray(files);

                sortByNumber(files);

                int numberImagesWithSameFps = imgToFpsIncrease;
                System.out.println();
                for (File img : files) {
                    if (numberImagesWithSameFps <= 0) {
                        if (fps < maxFps) {
                            fps++; // Increase fps
                            numberImagesWithSameFps = imgToFpsIncrease - 1;
                        }
                    } else {
                        numberImagesWithSameFps--; // Countdown to increase
                    }

                    System.err.println("Encoding image " + img.getName() + " [at " + fps + " fps]");
                    // Generate the image
                    BufferedImage image = ImageIO.read(img);
                    // Encode the image often enough to fit current fps
                    for (int i = 0; i < Math.round((float) maxFps / fps); i++) {
                        encoder.encodeImage(image);
                        encodedImages++;
                    }
                    // Finalize the encoding, i.e. clear the buffers, write the header, etc.
                }
            }
            // Finalize the encoding, i.e. clear the buffers, write the header, etc.
            encoder.finish();
            System.err.println("Finished creating video: " + videoFile.getCanonicalPath());
            double videoLenght = (double) encodedImages / maxFps;
            System.out.printf("The final video is %.2f seconds long", videoLenght);

        } finally {
            NIOUtils.closeQuietly(out);
        }
    }
}