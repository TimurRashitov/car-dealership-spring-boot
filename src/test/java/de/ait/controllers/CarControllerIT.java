package de.ait.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ait.enums.CarStatus;
import de.ait.enums.FuelType;
import de.ait.enums.Transmission;
import de.ait.model.Car;
import de.ait.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CarControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        carRepository.deleteAll();
    }

    private Car buildValidCar(String brand, String model){
        Car car = new Car();
        car.setBrand(brand);
        car.setModel(model);
        car.setProductionYear(2020);
        car.setMileage(30000);
        car.setPrice(30000);
        car.setStatus(CarStatus.AVAILABLE);
        car.setColor("red");
        car.setFuelType(FuelType.DIESEL);
        car.setHorsepower(150);
        car.setTransmission(Transmission.AUTOMATIC);
        return car;
    }

    @Test
    @DisplayName("GET /cars/{id} should return car if exists")
    void testGetCarByIdShouldReturnCar() throws Exception {
        Car saved = carRepository.save(buildValidCar("BMW","X5"));

        mockMvc.perform(get("/api/cars/{id}",saved.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /cars/{id} should not return car")
    void testGetCarByIdShouldNotReturnCar() throws Exception {

        mockMvc.perform(get("/api/cars/{id}",1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/cars should save car in H2 return 201")
    void testCreateNewCarShouldReturn201() throws  Exception {
        Car car = buildValidCar("Audi", "A6");

        String jsonBody = objectMapper.writeValueAsString(car);

        mockMvc.perform(
                post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());

        List<Car> cars = carRepository.findAll();
        assertEquals(1, cars.size());

        assertEquals(car.getBrand(), cars.getFirst().getBrand());
    }

    @Test
    @DisplayName("GET /api/cars should return all cars")
    void testAllCarsShouldReturnAllCars() throws Exception {
        Car carAudi = buildValidCar("Audi", "A6");
        Car carKia = buildValidCar("Kia", "Rio");

    }
}

