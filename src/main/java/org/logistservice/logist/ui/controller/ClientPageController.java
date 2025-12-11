package org.logistservice.logist.ui.controller;

import lombok.RequiredArgsConstructor;
import org.logistservice.logist.client.model.dto.ClientCreateUpdateRequest;
import org.logistservice.logist.client.model.dto.ClientDto;
import org.logistservice.logist.client.service.ClientService;
import org.logistservice.logist.common.enums.ClientSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.common.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/ui/clients")
@RequiredArgsConstructor
public class ClientPageController {
    
    private final ClientService clientService;
    
    @GetMapping
    public String listClients(
            @RequestParam(required = false) ClientSortField sortField,
            @RequestParam(required = false, defaultValue = "ASC") SortDirection sortDirection,
            Model model) {
        
        // По умолчанию сортируем по названию
        if (sortField == null) {
            sortField = ClientSortField.NAME;
        }
        
        List<ClientDto> clients = clientService.getAll(null, null, sortField, sortDirection);
        model.addAttribute("clients", clients);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("availableSortFields", Arrays.asList(ClientSortField.values()));
        return "clients/list";
    }
    
    @GetMapping("/{id}")
    public String viewClient(@PathVariable Long id, Model model) {
        ClientDto client = clientService.getById(id);
        model.addAttribute("client", client);
        return "clients/detail";
    }
    
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String showCreateForm(Model model) {
        model.addAttribute("client", new ClientCreateUpdateRequest());
        return "clients/create";
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String createClient(@ModelAttribute ClientCreateUpdateRequest request, RedirectAttributes redirectAttributes) {
        try {
            ClientDto created = clientService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Клиент успешно создан");
            return "redirect:/ui/clients/" + created.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании клиента: " + e.getMessage());
            return "redirect:/ui/clients/new";
        }
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        ClientDto client = clientService.getById(id);
        ClientCreateUpdateRequest request = ClientCreateUpdateRequest.builder()
                .name(client.getName())
                .contactPerson(client.getContactPerson())
                .phone(client.getPhone())
                .email(client.getEmail())
                .taxNumber(client.getTaxNumber())
                .city(client.getCity())
                .address(client.getAddress())
                .build();
        model.addAttribute("clientId", id);
        model.addAttribute("client", request);
        return "clients/edit";
    }
    
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String updateClient(@PathVariable Long id, @ModelAttribute ClientCreateUpdateRequest request, RedirectAttributes redirectAttributes) {
        try {
            clientService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Клиент успешно обновлен");
            return "redirect:/ui/clients/" + id;
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Клиент не найден");
            return "redirect:/ui/clients";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении клиента: " + e.getMessage());
            return "redirect:/ui/clients/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clientService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Клиент успешно удален");
            return "redirect:/ui/clients";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Клиент не найден");
            return "redirect:/ui/clients";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении клиента: " + e.getMessage());
            return "redirect:/ui/clients";
        }
    }
}




