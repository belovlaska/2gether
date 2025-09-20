package ru.ifmo.is.together.common.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@RequiredArgsConstructor
@Configuration
public class MinioClientProvider {

  private final MinioProperties properties;

  @Bean
  public MinioClient getClient() {
    return MinioClient.builder()
      .endpoint(properties.getHost() + ":" + properties.getPortApi())
      .credentials(properties.getUsername(), properties.getPassword())
      .build();
  }

  @Bean
  public String bucket() {
    return properties.getBucketName();
  }
}
