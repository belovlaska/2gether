package ru.ifmo.is.together.storage;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import io.minio.*;
import org.springframework.stereotype.Service;
import ru.ifmo.is.together.common.config.MinioClientProvider;
import ru.ifmo.is.together.common.config.MinioInitializer;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class StorageService {

  private final MinioClientProvider provider;
  private final MinioInitializer minioInitializer;

  public byte[] getFile(String filename) throws Exception {
    MinioClient client = provider.getClient();
    InputStream obj = client.getObject(GetObjectArgs.builder()
      .object(filename)
      .bucket(provider.bucket())
      .build());
    return obj.readAllBytes();
  }

  public String getFileUrl(String filename) {
    if (filename == null) {
      return null;
    }

    var host = provider.getProperties().getHost() + ":" + provider.getProperties().getPortApi();
    var bucket = provider.bucket();
    return String.format("%s/%s/%s", host, bucket, filename);
  }

  public void create(String filename, String contentType, InputStream stream, Long size) throws Exception {
    MinioClient client = provider.getClient();
    client.putObject(
      PutObjectArgs.builder()
        .bucket(provider.bucket())
        .object(filename)
        .contentType(contentType)
        .stream(stream, size, -1)
        .build()
    );
  }

  public void delete(String filename) throws Exception {
    MinioClient client = provider.getClient();
    client.removeObject(
      RemoveObjectArgs.builder()
        .bucket(provider.bucket())
        .object(filename)
        .build()
    );
  }

  @PostConstruct
  public void initialize() {
    minioInitializer.createBucket();
  }
}
