package br.com.iagoomes.application.controller;

import br.com.iagoomes.application.service.FixedIncomeEventService;
import br.com.iagoomes.domain.dto.FixedIncomeEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class FixedIncomeEventController {

    private final FixedIncomeEventService eventService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Producer API is running!");
    }

    @PostMapping("/rate-change")
    public ResponseEntity<String> publishRateChange(@RequestBody FixedIncomeEventDto event) {
        try {
            eventService.publishRateChangeEvent(event);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Rate change event published successfully");
        } catch (Exception e) {
            log.error("Error publishing rate change event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/pricing-update")
    public ResponseEntity<String> publishPricingUpdate(@RequestBody FixedIncomeEventDto event) {
        try {
            eventService.publishPricingUpdateEvent(event);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Pricing update event published successfully");
        } catch (Exception e) {
            log.error("Error publishing pricing update event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> publishGenericEvent(@RequestBody FixedIncomeEventDto event) {
        try {
            eventService.publishGenericEvent(event);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Event broadcasted successfully to all consumers");
        } catch (Exception e) {
            log.error("Error publishing generic event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

}
