package org.logistservice.logist.ui.controller;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.client.service.ClientService;
import org.logistservice.logist.common.enums.DriverSortField;
import org.logistservice.logist.common.enums.OrderSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.common.enums.VehicleSortField;
import org.logistservice.logist.common.exception.NotFoundException;
import org.logistservice.logist.driver.service.DriverService;
import org.logistservice.logist.order.model.dto.OrderAssignmentCreateUpdateRequest;
import org.logistservice.logist.order.model.dto.OrderAssignmentDto;
import org.logistservice.logist.order.model.dto.OrderCreateUpdateRequest;
import org.logistservice.logist.order.model.dto.OrderDetailsDto;
import org.logistservice.logist.order.model.dto.OrderDto;
import org.logistservice.logist.order.service.OrderService;
import org.logistservice.logist.vehicle.service.VehicleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/ui/orders")
@RequiredArgsConstructor
public class OrderPageController {
    
    private final OrderService orderService;
    private final ClientService clientService;
    private final DriverService driverService;
    private final VehicleService vehicleService;
    
    @GetMapping
    public String listOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OrderSortField sortField,
            @RequestParam(required = false, defaultValue = "ASC") SortDirection sortDirection,
            Model model) {
        
        // По умолчанию сортируем по дате создания (новые сначала)
        if (sortField == null) {
            sortField = OrderSortField.CREATED_AT;
            sortDirection = SortDirection.DESC;
        }
        
        List<OrderDto> orders = orderService.getAll(search, null, null, null, null, sortField, sortDirection);
        model.addAttribute("search", search);
        model.addAttribute("orders", orders);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("availableSortFields", Arrays.asList(OrderSortField.values()));
        return "orders/list";
    }
    
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        OrderDetailsDto order = orderService.getById(id);
        model.addAttribute("order", order);
        return "orders/detail";
    }
    
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new OrderCreateUpdateRequest());
        model.addAttribute("clients", clientService.getAll(null, null, null, null, null));
        model.addAttribute("drivers", driverService.getAll(null, true, DriverSortField.FULL_NAME, SortDirection.ASC));
        model.addAttribute("vehicles", vehicleService.getAll(null, null, VehicleSortField.REGISTRATION_NUMBER, SortDirection.ASC));
        return "orders/create";
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String createOrder(@ModelAttribute OrderCreateUpdateRequest request, RedirectAttributes redirectAttributes) {
        try {
            OrderDto created = orderService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно создан");
            return "redirect:/ui/orders/" + created.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании заказа: " + e.getMessage());
            return "redirect:/ui/orders/new";
        }
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        OrderDetailsDto detailedOrder = orderService.getById(id);
        OrderDto order = detailedOrder.getOrder();
        OrderCreateUpdateRequest request = OrderCreateUpdateRequest.builder()
                .clientId(order.getClientId())
                .plannedPickupDate(order.getPlannedPickupDate())
                .plannedDeliveryDate(order.getPlannedDeliveryDate())
                .originCity(order.getOriginCity())
                .originAddress(order.getOriginAddress())
                .destinationCity(order.getDestinationCity())
                .destinationAddress(order.getDestinationAddress())
                .build();
        // Получаем текущие назначения для предзаполнения
        if (!detailedOrder.getAssignments().isEmpty()) {
            var firstAssignment = detailedOrder.getAssignments().get(0);
            request.setDriverId(firstAssignment.getDriverId());
            request.setVehicleId(firstAssignment.getVehicleId());
        }
        request.setPrice(order.getPrice());
        request.setCargoWeight(order.getCargoWeight());
        request.setCargoVolume(order.getCargoVolume());
        request.setCargoDescription(order.getCargoDescription());
        
        model.addAttribute("orderId", id);
        model.addAttribute("order", request);
        model.addAttribute("clients", clientService.getAll(null, null, null, null, null));
        model.addAttribute("drivers", driverService.getAll(null, true, DriverSortField.FULL_NAME, SortDirection.ASC));
        model.addAttribute("vehicles", vehicleService.getAll(null, null, VehicleSortField.REGISTRATION_NUMBER, SortDirection.ASC));
        return "orders/edit";
    }
    
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String updateOrder(@PathVariable Long id, @ModelAttribute OrderCreateUpdateRequest request, RedirectAttributes redirectAttributes) {
        try {
            orderService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно обновлен");
            return "redirect:/ui/orders/" + id;
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Заказ не найден");
            return "redirect:/ui/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении заказа: " + e.getMessage());
            return "redirect:/ui/orders/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно удален");
            return "redirect:/ui/orders";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Заказ не найден");
            return "redirect:/ui/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении заказа: " + e.getMessage());
            return "redirect:/ui/orders";
        }
    }
    
    @GetMapping("/{orderId}/assignments/{assignmentId}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String showEditAssignmentForm(@PathVariable Long orderId, @PathVariable Long assignmentId, Model model) {
        OrderDetailsDto order = orderService.getById(orderId);
        OrderAssignmentDto assignment = order.getAssignments().stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));
        
        OrderAssignmentCreateUpdateRequest request = OrderAssignmentCreateUpdateRequest.builder()
                .vehicleId(assignment.getVehicleId())
                .driverId(assignment.getDriverId())
                .plannedStart(assignment.getPlannedStart())
                .plannedEnd(assignment.getPlannedEnd())
                .actualStart(assignment.getActualStart())
                .actualEnd(assignment.getActualEnd())
                .build();
        
        model.addAttribute("orderId", orderId);
        model.addAttribute("assignment", assignment); // Для отображения информации о водителе и транспорте
        model.addAttribute("assignmentRequest", request); // Для формы
        return "orders/assignment-edit";
    }
    
    @PostMapping("/{orderId}/assignments/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String updateAssignment(@PathVariable Long orderId, 
                                   @PathVariable Long assignmentId,
                                   @ModelAttribute("assignmentRequest") OrderAssignmentCreateUpdateRequest request,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Убеждаемся, что vehicleId и driverId установлены
            if (request.getVehicleId() == null || request.getDriverId() == null) {
                OrderDetailsDto order = orderService.getById(orderId);
                OrderAssignmentDto assignment = order.getAssignments().stream()
                        .filter(a -> a.getId().equals(assignmentId))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Assignment not found"));
                if (request.getVehicleId() == null) {
                    request.setVehicleId(assignment.getVehicleId());
                }
                if (request.getDriverId() == null) {
                    request.setDriverId(assignment.getDriverId());
                }
            }
            
            orderService.updateAssignment(assignmentId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Назначение успешно обновлено");
            return "redirect:/ui/orders/" + orderId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении назначения: " + e.getMessage());
            return "redirect:/ui/orders/" + orderId + "/assignments/" + assignmentId + "/edit";
        }
    }
}




