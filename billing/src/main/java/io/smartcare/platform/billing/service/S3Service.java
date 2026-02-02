package io.smartcare.platform.billing.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Template s3Template;

    @Value("${application.bucket.name:smarthealth-billing-invoices}")
    private String bucketName;

    /**
     * Uploads an invoice document to AWS S3.
     * 
     * @param appointmentId the ID of the appointment associated with the invoice
     * @param content the content of the invoice to be uploaded
     */
    public void uploadInvoice(Long appointmentId, String content) {
        String key = "invoices/invoice-" + appointmentId + ".txt";
        
        // Ensure bucket exists (LocalStack ephemeral nature)
        if (!s3Template.bucketExists(bucketName)) {
            s3Template.createBucket(bucketName);
            log.info("Bucket {} created.", bucketName);
        }

        s3Template.upload(bucketName, key, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        log.info("Invoice uploaded to S3: {}/{}", bucketName, key);
    }
}
