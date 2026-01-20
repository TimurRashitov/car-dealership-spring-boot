package de.ait.repository;

import de.ait.enums.CarStatus;
import de.ait.enums.FuelType;
import de.ait.enums.Transmission;
import de.ait.model.Car;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    // SELECT * FROM CARS WHERE brand = ?
    List<Car> findByBrand(String brand);

    List<Car> findByStatus(CarStatus status);

    boolean existsById(@NonNull Long id);

    List<Car> findByPriceBetween(int min, int max);

    List<Car> findByColor(String color);

    List<Car> findByHorsepowerBetween(int minHp, int maxHp);

    List<Car> findByFuelType(FuelType fuelType);

    List<Car> findByTransmission(Transmission transmission);

}
