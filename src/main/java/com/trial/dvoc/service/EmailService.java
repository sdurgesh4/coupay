package com.trial.dvoc.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    public void sendOtp(
            String to,
            String otp
    ) throws Exception {

        String apiKey =
                System.getenv("BREVO_API_KEY");

        String from =
                System.getenv("BREVO_FROM");


        if(apiKey == null || apiKey.isBlank()){
            throw new RuntimeException(
                    "BREVO_API_KEY missing"
            );
        }

        if(from == null || from.isBlank()){
            throw new RuntimeException(
                    "BREVO_FROM missing"
            );
        }


        String json =
                "{"
                        + "\"sender\":{"
                        + "\"email\":\""+from+"\""
                        + "},"

                        + "\"to\":["
                        + "{"
                        + "\"email\":\""+to+"\""
                        + "}"
                        + "],"

                        + "\"subject\":\"Coupay Password Reset OTP\","

                        + "\"htmlContent\":"
                        + "\"<h2>Your OTP is "
                        + otp
                        + "</h2>"
                        + "<p>Valid for 10 minutes.</p>\""

                        + "}";


        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.brevo.com/v3/smtp/email"
                                )
                        )
                        .header(
                                "accept",
                                "application/json"
                        )
                        .header(
                                "content-type",
                                "application/json"
                        )
                        .header(
                                "api-key",
                                apiKey
                        )
                        .POST(
                                HttpRequest
                                        .BodyPublishers
                                        .ofString(json)
                        )
                        .build();


        HttpResponse<String> response =
                HttpClient
                        .newHttpClient()
                        .send(
                                request,
                                HttpResponse.BodyHandlers.ofString()
                        );


        System.out.println(
                "BREVO STATUS = "
                        + response.statusCode()
        );

        System.out.println(
                "BREVO RESPONSE = "
                        + response.body()
        );


        if(response.statusCode()!=201){

            throw new RuntimeException(
                    "Brevo send failed -> "
                            + response.body()
            );
        }

    }

}