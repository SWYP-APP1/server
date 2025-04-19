package com.swyp.futsal.provider;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Component
public class S3Provider {

    private final AmazonS3 s3Client;
    private final String bucket;

    public S3Provider(AmazonS3 s3Client, @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    /**
     * 파일 업로드를 위한 presigned URL 생성
     * 
     * @param directory 저장될 디렉토리 경로 (예: "images/profile")
     */
    public PresignedUrlResponse getUploadPresignedUrl(String directory) {
        String objectKey = createObjectKey(directory);
        String ncpUri = String.format("ncp://%s/%s", bucket, objectKey);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, objectKey)
                .withMethod(HttpMethod.PUT)
                .withExpiration(java.util.Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)));
        String presignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();

        return new PresignedUrlResponse(presignedUrl, ncpUri);
    }

    /**
     * 파일 다운로드를 위한 presigned URL 생성
     * 
     * @param ncpUri 객체 키 (ncp://버킷/경로 형식)
     * @return 생성된 presigned URL과 NCP URI
     */
    public Optional<PresignedUrlResponse> getDownloadPresignedUrl(String ncpUri) {
        if (ncpUri == null) {
            return Optional.empty();
        }
        String objectKey = getObjectKey(ncpUri);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, objectKey)
                .withMethod(HttpMethod.GET)
                .withExpiration(java.util.Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)));

        String presignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
        return Optional.of(new PresignedUrlResponse(presignedUrl, ncpUri));
    }

    private String createObjectKey(String directory) {
        return directory + "/" + UUID.randomUUID().toString();
    }

    private String getObjectKey(String ncpUri) {
        String prefix = "ncp://" + bucket + "/";
        if (!ncpUri.startsWith(prefix)) {
            throw new IllegalArgumentException("INVALID_URI_FORMAT or BUCKET_MISMATCH. URI: " + ncpUri + ", Expected Prefix: " + prefix);
        }
        return ncpUri.substring(prefix.length());
    }
}