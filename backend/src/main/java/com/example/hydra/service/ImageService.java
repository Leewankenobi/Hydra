package com.example.hydra.service;

import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.exception.SdkException;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import org.springframework.beans.factory.annotation.Value;

@Service
public class ImageService {

  private final S3Client s3;
  private final String bucket_name;

   private static final Map<String,String> ct = Map.of(
    "image/jpeg", "jpeg",
    "image/png", "png",
    "image/webp", "webp"
    );

  public ImageService(S3Client s3, @Value("${filebase.bucket}") String bucket_name){
    this.s3 = s3;
    this.bucket_name = bucket_name;
  }

  private String generateKey(String contentType){
      String myId = UUID.randomUUID().toString();
      String subtype = ct.get(contentType);

      if (subtype == null) {
        throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Can not find image subtype for key"
        );
      }
      String key = (myId + "." + subtype);
    return key;  
  }

  // returns key as String
  public String uploadImage(MultipartFile plantImage){
    String contentType = plantImage.getContentType(); 
    String key = generateKey(contentType);
    
    // s3 interface to upload image
    // build the request
    PutObjectRequest request = PutObjectRequest.builder()
      .bucket(bucket_name)
      .key(key)
      .contentType(contentType)
      .build();
    
    // send the request
    try {
      s3.putObject(
        request,
        RequestBody.fromInputStream(plantImage.getInputStream(), plantImage.getSize())
      );
    } catch (IOException e) {
      throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR,
      "The server could not read the plant image",
      e);
    } catch (SdkException s) {
      throw new ResponseStatusException(
        HttpStatus.BAD_GATEWAY,
        "Could not upload plant image",
        s
      );
    }
    return key;
  }

  public void deleteImage(String key) {

    DeleteObjectRequest delRequest = DeleteObjectRequest.builder()
      .bucket(bucket_name)
      .key(key)
      .build();

    try {
      s3.deleteObject(delRequest);
      
    } catch (SdkException e) {
      throw new ResponseStatusException(
        HttpStatus.BAD_GATEWAY,
        "Could not delete image",
        e
      );
    }
  }

}
