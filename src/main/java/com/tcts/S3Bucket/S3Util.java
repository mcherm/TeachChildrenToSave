package com.tcts.S3Bucket;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
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
import com.amazonaws.services.s3.model.ObjectMetadata;

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

    private AmazonS3Client amazonS3Client;
    private String bucketName; // the bucketname that the documents are stored in.  This is initialized upon object creation

    /**
     * Reads the authentication info and Amazon S3 bucket name from
     * the application.properties file and initializes them.  This
     * function is called by spring after all Autowired
     */
    @PostConstruct
    private void init() {
        AWSCredentials credentials = new BasicAWSCredentials(
                configuration.getProperty("aws.access_key"),
                configuration.getProperty("aws.secret_access_key"));
        amazonS3Client = new AmazonS3Client(credentials);

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
        final ListObjectsV2Result result = amazonS3Client.listObjectsV2(bucketName, folderName);
        assert !result.isTruncated(); // no WAY there will be too many for a single call
        final List<S3ObjectSummary> objectSummaries = result.getObjectSummaries();
        for (final S3ObjectSummary objectSummary : objectSummaries) {
            final String key = objectSummary.getKey();
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
        amazonS3Client.deleteObject(bucketName, s3ObjectName);
    }

    /**
     * Given the name of a document in the S3 bucket, this will return a URL that can be
     * used (by browsers and stuff) for downloading this document.
     */
    public String makeS3URL(String documentName) {
        final String s3ObjectName = getFolderName() + documentName;
       return amazonS3Client.getResourceUrl(bucketName, s3ObjectName);
    }

    /**
     * Uploads a document of type MultipartFile to the amazon S3 bucket specified in the application.properties
     * file, in a sub-folder defined by the current site and environment.
     *
     * @param document - contains document to be uploaded
     * @throws IOException - if something went wrong with reading the Input Stream from the MultipartFile
     * @throws SdkClientException - if something went wrong with uploading the file to Amazon
     */
    public void uploadDocument(MultipartFile document)  throws IOException, SdkClientException {
        final String s3ObjectName = getFolderName() + document.getOriginalFilename();
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(document.getSize());
        metadata.setContentType(document.getContentType());

        amazonS3Client.putObject(new PutObjectRequest(bucketName, s3ObjectName, document.getInputStream(), metadata));
    }
}
