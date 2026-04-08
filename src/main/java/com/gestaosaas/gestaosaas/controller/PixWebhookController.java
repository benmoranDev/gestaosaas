package com.gestaosaas.gestaosaas.controller;

import com.gestaosaas.gestaosaas.dto.PixWebhookRequest;
import com.gestaosaas.gestaosaas.service.PixService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pix/webhook")
public class PixWebhookController {

    private final PixService pixService;

    public PixWebhookController(PixService pixService) {
        this.pixService = pixService;
    }

    // Endpoint que o PSP usará para confirmar pagamentos Pix.
    @PostMapping
    public ResponseEntity<Void> receberWebhook(
            @RequestBody PixWebhookRequest request,
            @RequestHeader(name = "X-Webhook-Token", required = false) String token) {

        pixService.processarWebhook(request, token);
        return ResponseEntity.ok().build();
    }
}