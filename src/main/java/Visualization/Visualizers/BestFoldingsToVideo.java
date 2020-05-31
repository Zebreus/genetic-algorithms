package Visualization.Visualizers;

import Interfaces.Visualizer;
import MainClasses.Candidate;
import MainClasses.Config;
import MainClasses.GeneticAlgorithm;
import Visualization.JCodecPNGtoMP4;
import Visualization.VideoCreator;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;

public class BestFoldingsToVideo implements Visualizer {

  Config config;
  private final int MAXIMUM_SIZE;
  private final FilenameFilter imageFilter;

  public BestFoldingsToVideo(Config config){
    this.config = config;
    MAXIMUM_SIZE = 3000;
    imageFilter = new FilenameFilter() {
      @Override
      public boolean accept(final File dir, String name) {
        for (final String ext : new String[]{"jpg", "png"}) {
          if (name.endsWith("." + ext) && name.startsWith(config.getJobName())) {
            return (true);
          }
        }
        return (false);
      }
    };
    try {
      Files.createDirectories(Paths.get(config.getVideoDirectory()));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  @Override
  public void drawProtein(Candidate[] generation, GeneticAlgorithm geneticAlgorithm) {
    //TODO Only render video for the last generation
    if(geneticAlgorithm.generation == config.getTotalGenerations()-2){
      String filename = config.getLogfileDirectory() + "/" + config.getJobName() + ".mp4";
      createVideo(
          config.getImageSequenceDirectory(),
          filename,
          config.getImageFps(),
          config.getImagesToFpsIncrease(),
          config.getImageFpsMax(),
          geneticAlgorithm.getMaxH(),
          geneticAlgorithm.getMaxW(),
          config.isZoom()
      );
    }
  }

  public void createVideo(String imagesPath, String videoPathAndFile,
      int imgFps, int imgToFpsIncrease, int maxFps, int maxH, int maxW, boolean zoom) {
    try {
      System.err.println("\nStarting image resizing");
      if (zoom) {
        resizeImagesWithZoom(imagesPath);
      } else {
        resizeImages(imagesPath, maxH, maxW);
      }

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

  private void resizeImages(String imagesPath, int maxH, int maxW) throws IOException {
    File dir = new File(imagesPath);

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
        //TODO The following two lines should be unnecessary
        //g2d.setColor(Config.imageBackground);
        //g2d.fillRect(0,0, maxWidth, maxHeight);
        g2d.drawImage(inputImage, 0, 0, inputImage.getWidth(), inputImage.getHeight(), null);
        g2d.dispose();

        // overwrites image
        ImageIO.write(outputImage, "png", f);
      }
    }
  }

  private void resizeImagesWithZoom(String imagesPath) throws IOException {
    File dir = new File(imagesPath);

    int[] maxHeightAfterIndex = new int[dir.listFiles(imageFilter).length];
    int[] maxWidthAfterIndex = new int[dir.listFiles(imageFilter).length];

    if (dir.isDirectory()) {

      Path directoryPath = Paths.get(new File(imagesPath).toURI());
      DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, "*.png");

      List<File> filesList = new ArrayList<>();
      for (Path path : stream) {
        filesList.add(path.toFile());
      }
      File[] files = new File[filesList.size()];
      filesList.toArray(files);

      JCodecPNGtoMP4.sortByNumber(files);
      List<Object> list = Arrays.asList(files);
      Collections.reverse(list);
      list.toArray(files);

      // reads input images and determines maximum required size after that particular image
      int currentMaxHeight = 0;
      int currentMaxWidth = 0;
      int counter = files.length - 1;
      for (File f : files) {
        BufferedImage inputImage = ImageIO.read(f);
        if (currentMaxHeight < inputImage.getHeight()) {
          currentMaxHeight = inputImage.getHeight();
        }
        if (currentMaxWidth < inputImage.getWidth()) {
          currentMaxWidth = inputImage.getWidth();
        }
        maxHeightAfterIndex[counter] = currentMaxHeight;
        maxWidthAfterIndex[counter] = currentMaxWidth;
        counter--;
      }

      // Resizes all images
      counter = files.length - 1;
      for (final File f : files) {
        BufferedImage inputImage = ImageIO.read(f);

        // creates output image
        BufferedImage outputImage = new BufferedImage(maxWidthAfterIndex[0], // Total maximum W/H
            maxHeightAfterIndex[0], inputImage.getType());

        // draws input image to the top left corner
        Graphics2D g2d = outputImage.createGraphics();
        //TODO The following two lines should be unnecessary
        //g2d.setColor(Config.imageBackground);
        //g2d.fillRect(0,0, maxWidthAfterIndex[0], maxHeightAfterIndex[0]);

        int newHeight = 0;
        int newWidth = 0;

        // Width expansion to border if height fits accordingly
        double expansionByWidth = (double) maxWidthAfterIndex[0] / maxWidthAfterIndex[counter];
        if (maxHeightAfterIndex[counter] * expansionByWidth <= maxHeightAfterIndex[0]) {
          newWidth = (int) Math.floor(inputImage.getWidth() * expansionByWidth);
          newHeight = (int) Math.floor(inputImage.getHeight() * expansionByWidth);
        } else {
          // otherwise height expansion to border and width according to ratio
          double expansionByHeight = (double) maxHeightAfterIndex[0] / maxHeightAfterIndex[counter];
          newWidth = (int) Math.floor(inputImage.getWidth() * expansionByHeight);
          newHeight = (int) Math.floor(inputImage.getHeight() * expansionByHeight);
        }

        g2d.drawImage(inputImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        // overwrites image
        ImageIO.write(outputImage, "png", f);

        counter--;
      }
    }
  }
}
