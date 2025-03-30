package com.swyp.futsal.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Provider {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일 업로드를 위한 presigned URL 생성
     * 
     * @param directory 저장될 디렉토리 경로 (예: "images/profile")
     */
    public PresignedUrlResponse getUploadPresignedUrl(String directory) {
        String objectKey = createObjectKey(directory);
        String ncpUri = String.format("ncp://%s/%s", bucket, objectKey);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .putObjectRequest(objectRequest)
                .build();

        String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
        return new PresignedUrlResponse(presignedUrl, ncpUri);
    }

    /**
     * 파일 다운로드를 위한 presigned URL 생성
     * 
     * @param objectKey 객체 키
     * @return 생성된 presigned URL과 NCP URI
     */
    public Optional<PresignedUrlResponse> getDownloadPresignedUrl(String ncpUri) {
        if (ncpUri == null) {
            return Optional.empty();
        }
        String objectKey = getObjectKey(ncpUri);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest(getObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignGetObject(presignRequest).url().toString();
        return Optional.of(new PresignedUrlResponse(presignedUrl, ncpUri));
    }

    private String createObjectKey(String directory) {
        return directory + "/" + UUID.randomUUID().toString();
    }

    private String getObjectKey(String ncpUri) {
        String[] parts = ncpUri.split("ncp://")[1].split("/", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("INVALID_URI_FORMAT");
        }
        return parts[1];
    }
}