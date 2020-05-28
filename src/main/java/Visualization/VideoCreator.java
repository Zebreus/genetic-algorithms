package Visualization;

import MainClasses.Config;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

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

    private static int[] resizeImages(String imagesPath, int maxH, int maxW) throws IOException {
        dir = new File(imagesPath);
        int[] widthHeight = new int[2];

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

            widthHeight[0] = maxWidth;
            widthHeight[1] = maxHeight;

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

        return widthHeight;
    }

    // MainClasses.Main function
    public static void createVideo(String imagesPath, String videoPathAndFile,
                                   int imgFps, int maxH, int maxW) {
        try {
            int[] widthHeight = VideoCreator.resizeImages(imagesPath, maxH, maxW);

            File videoFile = new File(videoPathAndFile);
            if (!videoFile.exists()) {
                videoFile.createNewFile();
            }
            Vector<String> imgLst = new Vector<>();

            makeVideo(videoFile, imagesPath, imgFps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeVideo(File videoFile, String imagesDir, int fps) throws MalformedURLException {
        try {
            JCodecPNGtoMP4.generateVideoBySequenceImages(videoFile, imagesDir, "png", fps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}