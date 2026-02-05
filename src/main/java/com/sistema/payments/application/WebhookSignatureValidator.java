package com.sistema.payments.application;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@ApplicationScoped
public class WebhookSignatureValidator {
    private final String secret;

    public WebhookSignatureValidator(@ConfigProperty(name = "payments.webhook.secret", defaultValue = "change-me") String secret) {
        this.secret = secret;
    }

    public boolean isValid(String signatureHeader, String body) {
        if (signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }
        String signature = signatureHeader.trim();
        if (signature.startsWith("sha256=")) {
            signature = signature.substring("sha256=".length());
        }
        String computed = hmacSha256Hex(body, secret);
        return MessageDigest.isEqual(computed.getBytes(StandardCharsets.UTF_8), signature.toLowerCase().getBytes(StandardCharsets.UTF_8));
    }

    private String hmacSha256Hex(String body, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
