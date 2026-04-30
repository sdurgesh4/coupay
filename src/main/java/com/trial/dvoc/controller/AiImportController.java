package com.trial.dvoc.controller;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.*;

@RestController
public class AiImportController {

    @PostMapping("/ai-import")
    public Map<String,String> importCoupon(
            @RequestParam String raw){

        Map<String,String> out=
                new HashMap<>();


        /* ---------- BRAND ---------- */

        if(raw.toLowerCase().contains("makemytrip")){
            out.put("brand","MakeMyTrip");
            out.put("category","Travel");
        }

        else if(raw.toLowerCase().contains("cleartrip")){
            out.put("brand","Cleartrip");
            out.put("category","Travel");
        }

        else if(raw.toLowerCase().contains("amazon")){
            out.put("brand","Amazon");
            out.put("category","Shopping");
        }



        /* ---------- DESCRIPTION ---------- */

        String[] lines=
                raw.split("\\r?\\n");

        for(String line:lines){

            line=line.trim();

            if(
                    line.contains("off")
                            && !line.toLowerCase()
                            .contains("voucher")
            ){
                out.put(
                        "description",
                        line
                );
                break;
            }
        }

        /* ---------- COUPON CODE ---------- */
        Matcher brand=
                Pattern.compile(
                        "may like this (.*?) voucher",
                        Pattern.CASE_INSENSITIVE
                ).matcher(raw);

        if(brand.find()){
            out.put(
                    "brand",
                    brand.group(1).trim()
            );
        }

        Matcher code=
                Pattern.compile(
                        "Voucher\\s*Code:\\s*(\\S+)",
                        Pattern.CASE_INSENSITIVE
                ).matcher(raw);

        if(code.find()){

            out.put(
                    "couponCode",
                    code.group(1)
            );

        }



        /* ---------- EXPIRY ---------- */

        Matcher exp=
                Pattern.compile(
                        "Expiring in\\s*(\\d+)\\s*days",
                        Pattern.CASE_INSENSITIVE
                ).matcher(raw);

        if(exp.find()){

            int days=
                    Integer.parseInt(
                            exp.group(1)
                    );

            out.put(
                    "expiryDate",
                    LocalDate.now()
                            .plusDays(days)
                            .toString()
            );

        }


        /* ---------- REDEEM URL ---------- */

        Matcher url=
                Pattern.compile(
                        "(https?://\\S+)"
                ).matcher(raw);

        if(url.find()){
            out.put(
                    "redeemNowUrl",
                    url.group(1)
            );

        }


        /* fallback if missing */
        out.putIfAbsent(
                "brand",
                "Other"
        );

        out.putIfAbsent(
                "category",
                "Shopping"
        );

        return out;

    }

}