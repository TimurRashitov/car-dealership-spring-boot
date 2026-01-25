package de.ait.controllers;

import de.ait.enums.FuelType;
import de.ait.enums.Transmission;
import de.ait.model.Car;
import de.ait.repository.CarRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Car managment API")
@RestController
@RequestMapping("/api/cars")
@Slf4j
public class CarController {

    private final CarRepository carRepository;

    /* private List<Car> allCars = new ArrayList<>( List.of(
            new Car(1L, "BMW","X5",2000,30000,35000,"AVAILABLE"),
            new Car(2L,"Audi","A4",2025,2000,25000, "SOLD")
    )); */

    @Value("${app.dealership.name:Welcome02}")
    private String dealershipName;

    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping("/info")
    public ResponseEntity<String> getInfo() {
        return ResponseEntity.ok("Welcome to the " + dealershipName + " car dealership!");
        // return ResponseEntity.badRequest().build();
    }


    @Operation(summary = "Get all cars")
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        /*if(id == 1L){
            return new Car(1L, "BMW","X5",2000,30000,35000,"AVAILABLE");
        } else if (id == 2L){
            return new Car(2L,"Audi","A4",2025,2000,25000, "SOLD");
        }
            return null; */
        if (!carRepository.existsById(id)) {
            log.warn("Car with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Car with id {} found", id);
        return ResponseEntity.ofNullable(carRepository.findById(id).orElse(null));
    }

    /*
     @GetMapping("/brand/{brand}")
    public List<Car> getCarByBrand(@PathVariable String brand) {
        //return allCars.stream()
        //        .filter(car->car.getBrand().equals(brand))
        //        .toList();
        return carRepository.findByBrand(brand);
    }
    */

    // api/cars/search?brand=BMW
    @GetMapping("/search")
    public ResponseEntity<List<Car>> searchCars(@RequestParam String brand) {
        return ResponseEntity.ok(carRepository.findByBrand(brand));
    }

    @Operation(summary = "Add a new car")
    @PostMapping
    public ResponseEntity<Long> addCar(@RequestBody Car car) {
        Car savedCar = carRepository.save(car);
        if (savedCar == null) {
            log.error("Car could not be saved");
            return ResponseEntity.badRequest().build();
        }
        log.info("Car with id{} saved", savedCar.getId());
        return new ResponseEntity(HttpStatusCode.valueOf(201));
    }

    @Operation(summary = "Update one car by id")
    @PutMapping("/{id}")
    public ResponseEntity updateCar(@PathVariable Long id, @RequestBody Car car) {
        if (carRepository.existsById(id)) {
            Car carToUpdate = /*allCars.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null); */
                    carRepository.findById(id).orElse(null);
            carToUpdate.setBrand(car.getBrand());
            carToUpdate.setModel(car.getModel());
            carToUpdate.setProductionYear(car.getProductionYear());
            carToUpdate.setMileage(car.getMileage());
            carToUpdate.setPrice(car.getPrice());
            carToUpdate.setStatus(car.getStatus());
            carRepository.save(carToUpdate);
            log.info("Car with id {} updated", id);
            return ResponseEntity.ok("updated car with id = " + id);
        }
        log.warn("Car with id {} not found", id);
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a car by id")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCar(@PathVariable Long id) {
        /*if (id == 1L) {
            allCars.removeFirst();
            return "delete ID = 1";
        }
        else if (id == 2L) {
            allCars.removeLast();
            return "delete ID = 2";
        } else {
            return "Not found";
        } */
        if (!carRepository.existsById(id)) {
            log.warn("Car with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        carRepository.deleteById(id);
        log.info("Car with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/cars/by-price?min=10000&max=20000
    @GetMapping("/by-price")
    public ResponseEntity<List<Car>> searchByPriceBetween(
            @RequestParam int min, @RequestParam int max
    ) {
        return ResponseEntity.ok(carRepository.findByPriceBetween(min, max));
    }

    @Operation(
            summary = "Search cars by color",
            description = "Returns a list of cars with the specified color (case-insensitive). " +
                    "Available colors include Black, White, Silver, Blue, Red, Gray, etc. " +
                    "Example: /api/cars/by-color?color=black"
    )
    @GetMapping("/by-color")
    public ResponseEntity<List<Car>> getCarByColor(@RequestParam String color) {
        if (!carRepository.existsByColorIgnoreCase(color)) {
            log.warn("Color {} not found", color);
            return ResponseEntity.notFound().build();
        }
        log.info("Color {} found", color);
        return ResponseEntity.ok(carRepository.findByColorIgnoreCase(color));
    }

    /* мой метод:
    @Operation(summary = "Search car by color")
    @GetMapping("/by-color")
    public ResponseEntity<?> findByColor(@RequestParam String color){
        List<Car> cars = carRepository.findByColor(color);
        if (cars.isEmpty()){
            return ResponseEntity
                    .badRequest()
                    .body("Cars with color '" + color + "' not found");
        }
        return ResponseEntity.ok(cars);
    } */

    @Operation(summary = "Search by horsepower in a range")
    @GetMapping("/by-power")
    public ResponseEntity<List<Car>> findHorsepowerInRange(
            @RequestParam int minHp, @RequestParam int maxHp
    ) {
        if (minHp < 0 || maxHp < 0 || minHp > maxHp) {
            return ResponseEntity.badRequest().build();
        }
        List<Car> cars = carRepository.findByHorsepowerBetween(minHp, maxHp);
        if (cars.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cars);
    }

    @Operation(summary = "Search by fuel type")
    @GetMapping("/by-fuel")
    public ResponseEntity<List<Car>> findByFuelType(@RequestParam FuelType fuelType) {
        List<Car> cars = carRepository.findByFuelType(fuelType);
        if (cars.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cars);
    }

    @Operation(summary = "Search by transmission type")
    @GetMapping("/by-transmission")
    public ResponseEntity<?> findByTransmission(@RequestParam Transmission transmission) {
        List<Car> cars = carRepository.findByTransmission(transmission);
        if (cars.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Cars with transmission type " + transmission + " not found");
        }
        return ResponseEntity.ok(cars);
    }
}
