package com.trial.dvoc.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
public class AiImportController {

    @PostMapping("/ai-import")
    public Map<String,String> importCoupon(
            @RequestParam String raw){

        String prompt=
                """
                Extract as JSON:
                brand,
                description,
                couponCode,
                category,
                expiryDate
                
                Text:
                """+raw;


        Map req=
                Map.of(
                        "inputs",
                        prompt
                );

        RestTemplate rt=
                new RestTemplate();

        String api=
                "https://api-inference.huggingface.co/models/google/flan-t5-large";

        Map res=
                rt.postForObject(
                        api,
                        req,
                        Map.class
                );

        /* fallback simple parse */
        Map<String,String> out=
                new HashMap<>();

        if(raw.contains("Cleartrip")){
            out.put("brand","Cleartrip");
            out.put("category","Travel");
        }

        if(raw.contains("MakeMyTrip")){
            out.put("brand","MakeMyTrip");
            out.put("category","Travel");
        }

        out.put(
                "description",
                raw.split("\n")[1]
        );

        return out;
    }
}