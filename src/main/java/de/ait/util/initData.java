package de.ait.util;

import de.ait.model.Car;
import de.ait.repository.CarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class initData {
    @Bean
    CommandLineRunner initDatabase(CarRepository carRepository){
        return args -> {
            if (carRepository.count() == 0){
                carRepository.save(new Car("BMW", "X5", 2019, 100000, 50000, "AVAILABLE","black",250,"PETROL","AUTOMATIC"));
                carRepository.save(new Car("Audi", "A4", 2018, 100000, 40000, "AVAILABLE", "blue", 180, "HYBRID","MANUAL"));
                carRepository.save(new Car("Mercedes", "C-Class", 2020, 80000, 60000, "AVAILABLE","white",230, "ELECTRIC", "AUTOMATIC"));
            }
        };
    }
}
