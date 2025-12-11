package org.logistservice.logist.ui.controller;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.common.enums.VehicleSortField;
import org.logistservice.logist.vehicle.model.VehicleStatus;
import org.logistservice.logist.vehicle.model.dto.VehicleCreateUpdateRequest;
import org.logistservice.logist.vehicle.model.dto.VehicleDto;
import org.logistservice.logist.vehicle.service.VehicleService;
import org.logistservice.logist.common.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/ui/vehicles")
@RequiredArgsConstructor
public class VehiclePageController {
    
    private final VehicleService vehicleService;
    
    @GetMapping
    public String listVehicles(
            @RequestParam(required = false) VehicleSortField sortField,
            @RequestParam(required = false, defaultValue = "ASC") SortDirection sortDirection,
            Model model) {
        
        // По умолчанию сортируем по гос. номеру
        if (sortField == null) {
            sortField = VehicleSortField.REGISTRATION_NUMBER;
        }
        
        List<VehicleDto> vehicles = vehicleService.getAll(null, null, sortField, sortDirection);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("availableSortFields", Arrays.asList(VehicleSortField.values()));
        return "vehicles/list";
    }
    
    @GetMapping("/{id}")
    public String viewVehicle(@PathVariable Long id, Model model) {
        VehicleDto vehicle = vehicleService.getById(id);
        model.addAttribute("vehicle", vehicle);
        return "vehicles/detail";
    }
    
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicle", new VehicleCreateUpdateRequest());
        model.addAttribute("statuses", Arrays.asList(VehicleStatus.values()));
        return "vehicles/create";
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String createVehicle(@ModelAttribute VehicleCreateUpdateRequest request, RedirectAttributes redirectAttributes) {
        try {
            VehicleDto created = vehicleService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Транспортное средство успешно создано");
            return "redirect:/ui/vehicles/" + created.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании транспорта: " + e.getMessage());
            return "redirect:/ui/vehicles/new";
        }
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        VehicleDto vehicle = vehicleService.getById(id);
        VehicleCreateUpdateRequest request = VehicleCreateUpdateRequest.builder()
                .registrationNumber(vehicle.getRegistrationNumber())
                .type(vehicle.getType())
                .capacityWeight(vehicle.getCapacityWeight())
                .capacityVolume(vehicle.getCapacityVolume())
                .status(vehicle.getStatus())
                .build();
        model.addAttribute("vehicleId", id);
        model.addAttribute("vehicle", request);
        model.addAttribute("statuses", Arrays.asList(VehicleStatus.values()));
        return "vehicles/edit";
    }
    
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String updateVehicle(@PathVariable Long id, @ModelAttribute VehicleCreateUpdateRequest request, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Транспортное средство успешно обновлено");
            return "redirect:/ui/vehicles/" + id;
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Транспортное средство не найдено");
            return "redirect:/ui/vehicles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении транспорта: " + e.getMessage());
            return "redirect:/ui/vehicles/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Транспортное средство успешно удалено");
            return "redirect:/ui/vehicles";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Транспортное средство не найдено");
            return "redirect:/ui/vehicles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении транспорта: " + e.getMessage());
            return "redirect:/ui/vehicles";
        }
    }
}




