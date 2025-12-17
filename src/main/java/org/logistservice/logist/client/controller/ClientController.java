package org.logistservice.logist.client.controller;

import jakarta.validation.Valid;
import org.logistservice.logist.client.model.dto.ClientCreateUpdateRequest;
import org.logistservice.logist.client.model.dto.ClientDto;
import org.logistservice.logist.client.service.ClientService;
import org.logistservice.logist.common.enums.ClientSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    
    private final ClientService clientService;
    
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    public ResponseEntity<List<ClientDto>> getAllClients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) ClientSortField sortField,
            @RequestParam(required = false, defaultValue = "ASC") SortDirection sortDirection) {
        return ResponseEntity.ok(clientService.getAll(name, city, active, sortField, sortDirection));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OPERATOR')")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ClientDto> createClient(@Valid @RequestBody ClientCreateUpdateRequest request) {
        ClientDto created = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ClientDto> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientCreateUpdateRequest request) {
        return ResponseEntity.ok(clientService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


