package de.ait.validators;

import de.ait.model.Car;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CarValidator {

    public boolean isValid(Car car) {
        return validateWithErrors(car).isEmpty();
    }

    public List<String> validateWithErrors(Car car) {

        List<String> errors = new ArrayList<>();

        if (car == null) {
            errors.add("Auto-Objekt darf nicht null sein");
            log.warn("Ungültiges Auto: Objekt ist null");
            return errors;
        }

        if (car.getBrand() == null || car.getBrand().isBlank()) {
            errors.add("Marke darf nicht leer sein");
        }

        if (car.getModel() == null || car.getModel().isBlank()) {
            errors.add("Modell darf nicht leer sein");
        }

        if (car.getProductionYear() < 1900) {
            errors.add("Baujahr muss >= 1900 sein");
        }

        if (car.getMileage() < 0) {
            errors.add("Kilometerstand muss >= 0 sein");
        }

        if (car.getPrice() < 1) {
            errors.add("Preis muss >= 1 sein");
        }

        if (car.getStatus() == null) {
            errors.add("Status darf nicht null sein");
        }

        if (car.getColor() == null || car.getColor().isBlank()) {
            errors.add("Farbe darf nicht leer sein");
        }

        if (car.getHorsepower() < 1) {
            errors.add("PS muss >= 1 sein");
        }

        if (car.getFuelType() == null) {
            errors.add("Kraftstofftyp darf nicht null sein");
        }

        if (car.getTransmission() == null) {
            errors.add("Getriebe darf nicht null sein");
        }

        if (!errors.isEmpty()) {
            log.warn("Ungültiges Auto empfangen: {}", errors);
        }

        return errors;
    }

    public static boolean checkValid(Car car) {

        if (car == null) {
            log.warn("Invalid car object received: car=null");
            return false;
        }

        // Brand
        if (car.getBrand() == null || car.getBrand().isBlank()
                || car.getBrand().length() < 2
                || car.getBrand().length() > 50) {

            log.warn("Invalid car object received: brand='{}'", car.getBrand());
            return false;
        }

        // Model
        if (car.getModel() == null || car.getModel().isBlank()
                || car.getModel().length() > 50) {

            log.warn("Invalid car object received: model='{}'", car.getModel());
            return false;
        }

        // Production year
        int currentYear = Year.now().getValue();
        if (car.getProductionYear() < 1900 || car.getProductionYear() > currentYear) {

            log.warn("Invalid car object received: productionYear={}",
                    car.getProductionYear());
            return false;
        }

        // Mileage
        if (car.getMileage() < 0) {
            log.warn("Invalid car object received: mileage={}",
                    car.getMileage());
            return false;
        }

        // Price
        if (car.getPrice() <= 0) {
            log.warn("Invalid car object received: price={}",
                    car.getPrice());
            return false;
        }

        // Horsepower
        if (car.getHorsepower() < 1 || car.getHorsepower() > 1500) {
            log.warn("Invalid car object received: horsepower={}",
                    car.getHorsepower());
            return false;
        }

        // Color
        if (car.getColor() == null || car.getColor().isBlank()) {
            log.warn("Invalid car object received: color='{}'",
                    car.getColor());
            return false;
        }

        // Enums
        if (car.getFuelType() == null
                || car.getTransmission() == null
                || car.getStatus() == null) {

            log.warn(
                    "Invalid car object received: fuelType={}, transmission={}, status={}",
                    car.getFuelType(),
                    car.getTransmission(),
                    car.getStatus()
            );
            return false;
        }

        return true;
    }

    public static List<String> validateCarWithErrors(Car car) {

        checkValid(car); // Log if car is null

        List<String> errors = new ArrayList<>();

        if (car == null) {
            errors.add("Car must not be null");
            log.warn("Invalid car object received: car=null");
            return errors;
        }

        // Brand
        if (car.getBrand() == null || car.getBrand().isBlank()) {
            errors.add("Brand must not be empty");
        } else if (car.getBrand().length() < 2 || car.getBrand().length() > 50) {
            errors.add("Brand length must be between 2 and 50 characters");
        }

        // Model
        if (car.getModel() == null || car.getModel().isBlank()) {
            errors.add("Model must not be empty");
        } else if (car.getModel().length() > 50) {
            errors.add("Model length must not exceed 50 characters");
        }

        // Production year
        int currentYear = Year.now().getValue();
        if (car.getProductionYear() < 1900 || car.getProductionYear() > currentYear) {
            errors.add("Production year must be between 1900 and " + currentYear);
        }

        // Mileage
        if (car.getMileage() < 0) {
            errors.add("Mileage must be greater or equal to 0");
        }

        // Price
        if (car.getPrice() <= 0) {
            errors.add("Price must be greater than 0");
        }

        // Horsepower
        if (car.getHorsepower() < 1 || car.getHorsepower() > 1500) {
            errors.add("Horsepower must be between 1 and 1500");
        }

        // Color
        if (car.getColor() == null || car.getColor().isBlank()) {
            errors.add("Color must not be empty");
        }

        // Enums
        if (car.getFuelType() == null) {
            errors.add("Fuel type must not be null");
        }

        if (car.getTransmission() == null) {
            errors.add("Transmission must not be null");
        }

        if (car.getStatus() == null) {
            errors.add("Status must not be null");
        }

        if (!errors.isEmpty()) {
            log.warn("Invalid car object received: {}", errors);
        }

        return errors;
    }

    private void logInvalid(String reason, Car car) {
        log.warn("Invalid car received. Reason: {} | Car: {}", reason, car);
    }

    public boolean isValidCar(Car car) {

        if (car == null) {
            log.error("Invalid car received: car is null");
            return false;
        }


        if (car.getBrand() == null || car.getBrand().isBlank()) {
            logInvalid("Brand is missing", car);
            return false;
        }

        if (car.getModel() == null || car.getModel().isBlank()) {
            logInvalid("Model is missing", car);
            return false;
        }

        if (car.getProductionYear() < 1900) {
            logInvalid("Production year < 1900", car);
            return false;
        }

        if (car.getMileage() < 0) {
            logInvalid("Mileage < 0", car);
            return false;
        }

        if (car.getPrice() <= 0) {
            logInvalid("price <= 0", car);
            return false;
        }

        if (car.getStatus() == null) {
            logInvalid("Status is null", car);
            return false;
        }

        if (car.getFuelType() == null) {
            logInvalid("Fuel type is null", car);
            return false;
        }

        if (car.getTransmission() == null) {
            logInvalid("Transmission is null", car);
            return false;
        }

        if (car.getColor() == null || car.getColor().isBlank()) {
            logInvalid("Color is missing", car);
            return false;
        }

        if (car.getHorsepower() <= 0) {
            logInvalid("horsepower <= 0", car);
            return false;
        }


        log.info("Car validation passed: {} {}", car.getBrand(), car.getModel());
        return true;
    }
}
