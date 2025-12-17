package org.logistservice.logist.config;

import org.logistservice.logist.client.model.Client;
import org.logistservice.logist.client.repository.ClientRepository;
import org.logistservice.logist.driver.model.Driver;
import org.logistservice.logist.driver.repository.DriverRepository;
import org.logistservice.logist.order.model.Order;
import org.logistservice.logist.order.model.OrderStatus;
import org.logistservice.logist.order.repository.OrderRepository;
import org.logistservice.logist.user.model.Role;
import org.logistservice.logist.user.model.RoleName;
import org.logistservice.logist.user.model.User;
import org.logistservice.logist.user.repository.RoleRepository;
import org.logistservice.logist.user.repository.UserRepository;
import org.logistservice.logist.vehicle.model.Vehicle;
import org.logistservice.logist.vehicle.model.VehicleStatus;
import org.logistservice.logist.vehicle.repository.VehicleRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements ApplicationRunner {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final OrderRepository orderRepository;
    
    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ClientRepository clientRepository,
                           DriverRepository driverRepository,
                           VehicleRepository vehicleRepository,
                           OrderRepository orderRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientRepository = clientRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.orderRepository = orderRepository;
    }
    
    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        // Ensure all roles exist in database
        // This creates roles: ADMIN, MANAGER, OPERATOR, USER
        System.out.println("Initializing roles in database...");
        for (RoleName roleName : RoleName.values()) {
            try {
                roleRepository.findByName(roleName).orElseGet(() -> {
                    Role role = Role.builder()
                            .name(roleName)
                            .users(new HashSet<>())
                            .build();
                    Role saved = roleRepository.save(role);
                    System.out.println("✓ Created role: " + roleName);
                    return saved;
                });
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                // Если constraint не позволяет создать роль, выводим предупреждение
                System.err.println("⚠ WARNING: Could not create role " + roleName + 
                        ". Please update the database constraint 'roles_name_check' to include '" + roleName + "'.");
                System.err.println("Run the following SQL: ALTER TABLE roles DROP CONSTRAINT IF EXISTS roles_name_check;");
                System.err.println("ALTER TABLE roles ADD CONSTRAINT roles_name_check CHECK (name IN ('ADMIN', 'MANAGER', 'OPERATOR', 'USER'));");
            } catch (Exception e) {
                System.err.println("✗ Error creating role " + roleName + ": " + e.getMessage());
            }
        }
        System.out.println("Role initialization completed.");
        
        // Create admin user if it doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .fullName("System Administrator")
                    .email("admin@example.com")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();
            
            userRepository.save(admin);
            System.out.println("Created admin user with username: admin, password: admin");
        }
        
        // Create test clients
        if (clientRepository.count() == 0) {
            System.out.println("Creating test clients...");
            createTestClients();
        }
        
        // Create test drivers
        if (driverRepository.count() == 0) {
            System.out.println("Creating test drivers...");
            createTestDrivers();
        }
        
        // Create test vehicles
        if (vehicleRepository.count() == 0) {
            System.out.println("Creating test vehicles...");
            createTestVehicles();
        }
        
        // Create test orders
        if (orderRepository.count() == 0) {
            System.out.println("Creating test orders...");
            createTestOrders();
        }
    }
    
    private void createTestClients() {
        Client client1 = Client.builder()
                .name("ООО \"Торговый Дом\"")
                .contactPerson("Иванов Иван Иванович")
                .phone("+7 (495) 123-45-67")
                .email("info@torgdom.ru")
                .taxNumber("7701234567")
                .city("Москва")
                .address("ул. Ленина, д. 10, офис 5")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        clientRepository.save(client1);
        
        Client client2 = Client.builder()
                .name("ИП Петров Петр Петрович")
                .contactPerson("Петров Петр Петрович")
                .phone("+7 (812) 234-56-78")
                .email("petrov@example.com")
                .taxNumber("781234567890")
                .city("Санкт-Петербург")
                .address("пр. Невский, д. 25")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        clientRepository.save(client2);
        
        Client client3 = Client.builder()
                .name("АО \"СтройМатериалы\"")
                .contactPerson("Сидорова Анна Сергеевна")
                .phone("+7 (343) 345-67-89")
                .email("info@stroymat.ru")
                .taxNumber("6658123456")
                .city("Екатеринбург")
                .address("ул. Мира, д. 15, склад 3")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        clientRepository.save(client3);
        
        Client client4 = Client.builder()
                .name("ООО \"МебельПро\"")
                .contactPerson("Козлов Дмитрий Викторович")
                .phone("+7 (391) 456-78-90")
                .email("sales@mebelpro.ru")
                .taxNumber("2467123456")
                .city("Красноярск")
                .address("ул. Советская, д. 42")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        clientRepository.save(client4);
        
        Client client5 = Client.builder()
                .name("ИП Смирнова Елена Александровна")
                .contactPerson("Смирнова Елена Александровна")
                .phone("+7 (495) 567-89-01")
                .email("smirnova@example.com")
                .taxNumber("7715987654")
                .city("Москва")
                .address("ул. Пушкина, д. 8, кв. 12")
                .active(false)
                .createdAt(LocalDateTime.now())
                .build();
        clientRepository.save(client5);
        
        System.out.println("✓ Created 5 test clients");
    }
    
    private void createTestDrivers() {
        Driver driver1 = Driver.builder()
                .fullName("Волков Алексей Николаевич")
                .phone("+7 (495) 111-22-33")
                .drivingLicense("77АА123456")
                .experienceYears(10)
                .active(true)
                .build();
        driverRepository.save(driver1);
        
        Driver driver2 = Driver.builder()
                .fullName("Морозов Сергей Владимирович")
                .phone("+7 (812) 222-33-44")
                .drivingLicense("78ВВ234567")
                .experienceYears(5)
                .active(true)
                .build();
        driverRepository.save(driver2);
        
        Driver driver3 = Driver.builder()
                .fullName("Новиков Андрей Игоревич")
                .phone("+7 (343) 333-44-55")
                .drivingLicense("66СС345678")
                .experienceYears(8)
                .active(true)
                .build();
        driverRepository.save(driver3);
        
        Driver driver4 = Driver.builder()
                .fullName("Лебедев Максим Олегович")
                .phone("+7 (391) 444-55-66")
                .drivingLicense("24ДД456789")
                .experienceYears(3)
                .active(true)
                .build();
        driverRepository.save(driver4);
        
        Driver driver5 = Driver.builder()
                .fullName("Соколов Игорь Петрович")
                .phone("+7 (495) 555-66-77")
                .drivingLicense("77ЕЕ567890")
                .experienceYears(15)
                .active(false)
                .build();
        driverRepository.save(driver5);
        
        System.out.println("✓ Created 5 test drivers");
    }
    
    private void createTestVehicles() {
        Vehicle vehicle1 = Vehicle.builder()
                .registrationNumber("А123БВ777")
                .type("Грузовик")
                .capacityWeight(5000.0)
                .capacityVolume(30.0)
                .status(VehicleStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        vehicleRepository.save(vehicle1);
        
        Vehicle vehicle2 = Vehicle.builder()
                .registrationNumber("В456ГД78")
                .type("Фургон")
                .capacityWeight(3500.0)
                .capacityVolume(20.0)
                .status(VehicleStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        vehicleRepository.save(vehicle2);
        
        Vehicle vehicle3 = Vehicle.builder()
                .registrationNumber("С789ЕЖ99")
                .type("Грузовик")
                .capacityWeight(10000.0)
                .capacityVolume(50.0)
                .status(VehicleStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        vehicleRepository.save(vehicle3);
        
        Vehicle vehicle4 = Vehicle.builder()
                .registrationNumber("Е012ЖЗ177")
                .type("Фургон")
                .capacityWeight(2500.0)
                .capacityVolume(15.0)
                .status(VehicleStatus.IN_SERVICE)
                .createdAt(LocalDateTime.now())
                .build();
        vehicleRepository.save(vehicle4);
        
        Vehicle vehicle5 = Vehicle.builder()
                .registrationNumber("З345ИК777")
                .type("Грузовик")
                .capacityWeight(7500.0)
                .capacityVolume(40.0)
                .status(VehicleStatus.OUT_OF_SERVICE)
                .createdAt(LocalDateTime.now())
                .build();
        vehicleRepository.save(vehicle5);
        
        System.out.println("✓ Created 5 test vehicles");
    }
    
    private void createTestOrders() {
        var clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            System.out.println("⚠ No clients found, skipping order creation");
            return;
        }
        
        var clientList = clients.stream().toList();
        LocalDateTime now = LocalDateTime.now();
        
        // Order 1
        Order order1 = Order.builder()
                .orderNumber("ORD-2024-001")
                .client(clientList.get(0))
                .status(OrderStatus.NEW)
                .originCity("Москва")
                .originAddress("ул. Ленина, д. 10")
                .destinationCity("Санкт-Петербург")
                .destinationAddress("пр. Невский, д. 25")
                .cargoDescription("Мебель и бытовая техника")
                .cargoWeight(1500.0)
                .cargoVolume(12.0)
                .price(new BigDecimal("25000.00"))
                .plannedPickupDate(LocalDate.now().plusDays(1))
                .plannedDeliveryDate(LocalDate.now().plusDays(3))
                .createdAt(now)
                .build();
        orderRepository.save(order1);
        
        // Order 2
        Order order2 = Order.builder()
                .orderNumber("ORD-2024-002")
                .client(clientList.size() > 1 ? clientList.get(1) : clientList.get(0))
                .status(OrderStatus.IN_PROGRESS)
                .originCity("Санкт-Петербург")
                .originAddress("пр. Невский, д. 25")
                .destinationCity("Екатеринбург")
                .destinationAddress("ул. Мира, д. 15")
                .cargoDescription("Строительные материалы")
                .cargoWeight(3000.0)
                .cargoVolume(25.0)
                .price(new BigDecimal("45000.00"))
                .plannedPickupDate(LocalDate.now().minusDays(2))
                .plannedDeliveryDate(LocalDate.now().plusDays(1))
                .createdAt(now.minusDays(3))
                .build();
        orderRepository.save(order2);
        
        // Order 3
        Order order3 = Order.builder()
                .orderNumber("ORD-2024-003")
                .client(clientList.size() > 2 ? clientList.get(2) : clientList.get(0))
                .status(OrderStatus.DELIVERED)
                .originCity("Екатеринбург")
                .originAddress("ул. Мира, д. 15")
                .destinationCity("Красноярск")
                .destinationAddress("ул. Советская, д. 42")
                .cargoDescription("Офисная мебель")
                .cargoWeight(800.0)
                .cargoVolume(8.0)
                .price(new BigDecimal("18000.00"))
                .plannedPickupDate(LocalDate.now().minusDays(5))
                .plannedDeliveryDate(LocalDate.now().minusDays(2))
                .actualDeliveryDate(now.minusDays(2).plusHours(5))
                .createdAt(now.minusDays(6))
                .build();
        orderRepository.save(order3);
        
        // Order 4
        Order order4 = Order.builder()
                .orderNumber("ORD-2024-004")
                .client(clientList.size() > 3 ? clientList.get(3) : clientList.get(0))
                .status(OrderStatus.NEW)
                .originCity("Красноярск")
                .originAddress("ул. Советская, д. 42")
                .destinationCity("Москва")
                .destinationAddress("ул. Пушкина, д. 8")
                .cargoDescription("Электроника и компьютеры")
                .cargoWeight(500.0)
                .cargoVolume(5.0)
                .price(new BigDecimal("32000.00"))
                .plannedPickupDate(LocalDate.now().plusDays(2))
                .plannedDeliveryDate(LocalDate.now().plusDays(5))
                .createdAt(now.minusDays(1))
                .build();
        orderRepository.save(order4);
        
        // Order 5
        Order order5 = Order.builder()
                .orderNumber("ORD-2024-005")
                .client(clientList.get(0))
                .status(OrderStatus.CANCELED)
                .originCity("Москва")
                .originAddress("ул. Ленина, д. 10")
                .destinationCity("Санкт-Петербург")
                .destinationAddress("пр. Невский, д. 25")
                .cargoDescription("Хрупкие товары")
                .cargoWeight(200.0)
                .cargoVolume(2.0)
                .price(new BigDecimal("15000.00"))
                .plannedPickupDate(LocalDate.now().minusDays(10))
                .plannedDeliveryDate(LocalDate.now().minusDays(7))
                .createdAt(now.minusDays(12))
                .build();
        orderRepository.save(order5);
        
        // Order 6
        Order order6 = Order.builder()
                .orderNumber("ORD-2024-006")
                .client(clientList.size() > 1 ? clientList.get(1) : clientList.get(0))
                .status(OrderStatus.IN_PROGRESS)
                .originCity("Санкт-Петербург")
                .originAddress("пр. Невский, д. 25")
                .destinationCity("Москва")
                .destinationAddress("ул. Ленина, д. 10")
                .cargoDescription("Продовольственные товары")
                .cargoWeight(1200.0)
                .cargoVolume(10.0)
                .price(new BigDecimal("22000.00"))
                .plannedPickupDate(LocalDate.now().minusDays(1))
                .plannedDeliveryDate(LocalDate.now().plusDays(1))
                .createdAt(now.minusDays(2))
                .build();
        orderRepository.save(order6);
        
        System.out.println("✓ Created 6 test orders");
    }
}





