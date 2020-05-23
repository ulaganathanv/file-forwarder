package com.sample.service;

import java.util.List;
import java.io.File;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;

import com.amazonaws.SdkClientException;
import com.amazonaws.AmazonServiceException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class AmazonClient {

    private AmazonS3 s3client;

    @Value("${aws.accessKeyId}")
    private String accessKey;
    @Value("${aws.secretAccessKey}")
    private String secretKey;

    public void listBuckets() {
        List<Bucket> buckets = s3client.listBuckets();
        System.out.println("Your Amazon S3 buckets are : ");
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        }
    }

    public void uploadObject(String bucketName, String fileObjKeyName, String fileName) {
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("title", "someTitle");
            request.setMetadata(metadata);
            s3client.putObject(request);
        }
        catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void initializeAmazon() {

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }
}