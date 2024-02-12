package com.merchant.transactions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Generated;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"customerEmail", "customerPhone", "merchant", "status", "reference"})
@Generated("jsonschema2pojo")
public class AuthorizeTransactionDto extends ApprovedTransactionDto {
    public AuthorizeTransactionDto() {super();}
    @Email
    @NotEmpty
    @JsonProperty("customerEmail")
    private String customerEmail;
    @JsonProperty("customerPhone")
    private String customerPhone;
    private List<ApprovedTransactionDto> approvedReferenceBy;


    public void setApprovedReferenceBy(ApprovedTransactionDto approvedTransactionDto) {
        if (approvedReferenceBy == null) {
            approvedReferenceBy = new ArrayList<>();
        }
        approvedReferenceBy.add(approvedTransactionDto);
    }

    public AuthorizeTransactionDto populateCreatedDate() {
        super.populateCreatedDate();
        return this;
    }
}
