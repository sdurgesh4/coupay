package com.trial.dvoc.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.*;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(){

        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name",System.getenv("CLOUD_NAME"),
                        "api_key",System.getenv("CLOUD_KEY"),
                        "api_secret",System.getenv("CLOUD_SECRET"),
                        "secure",true
                ));
    }

}