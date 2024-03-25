package com.tcts.S3Bucket;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.tcts.common.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import com.amazonaws.services.s3.model.ObjectMetadata;

import static com.amazonaws.services.s3.model.CryptoStorageMode.ObjectMetadata;

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
     */
    public String makeS3URL(String fileName){
       return amazonS3Client.getResourceUrl(bucketName, fileName);
    }

    /**
     * Uploads a document of type MultipartFile to the amazon S3 bucket specified in the application.properties file
     * @param document - contains document to be uploaded
     * @throws IOException - if something went wrong with reading the Input Stream from the MultipartFile
     * @throws SdkClientException - if something went wrong with uploading the file to Amazon
     */
    public void upLoadDocument(MultipartFile document)  throws IOException, SdkClientException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(document.getSize());
        metadata.setContentType(document.getContentType());

        amazonS3Client.putObject(new PutObjectRequest(bucketName, document.getOriginalFilename(), document.getInputStream(), metadata));

    }
}
