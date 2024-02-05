package com.merchant.transactions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.merchant.transactions.model.enums.TransactionStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "amount", "author", "status", "reference"})
@Generated("jsonschema2pojo")
public class ApprovedTransactionDto {
    public ApprovedTransactionDto(){}
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("amount")
    private BigDecimal amount;
   // @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private TransactionStatus status;
    @JsonIgnore
    //@JsonProperty("status")
    private UUID reference;
    private LocalDateTime created;
    private String createdDateFormatted;
    public ApprovedTransactionDto populateCreatedDate() {
        createdDateFormatted = formatDate(created);
        return this;
    }

    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime != null ? dateTime.format(formatter) : null;
    }
}
