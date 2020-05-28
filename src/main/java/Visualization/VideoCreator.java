package Visualization;

import MainClasses.Config;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class VideoCreator{

    private static final int MAXIMUM_SIZE = 3000;
    public static File dir = new File("./visualization/series"); // Default
    public static final String[] extensions = new String[]{"jpg", "png"};
    public static final FilenameFilter imageFilter = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, String name) {
            for (final String ext : extensions) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    private static void resizeImages(String imagesPath, int maxH, int maxW) throws IOException {
        System.err.println("\nStarting image resizing");

        dir = new File(imagesPath);

        if (dir.isDirectory()) {
            // reads input images and determines maximum required size
            int maxHeight = maxH;
            int maxWidth = maxW;

            if (maxHeight <= 0 || maxWidth <= 0) {
                int counter = 1;
                for (final File f : dir.listFiles(imageFilter)) {
                    BufferedImage inputImage = ImageIO.read(f);
                    if (maxHeight < inputImage.getHeight()) {
                        maxHeight = inputImage.getHeight();
                    }
                    if (maxWidth < inputImage.getWidth()) {
                        maxWidth = inputImage.getWidth();
                    }
                }
            }

            // Needed because video is not playable with bigger sizes, mostly a concern for straight line initialization
            if (maxWidth > MAXIMUM_SIZE) {
                maxWidth = MAXIMUM_SIZE;
            }
            if (maxHeight > MAXIMUM_SIZE) {
                maxHeight = MAXIMUM_SIZE;
            }

            // Resizes all images
            for (final File f : dir.listFiles(imageFilter)) {
                BufferedImage inputImage = ImageIO.read(f);

                // creates output image
                BufferedImage outputImage = new BufferedImage(maxWidth,
                        maxHeight, inputImage.getType());

                // draws input image to the top left corner
                Graphics2D g2d = outputImage.createGraphics();
                g2d.setColor(Config.imageBackground);
                g2d.fillRect(0,0, maxWidth, maxHeight);
                g2d.drawImage(inputImage, 0, 0, inputImage.getWidth(), inputImage.getHeight(), null);
                g2d.dispose();

                // overwrites image
                ImageIO.write(outputImage, "png", f);
            }
        }
    }

    // MainClasses.Main function
    public static void createVideo(String imagesPath, String videoPathAndFile,
                                   int imgFps, int imgToFpsIncrease, int maxFps, int maxH, int maxW) {
        try {
            VideoCreator.resizeImages(imagesPath, maxH, maxW);

            File videoFile = new File(videoPathAndFile);
            if (!videoFile.exists()) {
                videoFile.createNewFile();
            }

            try {
                JCodecPNGtoMP4.generateVideoBySequenceImages(videoFile, imagesPath, "png", imgFps, imgToFpsIncrease, maxFps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}