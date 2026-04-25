package com.trial.dvoc.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ImageService {

    private final Cloudinary cloudinary;

    public ImageService(
            Cloudinary cloudinary){
        this.cloudinary=cloudinary;
    }
    public String upload(MultipartFile file)throws Exception{
        Map result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder","coupay_profiles/users") );

        return result.get("secure_url").toString();

    }
}