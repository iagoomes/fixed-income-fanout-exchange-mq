package br.com.iagoomes.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixedIncomeEventDto implements Serializable {

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("old_rate")
    private Double oldRate;

    @JsonProperty("new_rate")
    private Double newRate;

    @JsonProperty("old_price")
    private Double oldPrice;

    @JsonProperty("new_price")
    private Double newPrice;

    @JsonProperty("effective_date")
    private String effectiveDate;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("description")
    private String description;

    @JsonProperty("severity")
    private String severity;

    @JsonProperty("event_id")
    private String eventId;

}
