package edu.byu.cs.tweeter.server.dao;

public interface S3Interface {
    String uploadImage(byte[] image, String alias);
}
