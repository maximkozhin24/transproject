package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.ClientRequestDto;
import com.logistics.logisticsapp.dto.ClientResponseDto;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.entity.Client;
import com.logistics.logisticsapp.entity.Order;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import com.logistics.logisticsapp.exception.ConflictException;
import com.logistics.logisticsapp.exception.ResourceNotFoundException;
import com.logistics.logisticsapp.repository.CargoRepository;
import com.logistics.logisticsapp.repository.ClientRepository;
import com.logistics.logisticsapp.repository.OrderRepository;
import com.logistics.logisticsapp.repository.RouteVehicleCargoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private RouteVehicleCargoRepository rvcRepository;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientRequestDto requestDto;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setName("John");
        client.setEmail("john@mail.com");
        client.setPhone("123");

        requestDto = new ClientRequestDto();
        requestDto.setName("John");
        requestDto.setEmail("john@mail.com");
        requestDto.setPhone("123");
    }

    // ---------------- GET ALL ----------------

    @Test
    void getAll_shouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(client));

        List<ClientResponseDto> result = clientService.getAll();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getById_shouldReturnClient() {
        when(repository.findById(1L)).thenReturn(Optional.of(client));

        ClientResponseDto result = clientService.getById(1L);

        assertNotNull(result);
        verify(repository).findById(1L);
    }

    @Test
    void getById_shouldThrowIfNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> clientService.getById(1L));
    }

    // ---------------- CREATE ----------------

    @Test
    void create_shouldSaveClient() {
        when(repository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(repository.existsByPhone(requestDto.getPhone())).thenReturn(false);

        when(repository.save(any(Client.class))).thenAnswer(invocation -> {
            Client c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        ClientResponseDto result = clientService.create(requestDto);

        assertNotNull(result);
        verify(repository).save(any(Client.class));
    }

    @Test
    void create_shouldThrowIfEmailExists() {
        when(repository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class,
            () -> clientService.create(requestDto));
    }

    @Test
    void create_shouldThrowIfPhoneExists() {
        when(repository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(repository.existsByPhone(requestDto.getPhone())).thenReturn(true);

        assertThrows(ConflictException.class,
            () -> clientService.create(requestDto));
    }

    // ---------------- UPDATE ----------------

    @Test
    void update_shouldModifyClient() {
        when(repository.findById(1L)).thenReturn(Optional.of(client));
        when(repository.save(any(Client.class))).thenReturn(client);

        ClientResponseDto result = clientService.update(1L, requestDto);

        assertNotNull(result);
        verify(repository).save(client);
    }

    @Test
    void update_shouldThrowIfNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> clientService.update(1L, requestDto));
    }

    // ---------------- DELETE (cascade logic) ----------------

    @Test
    void delete_shouldRemoveClientOrdersAndRelations() {

        Order order = new Order();
        order.setId(10L);

        Cargo cargo = new Cargo();
        cargo.setId(100L);

        RouteVehicleCargo rvc = mock(RouteVehicleCargo.class);

        when(rvc.getCargo()).thenReturn(cargo);

        when(repository.findById(1L)).thenReturn(Optional.of(client));
        when(orderRepository.findAllByClientId(1L)).thenReturn(List.of(order));
        when(rvcRepository.findAllByOrderId(10L)).thenReturn(List.of(rvc));

        when(rvcRepository.existsByCargoId(100L)).thenReturn(false);

        clientService.delete(1L);

        verify(rvcRepository).deleteAll(anyList());
        verify(cargoRepository).delete(cargo);
        verify(orderRepository).delete(order);
        verify(repository).delete(client);
    }

    @Test
    void delete_shouldThrowIfClientNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> clientService.delete(1L));
    }

    // ---------------- EDGE CASE ----------------

    @Test
    void delete_shouldNotDeleteCargoIfUsedElsewhere() {

        Order order = new Order();
        order.setId(10L);

        Cargo cargo = new Cargo();
        cargo.setId(100L);

        RouteVehicleCargo rvc = mock(RouteVehicleCargo.class);
        when(rvc.getCargo()).thenReturn(cargo);

        when(repository.findById(1L)).thenReturn(Optional.of(client));
        when(orderRepository.findAllByClientId(1L)).thenReturn(List.of(order));
        when(rvcRepository.findAllByOrderId(10L)).thenReturn(List.of(rvc));

        // cargo still used somewhere else
        when(rvcRepository.existsByCargoId(100L)).thenReturn(true);

        clientService.delete(1L);

        verify(cargoRepository, never()).delete(any());
        verify(orderRepository).delete(order);
        verify(repository).delete(client);
    }
}
