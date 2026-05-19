package com.stellarjets.config;

import com.stellarjets.entity.*;
import com.stellarjets.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initData(CategoryRepository catRepo, FlightRepository flightRepo) {
        return args -> {
            if (flightRepo.count() > 0) return;

            log.info("Cargando datos de demo...");

            Category luxury    = catRepo.save(cat("Lujo",      "Vuelos privados de primera clase",   "star"));
            Category adventure = catRepo.save(cat("Aventura",  "Destinos exóticos y remotos",        "globe"));
            Category business  = catRepo.save(cat("Negocios",  "Vuelos ejecutivos y corporativos",   "briefcase"));
            Category family    = catRepo.save(cat("Familia",   "Ideal para viajes en familia",       "users"));

            // ---- Aventura ----
            save(flightRepo, "SJ-001", "Patagonia Extrema",
                    "Vuelo charter hacia el fin del mundo",
                    new BigDecimal("2500.00"), 145,
                    airport("Buenos Aires", "Argentina", "EZE"),
                    airport("Ushuaia", "Argentina", "USH"),
                    12, new BigDecimal("4.9"), adventure,
                    "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=800",
                    "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800");

            save(flightRepo, "SJ-005", "Safari Kenia",
                    "Aventura salvaje en el corazón de África",
                    new BigDecimal("3800.00"), 780,
                    airport("Lima", "Perú", "LIM"),
                    airport("Nairobi", "Kenia", "NBO"),
                    10, new BigDecimal("4.8"), adventure,
                    "https://images.unsplash.com/photo-1516026672322-bc52d61a55d5?w=800",
                    "https://images.unsplash.com/photo-1547970810-dc1eac37d174?w=800");

            save(flightRepo, "SJ-010", "Islandia Extrema",
                    "Auroras boreales y glaciares",
                    new BigDecimal("3400.00"), 180,
                    airport("Londres", "Reino Unido", "LHR"),
                    airport("Reikiavik", "Islandia", "KEF"),
                    9, new BigDecimal("4.8"), adventure,
                    "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?w=800",
                    "https://images.unsplash.com/photo-1476610182048-b716b8518aae?w=800");

            // ---- Lujo ----
            save(flightRepo, "SJ-002", "Dubai Premium",
                    "Experiencia de lujo hacia el Golfo Pérsico",
                    new BigDecimal("5800.00"), 390,
                    airport("Madrid", "España", "MAD"),
                    airport("Dubái", "EAU", "DXB"),
                    6, new BigDecimal("5.0"), luxury,
                    "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?w=800",
                    "https://images.unsplash.com/photo-1506197603052-3cc9c3a201bd?w=800");

            save(flightRepo, "SJ-006", "Maldivas Exclusivo",
                    "Paraíso privado en el Índico",
                    new BigDecimal("6500.00"), 960,
                    airport("Santiago", "Chile", "SCL"),
                    airport("Malé", "Maldivas", "MLE"),
                    4, new BigDecimal("5.0"), luxury,
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
                    "https://images.unsplash.com/photo-1573843981267-be1999ff37cd?w=800");

            save(flightRepo, "SJ-008", "París Romántico",
                    "La ciudad del amor te espera",
                    new BigDecimal("2900.00"), 135,
                    airport("Madrid", "España", "MAD"),
                    airport("París", "Francia", "CDG"),
                    18, new BigDecimal("4.4"), luxury,
                    "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800",
                    "https://images.unsplash.com/photo-1541795795328-f073b763494e?w=800");

            // ---- Negocios ----
            save(flightRepo, "SJ-003", "Tokio Business",
                    "Vuelo ejecutivo non-stop a Japón",
                    new BigDecimal("4200.00"), 780,
                    airport("Ciudad de México", "México", "MEX"),
                    airport("Tokio", "Japón", "NRT"),
                    8, new BigDecimal("4.7"), business,
                    "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=800",
                    "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=800");

            save(flightRepo, "SJ-007", "Nueva York Ejecutivo",
                    "Negocios en la Gran Manzana",
                    new BigDecimal("3100.00"), 720,
                    airport("Buenos Aires", "Argentina", "EZE"),
                    airport("Nueva York", "Estados Unidos", "JFK"),
                    15, new BigDecimal("4.6"), business,
                    "https://images.unsplash.com/photo-1485871981521-5b1fd3805eee?w=800",
                    "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?w=800");

            save(flightRepo, "SJ-011", "Barcelona Meetings",
                    "Conferencias en el Mediterráneo",
                    new BigDecimal("1800.00"), 660,
                    airport("Bogotá", "Colombia", "BOG"),
                    airport("Barcelona", "España", "BCN"),
                    22, new BigDecimal("4.5"), business,
                    "https://images.unsplash.com/photo-1539037116277-4db20889f2d4?w=800");

            // ---- Familia ----
            save(flightRepo, "SJ-004", "Orlando Familia",
                    "El viaje de tus sueños con los chicos",
                    new BigDecimal("1200.00"), 285,
                    airport("Bogotá", "Colombia", "BOG"),
                    airport("Orlando", "Estados Unidos", "MCO"),
                    20, new BigDecimal("4.5"), family,
                    "https://images.unsplash.com/photo-1575089976121-8ed7b2a54265?w=800",
                    "https://images.unsplash.com/photo-1618588507085-c79565432917?w=800");

            save(flightRepo, "SJ-009", "Cancún Familia Total",
                    "Sol, playa y diversión",
                    new BigDecimal("980.00"), 210,
                    airport("Ciudad de México", "México", "MEX"),
                    airport("Cancún", "México", "CUN"),
                    30, new BigDecimal("4.3"), family,
                    "https://images.unsplash.com/photo-1552074284-5e88ef1aef18?w=800",
                    "https://images.unsplash.com/photo-1510414842594-a61c69b5ae57?w=800");

            log.info("Datos de demo cargados: {} vuelos", flightRepo.count());
        };
    }

    // ---- helpers ----

    private Category cat(String name, String description, String iconName) {
        return Category.builder().name(name).description(description).iconName(iconName).build();
    }

    private AirportInfo airport(String city, String country, String iataCode) {
        return AirportInfo.builder().city(city).country(country).iataCode(iataCode).build();
    }

    private void save(FlightRepository repo,
                      String flightNumber, String name, String description,
                      BigDecimal price, int durationMinutes,
                      AirportInfo origin, AirportInfo destination,
                      int seats, BigDecimal rating,
                      Category category, String... imageUrls) {

        Flight flight = Flight.builder()
                .flightNumber(flightNumber)
                .name(name).description(description).price(price)
                .durationMinutes(durationMinutes)
                .origin(origin).destination(destination)
                .availableSeats(seats).rating(rating)
                .category(category).active(true)
                .build();

        for (int i = 0; i < imageUrls.length; i++) {
            FlightImage img = FlightImage.builder()
                    .url(imageUrls[i])
                    .altText(name + " imagen " + (i + 1))
                    .cover(i == 0)
                    .build();
            flight.addImage(img);
        }
        repo.save(flight);
    }
}
