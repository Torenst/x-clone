package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.ByteArrayInputStream;

public class S3DAO implements S3Interface {

    private static final String S3BUCKET_NAME = "tweeter-image-storage";

    private final AmazonS3 s3 = AmazonS3ClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();

    @Override
    public String uploadImage(byte[] image, String alias) {

        ByteArrayInputStream stream = new ByteArrayInputStream(image);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.length);
        metadata.setContentType("image/jpeg");

        PutObjectRequest request = new PutObjectRequest(S3BUCKET_NAME, alias + ".png", stream, metadata).withCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult result = s3.putObject(request);

        return s3.getUrl(S3BUCKET_NAME, alias + ".png").toString();
    }
}
