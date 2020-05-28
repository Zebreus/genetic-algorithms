package Visualization;

import MainClasses.Config;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.media.MediaLocator;

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
                ImageIO.write(outputImage, "jpg", f);
            }
        }

        return widthHeight;
    }

    // MainClasses.Main function
    public static void createVideo(Properties properties, int maxH, int maxW) throws IOException {
        String imagesPath = properties.getProperty("imageSequencePath");
        String videoPathAndFile = properties.getProperty("videoPathAndFile");
        int imgInterval = Integer.parseInt(properties.getProperty("imgInterval"));
        dir = new File(imagesPath);

        int[] widthHeight = VideoCreator.resizeImages(imagesPath, maxH, maxW);

        File file = new File(videoPathAndFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        Vector<String> imgLst = new Vector<>();
        if (dir.isDirectory()) {
            int counter = 1;
            for (final File f : dir.listFiles(imageFilter)) {
                imgLst.add(f.getAbsolutePath());

            }
        }
        makeVideo("file:\\" + file.getAbsolutePath(), imgLst, widthHeight[0], widthHeight[1], imgInterval);
    }

    public static void makeVideo(String fileName, Vector imgLst, int width, int height, int interval) throws MalformedURLException {
        JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
        MediaLocator oml;
        if ((oml = JpegImagesToMovie.createMediaLocator(fileName)) == null) {
            System.err.println("Cannot build media locator from: " + fileName);
            System.exit(0);
        }
        imageToMovie.doIt(width, height, (1000 / interval), imgLst, oml);
    }
}