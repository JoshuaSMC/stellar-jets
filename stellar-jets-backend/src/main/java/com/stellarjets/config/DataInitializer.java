package com.stellarjets.config;

import com.stellarjets.entity.*;
import com.stellarjets.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepo, CategoryRepository catRepo,
                               FlightRepository flightRepo, CharacteristicRepository charRepo,
                               ReservationRepository reservationRepo,
                               PasswordEncoder encoder) {
        return args -> {

            // ---- Usuarios demo ----
            if (!userRepo.existsByEmail("admin@stellarjets.com")) {
                userRepo.save(User.builder()
                        .firstName("Admin").lastName("Stellar")
                        .email("admin@stellarjets.com")
                        .password(encoder.encode("admin123"))
                        .role(Role.ADMIN).active(true).build());
                log.info("Usuario admin creado: admin@stellarjets.com / admin123");
            }
            if (!userRepo.existsByEmail("user@stellarjets.com")) {
                userRepo.save(User.builder()
                        .firstName("Carlos").lastName("López")
                        .email("user@stellarjets.com")
                        .password(encoder.encode("user123"))
                        .role(Role.USER).active(true).build());
            }

            // ---- Características demo ----
            List<Characteristic> chars;
            if (charRepo.count() == 0) {
                chars = List.of(
                    charRepo.save(char_("WiFi a bordo", "wifi")),
                    charRepo.save(char_("Asientos reclinables", "armchair")),
                    charRepo.save(char_("Comida gourmet", "utensils")),
                    charRepo.save(char_("Entretenimiento en vuelo", "tv")),
                    charRepo.save(char_("Equipaje incluido", "briefcase")),
                    charRepo.save(char_("Lounge VIP", "wine")),
                    charRepo.save(char_("Servicio premium", "star")),
                    charRepo.save(char_("Vuelo directo", "plane"))
                );
            } else {
                chars = charRepo.findAll();
            }

            // ---- Vuelos demo ----
            if (flightRepo.count() == 0) {
                log.info("Cargando datos de demo...");

                Category luxury    = catRepo.save(cat("Lujo",      "Vuelos privados de primera clase",   "https://images.unsplash.com/photo-1543007630-9710e4a00a20?w=600&q=80&fit=crop"));
                Category adventure = catRepo.save(cat("Aventura",  "Destinos exóticos y remotos",        "https://images.unsplash.com/photo-1682687982501-1e58ab814714?w=600&q=80&fit=crop"));
                Category business  = catRepo.save(cat("Negocios",  "Vuelos ejecutivos y corporativos",   "https://images.unsplash.com/photo-1486325212027-8081e485255e?w=600&q=80&fit=crop"));
                Category family    = catRepo.save(cat("Familia",   "Ideal para viajes en familia",        "https://images.unsplash.com/photo-1609220136736-443140cffec6?w=600&q=80&fit=crop"));

                // ---- Aventura ----
                Flight patagonia = save(flightRepo, "SJ-001", "Patagonia Extrema",
                        "Vuelo charter hacia el fin del mundo",
                        new BigDecimal("2500.00"), 145,
                        airport("Buenos Aires", "Argentina", "EZE"),
                        airport("Ushuaia", "Argentina", "USH"),
                        12, new BigDecimal("4.9"), adventure,
                        "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=800",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800");
                patagonia.getCharacteristics().addAll(chars.subList(0, Math.min(4, chars.size())));
                flightRepo.save(patagonia);

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
            }

            // ---- Reservas demo (US#23) ----
            if (reservationRepo.count() == 0 && flightRepo.count() > 0) {
                List<Flight> flights = flightRepo.findAll();
                LocalDate today = LocalDate.now();

                // Vuelo 0 (Patagonia Extrema) — 3 reservas repartidas
                if (flights.size() >= 1) {
                    res(reservationRepo, flights.get(0), today.plusDays(3),  today.plusDays(7));
                    res(reservationRepo, flights.get(0), today.plusDays(15), today.plusDays(20));
                    res(reservationRepo, flights.get(0), today.plusDays(28), today.plusDays(33));
                }
                // Vuelo 1 (Safari Kenia) — 2 reservas, una larga
                if (flights.size() >= 2) {
                    res(reservationRepo, flights.get(1), today.plusDays(5),  today.plusDays(8));
                    res(reservationRepo, flights.get(1), today.plusDays(22), today.plusDays(30));
                }
                // Vuelo 2 (Islandia Extrema) — casi sin disponibilidad este mes
                if (flights.size() >= 3) {
                    res(reservationRepo, flights.get(2), today.plusDays(1),  today.plusDays(6));
                    res(reservationRepo, flights.get(2), today.plusDays(8),  today.plusDays(14));
                    res(reservationRepo, flights.get(2), today.plusDays(17), today.plusDays(22));
                    res(reservationRepo, flights.get(2), today.plusDays(25), today.plusDays(31));
                }
                // Vuelo 3 (Dubai Premium) — 1 sola reserva larga
                if (flights.size() >= 4) {
                    res(reservationRepo, flights.get(3), today.plusDays(10), today.plusDays(24));
                }
                // Vuelo 4 (Maldivas Exclusivo) — muy ocupado
                if (flights.size() >= 5) {
                    res(reservationRepo, flights.get(4), today.plusDays(2),  today.plusDays(9));
                    res(reservationRepo, flights.get(4), today.plusDays(12), today.plusDays(18));
                    res(reservationRepo, flights.get(4), today.plusDays(20), today.plusDays(27));
                    res(reservationRepo, flights.get(4), today.plusDays(35), today.plusDays(42));
                }
                // Vuelo 5 (París Romántico) — libre la mayor parte
                if (flights.size() >= 6) {
                    res(reservationRepo, flights.get(5), today.plusDays(8),  today.plusDays(11));
                    res(reservationRepo, flights.get(5), today.plusDays(40), today.plusDays(44));
                }
                // Vuelo 6 (Tokio Business) — 2 reservas seguidas
                if (flights.size() >= 7) {
                    res(reservationRepo, flights.get(6), today.plusDays(6),  today.plusDays(13));
                    res(reservationRepo, flights.get(6), today.plusDays(14), today.plusDays(19));
                }
                // Vuelo 7 (Nueva York Ejecutivo) — 1 reserva próxima
                if (flights.size() >= 8) {
                    res(reservationRepo, flights.get(7), today.plusDays(4),  today.plusDays(9));
                    res(reservationRepo, flights.get(7), today.plusDays(30), today.plusDays(37));
                }
                // Vuelo 8 (Barcelona Meetings) — sin reservas (disponible todo)
                // Vuelo 9 (Orlando Familia) — 2 reservas en fines de semana largos
                if (flights.size() >= 10) {
                    res(reservationRepo, flights.get(9), today.plusDays(7),  today.plusDays(10));
                    res(reservationRepo, flights.get(9), today.plusDays(21), today.plusDays(25));
                    res(reservationRepo, flights.get(9), today.plusDays(45), today.plusDays(50));
                }
                // Vuelo 10 (Cancún Familia Total) — muy ocupado, bloquea el buscador de fechas
                if (flights.size() >= 11) {
                    res(reservationRepo, flights.get(10), today.plusDays(1),  today.plusDays(5));
                    res(reservationRepo, flights.get(10), today.plusDays(6),  today.plusDays(12));
                    res(reservationRepo, flights.get(10), today.plusDays(15), today.plusDays(20));
                    res(reservationRepo, flights.get(10), today.plusDays(22), today.plusDays(28));
                }

                log.info("Reservas demo creadas: {}", reservationRepo.count());
            }
        };
    }

    // ---- helpers ----

    private void res(ReservationRepository repo, Flight flight, LocalDate checkIn, LocalDate checkOut) {
        repo.save(Reservation.builder().flight(flight).checkIn(checkIn).checkOut(checkOut).userEmail("system@stellarjets.com").build());
    }

    private Characteristic char_(String name, String iconName) {
        return Characteristic.builder().name(name).iconName(iconName).build();
    }

    private Category cat(String name, String description, String imageUrl) {
        return Category.builder().name(name).description(description).imageUrl(imageUrl).build();
    }

    private AirportInfo airport(String city, String country, String iataCode) {
        return AirportInfo.builder().city(city).country(country).iataCode(iataCode).build();
    }

    private Flight save(FlightRepository repo,
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
        return repo.save(flight);
    }
}
