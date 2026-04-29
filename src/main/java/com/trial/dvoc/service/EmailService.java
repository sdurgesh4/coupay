package com.trial.dvoc.service;

import org.springframework.stereotype.Service;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    public void sendOtp( String to, String otp )throws Exception{

        String apiKey= System.getenv("BREVO_API_KEY");

        String from=System.getenv("BREVO_FROM" );

        String json= """
            {
                "sender":{
                "email":"%s"
                },
                "to":[
                {
                "email":"%s"
                }
                ],
                "subject":"Coupay Password Reset OTP",
                "htmlContent":
                "<h2>Your OTP is %s</h2><p>Valid for 10 mins</p>"
                }
            """.formatted(
                        from,
                        to,
                        otp
                );

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
                                HttpRequest.BodyPublishers.ofString(
                                        json
                                )
                        ).build();

        HttpClient
                .newHttpClient()
                .send(
                        request,
                        HttpResponse
                                .BodyHandlers
                                .ofString()
                );

    }

}