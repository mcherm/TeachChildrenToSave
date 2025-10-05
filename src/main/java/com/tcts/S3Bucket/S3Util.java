package com.tcts.S3Bucket;

import com.tcts.common.Configuration;
import com.tcts.common.SitesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import java.time.Duration;


/**
 * A component containing some utility functions for working with
 * the S3 bucket that this application is configured (in
 * application.properties) to use.
 */
@Component
public class S3Util {

    @Autowired
    private Configuration configuration;

    @Autowired
    private SitesConfig sitesConfig;

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private String bucketName; // the bucketname that the documents are stored in.  This is initialized upon object creation

    /**
     * Reads the authentication info and Amazon S3 bucket name from
     * the application.properties file and initializes them.  This
     * function is called by spring after all Autowired
     */
    @PostConstruct
    private void init() {
        String accessKey = configuration.getProperty("aws.access_key");
        String accessSecret = configuration.getProperty("aws.secret_access_key");
        Region region = Region.US_EAST_1;
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, accessSecret);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
        s3Presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
        bucketName = "teachchildrentosave-documents";
    }

    /**
     * This obtains the "folder" prefix for the current site and environment.
     * This prefix should be added to all documents read or written.
     */
    private String getFolderName() {
        final String site = sitesConfig.getSite();
        final String env = configuration.getProperty("dynamoDB.environment");
        return site + "/" + env + "/";
    }

    /**
     * Retrieves the complete set of names of all documents that are in S3 in the folder for
     * the current site and environment.
     */
    public Set<String> getAllDocuments() {
        final Set<String> files = new HashSet<>();
        final String folderName = getFolderName();
        final ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(bucketName)
                .prefix(folderName)
                .build();
        final ListObjectsResponse response = s3Client.listObjects(listObjectsRequest);
        assert !response.isTruncated();  // no WAY there will be too many for a single call
        for (final S3Object s3Object : response.contents()) {
            final String key = s3Object.key();
            assert key.startsWith(folderName);
            final String filename = key.substring(folderName.length());
            if (filename.length() == 0) {
                continue; // ignore when we get the directory itself
            }
            files.add(filename);
        }
        return files;
    }

    /**
     * Deletes the given document from the bucket if it exists, does
     * nothing if the name does not exist.
     */
    public void deleteDocument(String documentName) {
        final String s3ObjectName = getFolderName() + documentName;
        final DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3ObjectName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    /**
     * Given the name of a document in the S3 bucket, this will return a URL that can be
     * used (by browsers and stuff) for downloading this document.
     */
    public String makeS3URL(String documentName) {
        final String s3ObjectName = getFolderName() + documentName;
        final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3ObjectName)
                .build();
        final GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    /**
     * Uploads a document of type MultipartFile to the amazon S3 bucket specified in the application.properties
     * file, in a sub-folder defined by the current site and environment.
     *
     * @param document - contains document to be uploaded
     * @throws IOException - if something went wrong with reading the Input Stream from the MultipartFile
     * @throws SdkException - if something went wrong with uploading the file to Amazon
     */
    public void uploadDocument(MultipartFile document)  throws IOException {
        final String s3ObjectName = getFolderName() + document.getOriginalFilename();
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3ObjectName)
                .contentType(document.getContentType())
                .contentLength(document.getSize())
                .build();
        final RequestBody requestBody = RequestBody.fromInputStream(document.getInputStream(), document.getSize());
        s3Client.putObject(putObjectRequest, requestBody); // throws SdkException on failure
    }
}
