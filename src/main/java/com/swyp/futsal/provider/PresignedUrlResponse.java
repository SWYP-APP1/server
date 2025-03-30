package com.swyp.futsal.provider;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PresignedUrlResponse {
    private String url; // presigned URL
    private String uri; // NCP URI (ncp://{bucket}/{objectKey})
}