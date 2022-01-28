package com.revature.project3backend.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.revature.project3backend.models.Product;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles uploading a file to the S3 bucket.
 */
public class FileUtil {
	
	private static Logger log = Logger.getLogger (FileUtil.class);
	
	/**
	 * Uploads a file to the S3 bucket for the user.
	 *
	 * @param product The product object that the image belongs to.
	 * @param multipartFile The file that is uploaded.
	 * @return The url for the uploaded file.
	 */
	public static String uploadToS3 (Product product, MultipartFile multipartFile) {
		
		final String awsID = System.getenv ("AWS_PASS");
		final String secretKey = System.getenv ("AWS_SECRET_PASS");
		final String region = "us-east-2";
		final String bucketName = "jwa-p2";
		
		/*
		 * credentials
		 * */
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials (awsID, secretKey);
		
		/*
		 * s3 instance
		 * */
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard ().withRegion (Regions.fromName (region)).withCredentials (new AWSStaticCredentialsProvider (awsCredentials)).build ();
		
		String imageURL = "RCC/" + "products/" + product.getId ().toString () + "-" + multipartFile.getOriginalFilename ();
		imageURL = imageURL.replace (' ', '+');
		try {
			s3Client.putObject (new PutObjectRequest (bucketName, imageURL, multipartFile.getInputStream (), new ObjectMetadata ()));
			//prevents the filename from containing line characters
			multipartFile.getName().replaceAll("[\n\r\t]", "_");
			log.info (multipartFile.getName () + " has been uploaded to S3 bucket.");
		} catch (Exception e) {
			log.error (e);
		}
		return "https://jwa-p2.s3.us-east-2.amazonaws.com/" + imageURL;
	}
	
}
