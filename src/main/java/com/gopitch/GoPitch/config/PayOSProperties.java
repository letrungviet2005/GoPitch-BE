package com.gopitch.GoPitch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "fourstars.payos")
@Getter
@Setter
public class PayOSProperties {
    private String clientId = "";
    private String apiKey = "";
    private String checksumKey = "";
    private String returnUrl = "http://localhost:5173/payment-success";
    private String cancelUrl = "http://localhost:5173/payment-failed";
    private String webhookUrl = "";
}
