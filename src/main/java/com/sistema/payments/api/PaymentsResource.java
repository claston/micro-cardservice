package com.sistema.payments.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.common.tenant.TenantResolver;
import com.sistema.payments.api.dto.CreatePixChargeRequest;
import com.sistema.payments.api.dto.CreatePixChargeResponse;
import com.sistema.payments.api.dto.CreatePixPayoutRequest;
import com.sistema.payments.api.dto.CreatePixPayoutResponse;
import com.sistema.payments.api.dto.PaymentResponse;
import com.sistema.payments.application.CreatePixChargeUseCase;
import com.sistema.payments.application.CreatePixPayoutUseCase;
import com.sistema.payments.application.GetPaymentByReferenceUseCase;
import com.sistema.payments.application.GetPaymentUseCase;
import com.sistema.payments.application.ProcessPspWebhookUseCase;
import com.sistema.payments.application.WebhookSignatureValidator;
import com.sistema.payments.application.command.CreatePixChargeCommand;
import com.sistema.payments.application.command.CreatePixPayoutCommand;
import com.sistema.payments.application.exception.PaymentsUnauthorizedException;
import com.sistema.payments.application.exception.PaymentsWebhookUnauthorizedException;
import com.sistema.payments.application.model.PspWebhookEvent;
import com.sistema.payments.domain.model.Payment;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.UUID;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentsResource {
    @Inject
    TenantResolver tenantResolver;

    @Inject
    CreatePixChargeUseCase createPixChargeUseCase;

    @Inject
    CreatePixPayoutUseCase createPixPayoutUseCase;

    @Inject
    ProcessPspWebhookUseCase processPspWebhookUseCase;

    @Inject
    GetPaymentUseCase getPaymentUseCase;

    @Inject
    GetPaymentByReferenceUseCase getPaymentByReferenceUseCase;

    @Inject
    WebhookSignatureValidator signatureValidator;

    @Inject
    ObjectMapper objectMapper;

    @POST
    @Path("/pix/charges")
    public Response createCharge(@HeaderParam("X-API-Key") String apiKey,
                                 @HeaderParam("Idempotency-Key") String idempotencyKey,
                                 @Valid CreatePixChargeRequest request) {
        UUID tenantId = requireTenantId(apiKey);
        String resolvedIdempotency = resolveIdempotencyKey(idempotencyKey, request.getReferenceId());
        var command = new CreatePixChargeCommand(
                request.getReferenceType(),
                request.getReferenceId(),
                request.getAmountMinor(),
                request.getCurrency(),
                request.getPayer().getName(),
                request.getPayer().getDocument(),
                request.getCreditToWalletAccountId(),
                resolvedIdempotency
        );
        var result = createPixChargeUseCase.execute(tenantId, command);
        CreatePixChargeResponse response = new CreatePixChargeResponse();
        response.setPaymentId(result.getPaymentId().toString());
        response.setStatus(result.getStatus());
        response.setExternalPaymentId(result.getExternalPaymentId());
        response.setTxid(result.getTxid());
        response.setQrCode(result.getQrCode());
        response.setCopyPaste(result.getCopyPaste());
        response.setExpiresAt(result.getExpiresAt());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/pix/payouts")
    public Response createPayout(@HeaderParam("X-API-Key") String apiKey,
                                 @HeaderParam("Idempotency-Key") String idempotencyKey,
                                 @Valid CreatePixPayoutRequest request) {
        UUID tenantId = requireTenantId(apiKey);
        String resolvedIdempotency = resolveIdempotencyKey(idempotencyKey, request.getReferenceId());
        var command = new CreatePixPayoutCommand(
                request.getReferenceType(),
                request.getReferenceId(),
                request.getAmountMinor(),
                request.getCurrency(),
                request.getPixKey(),
                request.getDebitFromWalletAccountId(),
                request.getDescription(),
                resolvedIdempotency
        );
        var result = createPixPayoutUseCase.execute(tenantId, command);
        CreatePixPayoutResponse response = new CreatePixPayoutResponse();
        response.setPaymentId(result.getPaymentId().toString());
        response.setStatus(result.getStatus());
        response.setExternalPaymentId(result.getExternalPaymentId());
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/webhooks/psp")
    public Response processWebhook(@HeaderParam("X-Signature") String signature,
                                   String rawBody) {
        String body = rawBody == null ? "" : rawBody;
        if (!signatureValidator.isValid(signature, body)) {
            throw new PaymentsWebhookUnauthorizedException("invalid webhook signature");
        }
        PspWebhookEvent event = parseWebhookEvent(body);
        processPspWebhookUseCase.execute(event);
        return Response.ok().build();
    }

    @GET
    @Path("/{paymentId}")
    public PaymentResponse getPayment(@HeaderParam("X-API-Key") String apiKey,
                                      @PathParam("paymentId") String paymentId) {
        UUID tenantId = requireTenantId(apiKey);
        Payment payment = getPaymentUseCase.execute(tenantId, parsePaymentId(paymentId));
        return toResponse(payment);
    }

    @GET
    @Path("/by-reference")
    public PaymentResponse getPaymentByReference(@HeaderParam("X-API-Key") String apiKey,
                                                 @QueryParam("referenceType") String referenceType,
                                                 @QueryParam("referenceId") String referenceId) {
        UUID tenantId = requireTenantId(apiKey);
        if (referenceType == null || referenceType.isBlank()) {
            throw new IllegalArgumentException("referenceType is required");
        }
        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException("referenceId is required");
        }
        Payment payment = getPaymentByReferenceUseCase.execute(tenantId, referenceType, referenceId);
        return toResponse(payment);
    }

    private UUID requireTenantId(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new PaymentsUnauthorizedException("apiKey is required");
        }
        return tenantResolver.resolveTenantId(apiKey)
                .orElseThrow(() -> new PaymentsUnauthorizedException("apiKey not recognized"));
    }

    private UUID parsePaymentId(String paymentId) {
        try {
            return UUID.fromString(paymentId);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("paymentId must be a valid UUID");
        }
    }

    private String resolveIdempotencyKey(String idempotencyKey, String referenceId) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return referenceId;
        }
        return idempotencyKey;
    }

    private PspWebhookEvent parseWebhookEvent(String rawBody) {
        try {
            return objectMapper.readValue(rawBody, PspWebhookEvent.class);
        } catch (IOException ex) {
            throw new IllegalArgumentException("invalid webhook payload");
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId().toString());
        response.setStatus(payment.getStatus().name());
        response.setType(payment.getType().name());
        response.setAmountMinor(payment.getAmountMinor());
        response.setCurrency(payment.getCurrency());
        response.setReferenceType(payment.getReferenceType());
        response.setReferenceId(payment.getReferenceId());
        response.setExternalProvider(payment.getExternalProvider());
        response.setExternalPaymentId(payment.getExternalPaymentId());
        response.setTxid(payment.getExternalTxid());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        response.setConfirmedAt(payment.getConfirmedAt());
        response.setFailureReason(payment.getFailureReason());
        response.setWalletFromAccountId(payment.getWalletFromAccountId() == null ? null : payment.getWalletFromAccountId().toString());
        response.setWalletToAccountId(payment.getWalletToAccountId() == null ? null : payment.getWalletToAccountId().toString());
        response.setLedgerTransactionId(payment.getLedgerTransactionId() == null ? null : payment.getLedgerTransactionId().toString());
        return response;
    }
}
