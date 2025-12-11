package org.logistservice.logist.ui.controller;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.common.enums.DriverSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.driver.model.dto.DriverCreateUpdateRequest;
import org.logistservice.logist.driver.model.dto.DriverDto;
import org.logistservice.logist.driver.service.DriverService;
import org.logistservice.logist.common.exception.NotFoundException;
import org.logistservice.logist.order.model.dto.OrderAssignmentDto;
import org.logistservice.logist.order.repository.OrderAssignmentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ui/drivers")
@RequiredArgsConstructor
public class DriverPageController {
    
    private final DriverService driverService;
    private final OrderAssignmentRepository assignmentRepository;
    
    @GetMapping
    public String listDrivers(
            @RequestParam(required = false) DriverSortField sortField,
            @RequestParam(required = false, defaultValue = "ASC") SortDirection sortDirection,
            Model model) {
        
        // По умолчанию сортируем по ФИО
        if (sortField == null) {
            sortField = DriverSortField.FULL_NAME;
        }
        
        List<DriverDto> drivers = driverService.getAll(null, null, sortField, sortDirection);
        model.addAttribute("drivers", drivers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("availableSortFields", Arrays.asList(DriverSortField.values()));
        return "drivers/list";
    }
    
    @GetMapping("/{id}")
    public String viewDriver(@PathVariable Long id, Model model) {
        DriverDto driver = driverService.getById(id);
        model.addAttribute("driver", driver);
        
        // Получаем назначения водителя на заказы
        List<OrderAssignmentDto> assignments = assignmentRepository.findByDriverId(id).stream()
                .map(assignment -> OrderAssignmentDto.builder()
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
                        .build())
                .sorted(Comparator.comparing(OrderAssignmentDto::getPlannedStart, 
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        
        model.addAttribute("assignments", assignments);
        return "drivers/detail";
    }
    
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String showCreateForm(Model model) {
        model.addAttribute("driver", new DriverCreateUpdateRequest());
        return "drivers/create";
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String createDriver(@ModelAttribute DriverCreateUpdateRequest request, RedirectAttributes redirectAttributes) {
        try {
            DriverDto created = driverService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Водитель успешно создан");
            return "redirect:/ui/drivers/" + created.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании водителя: " + e.getMessage());
            return "redirect:/ui/drivers/new";
        }
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        DriverDto driver = driverService.getById(id);
        DriverCreateUpdateRequest request = DriverCreateUpdateRequest.builder()
                .fullName(driver.getFullName())
                .phone(driver.getPhone())
                .drivingLicense(driver.getDrivingLicense())
                .experienceYears(driver.getExperienceYears())
                .active(driver.getActive())
                .build();
        model.addAttribute("driverId", id);
        model.addAttribute("driver", request);
        return "drivers/edit";
    }
    
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String updateDriver(@PathVariable Long id, @ModelAttribute DriverCreateUpdateRequest request, RedirectAttributes redirectAttributes) {
        try {
            driverService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Водитель успешно обновлен");
            return "redirect:/ui/drivers/" + id;
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Водитель не найден");
            return "redirect:/ui/drivers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении водителя: " + e.getMessage());
            return "redirect:/ui/drivers/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteDriver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            driverService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Водитель успешно удален");
            return "redirect:/ui/drivers";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Водитель не найден");
            return "redirect:/ui/drivers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении водителя: " + e.getMessage());
            return "redirect:/ui/drivers";
        }
    }
}




