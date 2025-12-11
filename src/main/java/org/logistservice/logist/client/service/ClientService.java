package org.logistservice.logist.client.service;

import org.logistservice.logist.client.model.dto.ClientCreateUpdateRequest;
import org.logistservice.logist.client.model.dto.ClientDto;
import org.logistservice.logist.common.enums.ClientSortField;
import org.logistservice.logist.common.enums.SortDirection;

import java.util.List;

public interface ClientService {
    List<ClientDto> getAll(String nameFilter, String cityFilter, ClientSortField sortField, SortDirection sortDirection);
    ClientDto getById(Long id);
    ClientDto create(ClientCreateUpdateRequest request);
    ClientDto update(Long id, ClientCreateUpdateRequest request);
    void delete(Long id);
}


