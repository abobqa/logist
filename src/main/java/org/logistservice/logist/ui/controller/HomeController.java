package org.logistservice.logist.ui.controller;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.driver.repository.DriverRepository;
import org.logistservice.logist.order.repository.OrderRepository;
import org.logistservice.logist.vehicle.model.VehicleStatus;
import org.logistservice.logist.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    
    @GetMapping
    public String index(Model model) {
        long totalOrders = orderRepository.count();
        long activeVehicles = vehicleRepository.countByStatus(VehicleStatus.ACTIVE);
        long activeDrivers = driverRepository.countByActive(true);
        
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("activeVehicles", activeVehicles);
        model.addAttribute("activeDrivers", activeDrivers);
        
        return "index";
    }
}




