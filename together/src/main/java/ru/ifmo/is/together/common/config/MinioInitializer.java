package ru.ifmo.is.together.common.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Data
@RequiredArgsConstructor
@Configuration
public class MinioInitializer {

  private static final Logger logger = LoggerFactory.getLogger(MinioInitializer.class);
  private final MinioClientProvider provider;

  @PostConstruct
  public void createBucket() {
    try {
      if (provider.getClient().bucketExists(BucketExistsArgs.builder().bucket(provider.bucket()).build())) {
        logger.info("Bucket {} already exists, skipping creation", provider.bucket());
        return;
      }
      logger.warn("Bucket {} doesn't exist, creating it", provider.bucket());

      provider.getClient().makeBucket(MakeBucketArgs.builder().bucket(provider.bucket()).build());

      String policy = generateBucketPolicy(provider.bucket());

      provider.getClient().setBucketPolicy(
        SetBucketPolicyArgs.builder()
          .bucket(provider.bucket())
          .config(policy)
          .build()
      );
      logger.info("Bucket {} is now public", provider.bucket());
    } catch (Exception e) {
      logger.error("Failed to set bucket policy for {}", provider.bucket(), e);
    }

    logger.info("Bucket {} created", provider.bucket());
  }

  public String generateBucketPolicy(String bucketName) {
    return "{\n" +
      "    \"Version\": \"2012-10-17\",\n" +
      "    \"Statement\": [\n" +
      "        {\n" +
      "            \"Effect\": \"Allow\",\n" +
      "            \"Principal\": \"*\",\n" +
      "            \"Action\": [\n" +
      "                \"s3:GetObject\",\n" +
      "                \"s3:PutObject\"\n" +
      "            ],\n" +
      "            \"Resource\": \"arn:aws:s3:::" + bucketName + "/*\"\n" +
      "        }\n" +
      "    ]\n" +
      "}";
  }
}
