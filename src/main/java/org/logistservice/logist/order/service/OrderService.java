package org.logistservice.logist.order.service;

import org.logistservice.logist.common.enums.OrderSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.order.model.OrderStatus;
import org.logistservice.logist.order.model.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    List<OrderDto> getAll(String search, OrderStatus status, Long clientId, LocalDate fromDate, LocalDate toDate, 
                          OrderSortField sortField, SortDirection sortDirection);
    OrderDetailsDto getById(Long id);
    OrderDto create(OrderCreateUpdateRequest request);
    OrderDto update(Long id, OrderCreateUpdateRequest request);
    void delete(Long id);
    OrderDto updateStatus(Long id, OrderStatus newStatus);
    OrderAssignmentDto addAssignment(Long orderId, OrderAssignmentCreateUpdateRequest request);
    OrderAssignmentDto updateAssignment(Long assignmentId, OrderAssignmentCreateUpdateRequest request);
    void deleteAssignment(Long assignmentId);
}


