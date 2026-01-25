package de.ait.model;

import de.ait.enums.CarStatus;
import de.ait.enums.FuelType;
import de.ait.enums.Transmission;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name ="cars")
@Getter
@Setter
@NoArgsConstructor

public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Brand must not be empty")
    private String brand;

    @Column(nullable = false)
    @NotBlank(message = "Model must not be empty")
    private String model;

    @Column(name = "production_year")
    @Min(value = 1900, message = "Year must not be empty")
    @Max(value = 2026, message = "Year should not be more than 2026")
    private int productionYear;

    @Min(value = 0, message = "Mileage must be greater than 0")
    private int mileage;

    @Min(value = 1,  message = "Price must be greater than 1")
    private int price;

    @NotNull (message = "Status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    @Column(nullable = false)
    @NotBlank(message = "Please specify the correct color")
    private String color;

    @Column(nullable = false)
    @Min(value = 1, message = "Horsepower must be greater than 1")
    @Max(value = 2300, message = "The number of horsepower should not be more than 2300")
    private int horsepower;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "FuelType must not be null")
    @Column(nullable = false)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transmission must not be null")
    @Column(nullable = false)
    private Transmission transmission;

    public Car(String brand,
               String model,
               int productionYear,
               int mileage,
               int price,
               String status,
               String color,
               int horsepower,
               String fuelType,
               String transmission) {
        this.brand = brand;
        this.model = model;
        this.productionYear = productionYear;
        this.mileage = mileage;
        this.price = price;
        this.status = CarStatus.valueOf(status);
        this.color = color;
        this.horsepower = horsepower;
        this.fuelType = FuelType.valueOf(fuelType);
        this.transmission = Transmission.valueOf(transmission);
    }
}
