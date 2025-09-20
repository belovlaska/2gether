package ru.ifmo.is.together.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
  private String host;
  private String username;
  private String password;
  private String bucketName;
  private String portApi;
  private String portConsole;
}
