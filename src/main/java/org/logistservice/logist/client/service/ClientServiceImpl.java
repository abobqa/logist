package org.logistservice.logist.client.service;

import org.logistservice.logist.client.model.Client;
import org.logistservice.logist.client.model.dto.ClientCreateUpdateRequest;
import org.logistservice.logist.client.model.dto.ClientDto;
import org.logistservice.logist.client.repository.ClientRepository;
import org.logistservice.logist.common.enums.ClientSortField;
import org.logistservice.logist.common.enums.SortDirection;
import org.logistservice.logist.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    
    @Override
    public List<ClientDto> getAll(String nameFilter, String cityFilter, ClientSortField sortField, SortDirection sortDirection) {
        List<Client> clients = clientRepository.findAll();
        
        List<ClientDto> result = clients.stream()
                .filter(client -> {
                    boolean nameMatch = !StringUtils.hasText(nameFilter) ||
                            (client.getName() != null && 
                             client.getName().toLowerCase().contains(nameFilter.toLowerCase()));
                    boolean cityMatch = !StringUtils.hasText(cityFilter) ||
                            (client.getCity() != null && 
                             client.getCity().toLowerCase().contains(cityFilter.toLowerCase()));
                    return nameMatch && cityMatch;
                })
                .map(this::toDto)
                .collect(Collectors.toList());
        
        // Применяем сортировку
        if (sortField != null) {
            Comparator<ClientDto> comparator = getClientComparator(sortField);
            if (sortDirection == SortDirection.DESC) {
                comparator = comparator.reversed();
            }
            result = result.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
        
        return result;
    }
    
    private Comparator<ClientDto> getClientComparator(ClientSortField sortField) {
        return switch (sortField) {
            case NAME -> Comparator.comparing(
                    client -> client.getName() != null ? client.getName() : "",
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case CITY -> Comparator.comparing(
                    client -> client.getCity() != null ? client.getCity() : "",
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case CREATED_AT -> Comparator.comparing(
                    ClientDto::getCreatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }
    
    @Override
    public ClientDto getById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
        return toDto(client);
    }
    
    @Override
    public ClientDto create(ClientCreateUpdateRequest request) {
        Client client = Client.builder()
                .name(request.getName())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .email(request.getEmail())
                .taxNumber(request.getTaxNumber())
                .city(request.getCity())
                .address(request.getAddress())
                .createdAt(LocalDateTime.now())
                .build();
        
        Client saved = clientRepository.save(client);
        return toDto(saved);
    }
    
    @Override
    public ClientDto update(Long id, ClientCreateUpdateRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
        
        updateEntityFromRequest(client, request);
        Client updated = clientRepository.save(client);
        return toDto(updated);
    }
    
    @Override
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new NotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }
    
    private ClientDto toDto(Client entity) {
        return ClientDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .contactPerson(entity.getContactPerson())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .taxNumber(entity.getTaxNumber())
                .city(entity.getCity())
                .address(entity.getAddress())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    
    private void updateEntityFromRequest(Client entity, ClientCreateUpdateRequest request) {
        entity.setName(request.getName());
        entity.setContactPerson(request.getContactPerson());
        entity.setPhone(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setTaxNumber(request.getTaxNumber());
        entity.setCity(request.getCity());
        entity.setAddress(request.getAddress());
    }
}


