package org.logistservice.logist.order.service;

import org.logistservice.logist.client.model.Client;
import org.logistservice.logist.client.repository.ClientRepository;
import org.logistservice.logist.common.enums.OrderSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.common.exception.BadRequestException;
import org.logistservice.logist.common.exception.NotFoundException;
import org.logistservice.logist.driver.model.Driver;
import org.logistservice.logist.driver.repository.DriverRepository;
import org.logistservice.logist.order.model.Order;
import org.logistservice.logist.order.model.OrderAssignment;
import org.logistservice.logist.order.model.OrderStatus;
import org.logistservice.logist.order.model.OrderStatusHistory;
import org.logistservice.logist.order.model.dto.*;
import org.logistservice.logist.order.repository.OrderAssignmentRepository;
import org.logistservice.logist.order.repository.OrderRepository;
import org.logistservice.logist.order.repository.OrderStatusHistoryRepository;
import org.logistservice.logist.security.CustomUserDetails;
import org.logistservice.logist.user.model.User;
import org.logistservice.logist.user.repository.UserRepository;
import org.springframework.util.StringUtils;
import org.logistservice.logist.vehicle.model.Vehicle;
import org.logistservice.logist.vehicle.repository.VehicleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderAssignmentRepository assignmentRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    
    private static final Random random = new Random();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    public OrderServiceImpl(OrderRepository orderRepository,
                           OrderAssignmentRepository assignmentRepository,
                           OrderStatusHistoryRepository statusHistoryRepository,
                           ClientRepository clientRepository,
                           VehicleRepository vehicleRepository,
                           DriverRepository driverRepository,
                           UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.assignmentRepository = assignmentRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAll(String search, OrderStatus status, Long clientId, LocalDate fromDate, LocalDate toDate,
                                 OrderSortField sortField, SortDirection sortDirection) {
        List<Order> orders = orderRepository.findAllWithClientAndManager();
        
        List<OrderDto> result = orders.stream()
                .filter(order -> {
                    boolean searchMatch = !StringUtils.hasText(search) || 
                            (order.getOrderNumber() != null && order.getOrderNumber().toLowerCase().contains(search.toLowerCase())) ||
                            (order.getClient() != null && order.getClient().getName() != null && 
                             order.getClient().getName().toLowerCase().contains(search.toLowerCase())) ||
                            (order.getOriginCity() != null && order.getOriginCity().toLowerCase().contains(search.toLowerCase())) ||
                            (order.getDestinationCity() != null && order.getDestinationCity().toLowerCase().contains(search.toLowerCase()));
                    boolean statusMatch = status == null || order.getStatus() == status;
                    boolean clientMatch = clientId == null || 
                            (order.getClient() != null && order.getClient().getId().equals(clientId));
                    boolean dateMatch = true;
                    if (fromDate != null || toDate != null) {
                        LocalDate orderDate = order.getCreatedAt() != null ? 
                                order.getCreatedAt().toLocalDate() : null;
                        if (orderDate != null) {
                            if (fromDate != null && orderDate.isBefore(fromDate)) {
                                dateMatch = false;
                            }
                            if (toDate != null && orderDate.isAfter(toDate)) {
                                dateMatch = false;
                            }
                        } else {
                            dateMatch = false;
                        }
                    }
                    return searchMatch && statusMatch && clientMatch && dateMatch;
                })
                .map(this::toOrderDto)
                .collect(Collectors.toList());
        
        // Применяем сортировку
        if (sortField != null) {
            Comparator<OrderDto> comparator = getOrderComparator(sortField);
            if (sortDirection == SortDirection.DESC) {
                comparator = comparator.reversed();
            }
            result = result.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
        
        return result;
    }
    
    private Comparator<OrderDto> getOrderComparator(OrderSortField sortField) {
        return switch (sortField) {
            case ORDER_NUMBER -> Comparator.comparing(
                    order -> order.getOrderNumber() != null ? order.getOrderNumber() : "",
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case CLIENT_NAME -> Comparator.comparing(
                    order -> order.getClientName() != null ? order.getClientName() : "",
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case STATUS -> Comparator.comparing(
                    OrderDto::getStatus,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case CREATED_AT -> Comparator.comparing(
                    OrderDto::getCreatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case PLANNED_PICKUP_DATE -> Comparator.comparing(
                    OrderDto::getPlannedPickupDate,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case PLANNED_DELIVERY_DATE -> Comparator.comparing(
                    OrderDto::getPlannedDeliveryDate,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderDetailsDto getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
        
        return toDetailsDto(order);
    }
    
    @Override
    @Transactional
    public OrderDto create(OrderCreateUpdateRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + request.getClientId()));
        
        User manager = null;
        if (request.getManagerId() != null) {
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getManagerId()));
        } else {
            manager = getCurrentUser();
        }
        
        String orderNumber = generateOrderNumber();
        
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .client(client)
                .status(OrderStatus.NEW)
                .createdAt(LocalDateTime.now())
                .plannedPickupDate(request.getPlannedPickupDate())
                .plannedDeliveryDate(request.getPlannedDeliveryDate())
                .originCity(request.getOriginCity())
                .originAddress(request.getOriginAddress())
                .destinationCity(request.getDestinationCity())
                .destinationAddress(request.getDestinationAddress())
                .cargoDescription(request.getCargoDescription())
                .cargoWeight(request.getCargoWeight())
                .cargoVolume(request.getCargoVolume())
                .price(request.getPrice())
                .manager(manager)
                .build();
        
        Order saved = orderRepository.save(order);
        
        // Если указаны водитель и транспорт, создаем назначение
        if (request.getDriverId() != null && request.getVehicleId() != null) {
            Driver driver = driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new NotFoundException("Driver not found with id: " + request.getDriverId()));
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + request.getVehicleId()));
            
            // Преобразуем даты в LocalDateTime для назначения
            LocalDateTime plannedStart = request.getPlannedPickupDate() != null ? 
                    request.getPlannedPickupDate().atStartOfDay() : null;
            LocalDateTime plannedEnd = request.getPlannedDeliveryDate() != null ? 
                    request.getPlannedDeliveryDate().atTime(23, 59, 59) : null;
            
            OrderAssignment assignment = OrderAssignment.builder()
                    .order(saved)
                    .driver(driver)
                    .vehicle(vehicle)
                    .plannedStart(plannedStart)
                    .plannedEnd(plannedEnd)
                    .build();
            
            assignmentRepository.save(assignment);
        }
        
        return toOrderDto(saved);
    }
    
    @Override
    @Transactional
    public OrderDto update(Long id, OrderCreateUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
        
        if (!request.getClientId().equals(order.getClient().getId())) {
            Client newClient = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new NotFoundException("Client not found with id: " + request.getClientId()));
            order.setClient(newClient);
        }
        
        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getManagerId()));
            order.setManager(manager);
        }
        
        order.setPlannedPickupDate(request.getPlannedPickupDate());
        order.setPlannedDeliveryDate(request.getPlannedDeliveryDate());
        order.setOriginCity(request.getOriginCity());
        order.setOriginAddress(request.getOriginAddress());
        order.setDestinationCity(request.getDestinationCity());
        order.setDestinationAddress(request.getDestinationAddress());
        // Используем cargoDescription, если указано, иначе description
        String description = request.getCargoDescription() != null && !request.getCargoDescription().isEmpty() 
                ? request.getCargoDescription() 
                : request.getDescription();
        order.setCargoDescription(description);
        order.setCargoWeight(request.getCargoWeight());
        order.setCargoVolume(request.getCargoVolume());
        order.setPrice(request.getPrice());
        
        Order updated = orderRepository.save(order);
        
        // Обновляем или создаем назначение, если указаны водитель и транспорт
        if (request.getDriverId() != null && request.getVehicleId() != null) {
            Driver driver = driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new NotFoundException("Driver not found with id: " + request.getDriverId()));
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + request.getVehicleId()));
            
            // Преобразуем даты в LocalDateTime для назначения
            LocalDateTime plannedStart = request.getPlannedPickupDate() != null ? 
                    request.getPlannedPickupDate().atStartOfDay() : null;
            LocalDateTime plannedEnd = request.getPlannedDeliveryDate() != null ? 
                    request.getPlannedDeliveryDate().atTime(23, 59, 59) : null;
            
            // Ищем существующее назначение для этого заказа
            List<OrderAssignment> existingAssignments = assignmentRepository.findByOrderId(updated.getId());
            OrderAssignment assignment;
            
            if (!existingAssignments.isEmpty()) {
                // Обновляем первое назначение
                assignment = existingAssignments.get(0);
                assignment.setDriver(driver);
                assignment.setVehicle(vehicle);
                assignment.setPlannedStart(plannedStart);
                assignment.setPlannedEnd(plannedEnd);
            } else {
                // Создаем новое назначение
                assignment = OrderAssignment.builder()
                        .order(updated)
                        .driver(driver)
                        .vehicle(vehicle)
                        .plannedStart(plannedStart)
                        .plannedEnd(plannedEnd)
                        .build();
            }
            
            assignmentRepository.save(assignment);
        }
        
        return toOrderDto(updated);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public OrderDto updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
        
        if (order.getStatus() == newStatus) {
            return toOrderDto(order);
        }
        
        OrderStatus oldStatus = order.getStatus();
        
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedAt(LocalDateTime.now())
                .changedBy(getCurrentUser())
                .build();
        
        order.setStatus(newStatus);
        if (newStatus == OrderStatus.DELIVERED) {
            order.setActualDeliveryDate(LocalDateTime.now());
        }
        
        orderRepository.save(order);
        statusHistoryRepository.save(history);
        
        return toOrderDto(order);
    }
    
    @Override
    @Transactional
    public OrderAssignmentDto addAssignment(Long orderId, OrderAssignmentCreateUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));
        
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + request.getVehicleId()));
        
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new NotFoundException("Driver not found with id: " + request.getDriverId()));
        
        if (request.getPlannedStart() != null && request.getPlannedEnd() != null) {
            if (request.getPlannedEnd().isBefore(request.getPlannedStart()) || 
                request.getPlannedEnd().equals(request.getPlannedStart())) {
                throw new BadRequestException("Planned end must be after planned start");
            }
        }
        
        OrderAssignment assignment = OrderAssignment.builder()
                .order(order)
                .vehicle(vehicle)
                .driver(driver)
                .plannedStart(request.getPlannedStart())
                .plannedEnd(request.getPlannedEnd())
                .build();
        
        OrderAssignment saved = assignmentRepository.save(assignment);
        return toAssignmentDto(saved);
    }
    
    @Override
    @Transactional
    public OrderAssignmentDto updateAssignment(Long assignmentId, OrderAssignmentCreateUpdateRequest request) {
        OrderAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));
        
        if (!request.getVehicleId().equals(assignment.getVehicle().getId())) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + request.getVehicleId()));
            assignment.setVehicle(vehicle);
        }
        
        if (!request.getDriverId().equals(assignment.getDriver().getId())) {
            Driver driver = driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new NotFoundException("Driver not found with id: " + request.getDriverId()));
            assignment.setDriver(driver);
        }
        
        if (request.getPlannedStart() != null && request.getPlannedEnd() != null) {
            if (request.getPlannedEnd().isBefore(request.getPlannedStart()) || 
                request.getPlannedEnd().equals(request.getPlannedStart())) {
                throw new BadRequestException("Planned end must be after planned start");
            }
        }
        
        assignment.setPlannedStart(request.getPlannedStart());
        assignment.setPlannedEnd(request.getPlannedEnd());
        assignment.setActualStart(request.getActualStart());
        assignment.setActualEnd(request.getActualEnd());
        
        OrderAssignment updated = assignmentRepository.save(assignment);
        return toAssignmentDto(updated);
    }
    
    @Override
    @Transactional
    public void deleteAssignment(Long assignmentId) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new NotFoundException("Assignment not found with id: " + assignmentId);
        }
        assignmentRepository.deleteById(assignmentId);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userRepository.findById(userDetails.getId())
                    .orElse(null);
        }
        return null;
    }
    
    private String generateOrderNumber() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        int randomPart = 10000 + random.nextInt(90000);
        return "ORD-" + datePart + "-" + randomPart;
    }
    
    private OrderDto toOrderDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .clientId(order.getClient() != null ? order.getClient().getId() : null)
                .clientName(order.getClient() != null ? order.getClient().getName() : null)
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .plannedPickupDate(order.getPlannedPickupDate())
                .plannedDeliveryDate(order.getPlannedDeliveryDate())
                .actualDeliveryDate(order.getActualDeliveryDate())
                .originCity(order.getOriginCity())
                .originAddress(order.getOriginAddress())
                .destinationCity(order.getDestinationCity())
                .destinationAddress(order.getDestinationAddress())
                .cargoDescription(order.getCargoDescription())
                .cargoWeight(order.getCargoWeight())
                .cargoVolume(order.getCargoVolume())
                .price(order.getPrice())
                .managerId(order.getManager() != null ? order.getManager().getId() : null)
                .managerName(order.getManager() != null ? order.getManager().getFullName() : null)
                .build();
    }
    
    private OrderDetailsDto toDetailsDto(Order order) {
        List<OrderAssignmentDto> assignments = order.getAssignments().stream()
                .map(this::toAssignmentDto)
                .sorted(Comparator.comparing(OrderAssignmentDto::getPlannedStart, 
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        
        List<OrderStatusHistoryDto> statusHistory = order.getStatusHistory().stream()
                .map(this::toStatusHistoryDto)
                .sorted(Comparator.comparing(OrderStatusHistoryDto::getChangedAt).reversed())
                .collect(Collectors.toList());
        
        return OrderDetailsDto.builder()
                .order(toOrderDto(order))
                .assignments(assignments)
                .statusHistory(statusHistory)
                .build();
    }
    
    private OrderAssignmentDto toAssignmentDto(OrderAssignment assignment) {
        return OrderAssignmentDto.builder()
                .id(assignment.getId())
                .orderId(assignment.getOrder() != null ? assignment.getOrder().getId() : null)
                .vehicleId(assignment.getVehicle() != null ? assignment.getVehicle().getId() : null)
                .vehicleRegistrationNumber(assignment.getVehicle() != null ? 
                        assignment.getVehicle().getRegistrationNumber() : null)
                .driverId(assignment.getDriver() != null ? assignment.getDriver().getId() : null)
                .driverName(assignment.getDriver() != null ? assignment.getDriver().getFullName() : null)
                .plannedStart(assignment.getPlannedStart())
                .plannedEnd(assignment.getPlannedEnd())
                .actualStart(assignment.getActualStart())
                .actualEnd(assignment.getActualEnd())
                .build();
    }
    
    private OrderStatusHistoryDto toStatusHistoryDto(OrderStatusHistory history) {
        return OrderStatusHistoryDto.builder()
                .id(history.getId())
                .oldStatus(history.getOldStatus())
                .newStatus(history.getNewStatus())
                .changedAt(history.getChangedAt())
                .changedByUserId(history.getChangedBy() != null ? history.getChangedBy().getId() : null)
                .changedByUsername(history.getChangedBy() != null ? history.getChangedBy().getUsername() : null)
                .build();
    }
}
