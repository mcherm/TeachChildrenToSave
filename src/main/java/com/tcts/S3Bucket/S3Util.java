package com.tcts.S3Bucket;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.tcts.common.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * A component containing some utility functions for working with
 * the S3 bucket that this application is configured (in
 * application.properties) to use.
 */
@Component
public class S3Util {

    @Autowired
    private Configuration configuration;

    private AmazonS3Client amazonS3Client;
    private String bucketName;

    @PostConstruct
    private void init() {
        AWSCredentials credentials = new BasicAWSCredentials(
                configuration.getProperty("aws.access_key"),
                configuration.getProperty("aws.secret_access_key"));
        amazonS3Client = new AmazonS3Client(credentials);
        bucketName = configuration.getProperty("bucketName");
    }

    /**
     * Retrieves the complete set of names of all documents that
     * are stored in the bucket.
     */
    public Set<String> getAllDocuments(){
        Set<String> files = new HashSet<String>();
        ObjectListing objectListing = amazonS3Client.listObjects(bucketName);
        for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
            String filename = s3ObjectSummary.getKey();
            files.add(filename);
        }
        return files;
    }

    /**
     * Deletes the given document from the bucket if it exists, does
     * nothing if the name does not exist.
     */
    public void deleteDocument(String documentName){
        amazonS3Client.deleteObject(bucketName, documentName);
    }

    /**
     * Given the name of a document in the S3 bucket, this will return (as
     * a String) the URL needed to download it.
     */
    public String makeS3URL(String documentName){
       return amazonS3Client.getResourceUrl(bucketName, documentName);
    }
}
