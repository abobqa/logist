package org.logistservice.logist.stats.service;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.order.model.Order;
import org.logistservice.logist.order.model.OrderAssignment;
import org.logistservice.logist.order.model.OrderStatus;
import org.logistservice.logist.order.repository.OrderAssignmentRepository;
import org.logistservice.logist.order.repository.OrderRepository;
import org.logistservice.logist.stats.model.OrderStatusCountDto;
import org.logistservice.logist.stats.model.TopClientDto;
import org.logistservice.logist.stats.model.VehicleLoadDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    
    private final OrderRepository orderRepository;
    private final OrderAssignmentRepository assignmentRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusCountDto> getOrderStatusCounts(LocalDate fromDate, LocalDate toDate) {
        List<Order> orders = orderRepository.findAllWithClientAndManager();
        
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;
        
        Map<OrderStatus, Long> statusCounts = orders.stream()
                .filter(order -> {
                    if (fromDateTime == null && toDateTime == null) {
                        return true;
                    }
                    LocalDateTime createdAt = order.getCreatedAt();
                    if (createdAt == null) {
                        return false;
                    }
                    boolean afterFrom = fromDateTime == null || !createdAt.isBefore(fromDateTime);
                    boolean beforeTo = toDateTime == null || createdAt.isBefore(toDateTime);
                    return afterFrom && beforeTo;
                })
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.counting()
                ));
        
        return statusCounts.entrySet().stream()
                .map(entry -> OrderStatusCountDto.builder()
                        .status(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(OrderStatusCountDto::getStatus, 
                        Comparator.comparing(Enum::name)))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TopClientDto> getTopClients(LocalDate fromDate, LocalDate toDate, int limit) {
        if (limit <= 0) {
            limit = 5;
        }
        
        List<Order> orders = orderRepository.findAllWithClientAndManager();
        
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;
        
        Map<Long, List<Order>> ordersByClient = orders.stream()
                .filter(order -> {
                    if (order.getClient() == null) {
                        return false;
                    }
                    if (fromDateTime == null && toDateTime == null) {
                        return true;
                    }
                    LocalDateTime createdAt = order.getCreatedAt();
                    if (createdAt == null) {
                        return false;
                    }
                    boolean afterFrom = fromDateTime == null || !createdAt.isBefore(fromDateTime);
                    boolean beforeTo = toDateTime == null || createdAt.isBefore(toDateTime);
                    return afterFrom && beforeTo;
                })
                .collect(Collectors.groupingBy(order -> order.getClient().getId()));
        
        return ordersByClient.entrySet().stream()
                .map(entry -> {
                    Long clientId = entry.getKey();
                    List<Order> clientOrders = entry.getValue();
                    long ordersCount = clientOrders.size();
                    BigDecimal totalPrice = clientOrders.stream()
                            .map(order -> order.getPrice() != null ? order.getPrice() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    String clientName = clientOrders.get(0).getClient().getName();
                    
                    return TopClientDto.builder()
                            .clientId(clientId)
                            .clientName(clientName)
                            .ordersCount(ordersCount)
                            .totalPrice(totalPrice)
                            .build();
                })
                .sorted(Comparator.comparing(TopClientDto::getTotalPrice, 
                        Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(TopClientDto::getOrdersCount, Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VehicleLoadDto> getVehicleLoad(LocalDate fromDate, LocalDate toDate) {
        List<OrderAssignment> assignments = assignmentRepository.findAllWithRelations();
        
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;
        
        Map<Long, List<OrderAssignment>> assignmentsByVehicle = assignments.stream()
                .filter(assignment -> {
                    if (assignment.getVehicle() == null) {
                        return false;
                    }
                    if (fromDateTime == null && toDateTime == null) {
                        return true;
                    }
                    LocalDateTime plannedStart = assignment.getPlannedStart();
                    if (plannedStart == null) {
                        return false;
                    }
                    boolean afterFrom = fromDateTime == null || !plannedStart.isBefore(fromDateTime);
                    boolean beforeTo = toDateTime == null || plannedStart.isBefore(toDateTime);
                    return afterFrom && beforeTo;
                })
                .collect(Collectors.groupingBy(assignment -> assignment.getVehicle().getId()));
        
        return assignmentsByVehicle.entrySet().stream()
                .map(entry -> {
                    Long vehicleId = entry.getKey();
                    List<OrderAssignment> vehicleAssignments = entry.getValue();
                    long ordersCount = vehicleAssignments.stream()
                            .map(assignment -> assignment.getOrder() != null ? assignment.getOrder().getId() : null)
                            .filter(id -> id != null)
                            .distinct()
                            .count();
                    
                    String registrationNumber = vehicleAssignments.get(0).getVehicle().getRegistrationNumber();
                    
                    return VehicleLoadDto.builder()
                            .vehicleId(vehicleId)
                            .registrationNumber(registrationNumber)
                            .ordersCount(ordersCount)
                            .build();
                })
                .sorted(Comparator.comparing(VehicleLoadDto::getOrdersCount, Comparator.reverseOrder())
                        .thenComparing(VehicleLoadDto::getRegistrationNumber))
                .collect(Collectors.toList());
    }
}

