package org.logistservice.logist.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.logistservice.logist.common.enums.OrderSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.common.exception.BadRequestException;
import org.logistservice.logist.order.model.OrderStatus;
import org.logistservice.logist.order.model.dto.*;
import org.logistservice.logist.order.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    public ResponseEntity<List<OrderDto>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) OrderSortField sortField,
            @RequestParam(required = false, defaultValue = "ASC") SortDirection sortDirection) {
        
        OrderStatus orderStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                orderStatus = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid order status: " + status);
            }
        }
        
        return ResponseEntity.ok(orderService.getAll(orderStatus, clientId, fromDate, toDate, sortField, sortDirection));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    public ResponseEntity<OrderDetailsDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderCreateUpdateRequest request) {
        OrderDto created = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<OrderDto> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderCreateUpdateRequest request) {
        return ResponseEntity.ok(orderService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.getNewStatus()));
    }
    
    @PostMapping("/{orderId}/assignments")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<OrderAssignmentDto> addAssignment(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderAssignmentCreateUpdateRequest request) {
        OrderAssignmentDto created = orderService.addAssignment(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/assignments/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<OrderAssignmentDto> updateAssignment(
            @PathVariable Long assignmentId,
            @Valid @RequestBody OrderAssignmentCreateUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateAssignment(assignmentId, request));
    }
    
    @DeleteMapping("/assignments/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long assignmentId) {
        orderService.deleteAssignment(assignmentId);
        return ResponseEntity.noContent().build();
    }
}


