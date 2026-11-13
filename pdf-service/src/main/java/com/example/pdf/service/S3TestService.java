/*

package com.example.pdf.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import java.nio.file.Path;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3TestService {

    private final S3Client s3Client;

    public S3TestService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void listFiles() {
        var request = ListObjectsV2Request.builder()
                .bucket("shipment-platform-pdf-development-029096971957-eu-north-1-an")
                .build();

        var response = s3Client.listObjectsV2(request);

        response.contents().forEach(object ->
                System.out.println(object.key() + " | " + object.size() + " bytes")
        );
    }


    public void uploadFile(Path filePath) {
        var request = PutObjectRequest.builder()
                .bucket("shipment-platform-pdf-development-029096971957-eu-north-1-an")
                .key(filePath.getFileName().toString())
                .contentType("application/pdf")
                .build();

        s3Client.putObject(request, RequestBody.fromFile(filePath));

        System.out.println("Uploaded: " + filePath.getFileName());
    }

}
*/
