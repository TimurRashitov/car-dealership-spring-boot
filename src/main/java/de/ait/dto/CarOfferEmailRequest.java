package de.ait.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class CarOfferEmailRequest {

    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Client name is mandatory")
    private String clientName;

    @NotNull (message = "Car ID is mandatory")
    private Long carId;

    @Positive(message = "Offer price must be positive")
    private Integer offerPrice;
}
