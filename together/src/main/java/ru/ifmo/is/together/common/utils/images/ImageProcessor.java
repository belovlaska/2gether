package ru.ifmo.is.together.common.utils.images;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import org.apache.commons.io.FileUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import ru.ifmo.is.together.common.errors.BadFileExtensionError;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Component
public class ImageProcessor {

  private static final Logger logger = LoggerFactory.getLogger(ImageProcessor.class);

  public MagickImage createMagickImageFromBytes(String filename, byte[] imageData) throws MagickException {
    var imageInfo = new ImageInfo(filename);
    try {
      return new MagickImage(imageInfo, imageData);
    } catch (Exception e) {
      throw new MagickException("Failed to create MagickImage from byte array: " + e.getMessage());
    }
  }

  public ByteArrayInputStream save(MagickImage image, String contentType)
    throws MagickException, IOException, MimeTypeException {

    var tempFile = File.createTempFile(UUID.randomUUID().toString(), getImageExtension(contentType));
    tempFile.deleteOnExit();

    try {
      image.setFileName(tempFile.getAbsolutePath());
      boolean result = image.writeImage(new ImageInfo());
      if (!result) {
        throw new IOException("Cant write image to temp file");
      }
      image.destroyImages();

      var bais = new ByteArrayInputStream(FileUtils.readFileToByteArray(tempFile));
      tempFile.delete();

      return bais;
    } catch (Exception e) {
      tempFile.delete();
      throw new MagickException("Failed to process image: " + e.getMessage());
    }
  }

  public ByteArrayInputStream cropToSquare(MagickImage image, String contentType)
    throws MagickException, IOException, MimeTypeException {

    var tempFile = File.createTempFile(UUID.randomUUID().toString(), getImageExtension(contentType));
    tempFile.deleteOnExit();

    try {
      int width = image.getDimension().width;
      int height = image.getDimension().height;
      int squareSize = Math.min(width, height);

      int x = (width - squareSize) / 2;
      int y = (height - squareSize) / 2;
      image = image.cropImage(new Rectangle(x, y, squareSize, squareSize));

      if (squareSize > 500) {
        image = image.scaleImage(500, 500);
      }

      image.setFileName(tempFile.getAbsolutePath());
      boolean result = image.writeImage(new ImageInfo());
      if (!result) {
        throw new IOException("Cant write image to temp file");
      }
      logger.info(
        "Result image {}: Dimensions: {}, Size: {}",
        tempFile.getName(),
        image.getDimension().getWidth() + "x" + image.getDimension().getHeight(),
        Files.size(tempFile.toPath())
      );
      image.destroyImages();

      var bais = new ByteArrayInputStream(FileUtils.readFileToByteArray(tempFile));
      tempFile.delete();

      return bais;
    } catch (Exception e) {
      tempFile.delete();
      throw new MagickException("Failed to process image: " + e.getMessage());
    }
  }

  public boolean checkImage(byte[] imageData) throws IOException {
    if (imageData == null || imageData.length == 0) {
      throw new IllegalArgumentException("Uploaded file is empty or invalid");
    }

    try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
      BufferedImage img = ImageIO.read(bais);
      if (img == null) {
        throw new IOException("Uploaded file is not a valid image");
      }

      if (img.getWidth() < 1 || img.getHeight() < 1) {
        throw new IOException("Image dimensions are invalid");
      }
    } catch (IOException e) {
      throw new IOException("Failed to read the uploaded image: " + e.getMessage(), e);
    }

    return true;
  }

  public boolean checkContentType(String contentType) {
    final var imageContentTypes = List.of(
      MediaType.IMAGE_JPEG_VALUE,
      MediaType.IMAGE_PNG_VALUE,
      "image/webp"
    );

    if (contentType != null && imageContentTypes.contains(contentType)) {
      return true;
    }

    throw new BadFileExtensionError("File must be an image");
  }

  public String getImageExtension(String contentType) throws MimeTypeException {
    MimeType type = MimeTypes.getDefaultMimeTypes().forName(contentType);
    return type.getExtension();
  }
}
