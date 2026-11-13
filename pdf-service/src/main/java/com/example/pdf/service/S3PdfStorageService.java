package com.example.pdf.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3PdfStorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3PdfStorageService(
            S3Client s3Client,
            @Value("${app.aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void uploadPdf(String objectKey, byte[] pdfBytes) {
        var request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(pdfBytes)
        );
    }

    public byte[] downloadPdf(String objectKey) {
        var request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        return s3Client.getObjectAsBytes(request).asByteArray();
    }
}