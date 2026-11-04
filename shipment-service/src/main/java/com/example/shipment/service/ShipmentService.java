package com.example.shipment.service;

import com.example.shipment.dto.*;
import com.example.shipment.exception.*;
import com.example.shipment.kafka.ShipmentEventProducer;
import com.example.shipment.kafka.ShipmentStatusChangedEvent;
import com.example.shipment.model.*;
import com.example.shipment.repository.*;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final EventRepository eventRepository;
    private final ShipmentEventRepository shipmentEventRepository;
    private final ShipmentSearchByCriteriaRepository shipmentSearchByCriteriaRepository;
    private final ShipmentEventProducer shipmentEventProducer;
    private final ShipmentCommentRepository shipmentCommentRepository;
    private final CustomerRepository customerRepository;
    private final CourierRepository courierRepository;
    private final LocationRepository locationRepository;

    private static final long SHIPMENT_CREATED_EVENT_CODE = 4000000001L;

    public ShipmentService(ShipmentRepository shipmentRepository, EventRepository eventRepository, ShipmentEventRepository shipmentEventRepository, ShipmentSearchByCriteriaRepository shipmentSearchByCriteriaRepository, EntityManager entityManager, ShipmentEventProducer shipmentEventProducer, ShipmentCommentRepository shipmentCommentRepository, CustomerRepository customerRepository, CourierRepository courierRepository, LocationRepository locationRepository) {
        this.shipmentRepository = shipmentRepository;
        this.eventRepository = eventRepository;
        this.shipmentEventRepository = shipmentEventRepository;
        this.shipmentSearchByCriteriaRepository = shipmentSearchByCriteriaRepository;
        this.shipmentEventProducer = shipmentEventProducer;
        this.shipmentCommentRepository = shipmentCommentRepository;
        this.customerRepository = customerRepository;
        this.courierRepository = courierRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public ShipmentDto createShipment(ShipmentCreateDto shipmentCreateDto) {

        Customer customer = null;
        Location currentLocation = null;
        Location destinationLocation = null;
        Courier assignedCourier = null;

        if (shipmentCreateDto.customerId() != null) {
            customer = customerRepository.findById(shipmentCreateDto.customerId())
                    .orElseThrow(() ->
                            new CustomerNotFoundException(shipmentCreateDto.customerId()));
        }

        if (shipmentCreateDto.currentLocationId() != null) {
            currentLocation = locationRepository.findById(shipmentCreateDto.currentLocationId())
                    .orElseThrow(() ->
                            new LocationNotFoundException(shipmentCreateDto.currentLocationId()));
        }

        if (shipmentCreateDto.destinationLocationId() != null) {
            destinationLocation = locationRepository.findById(shipmentCreateDto.destinationLocationId())
                    .orElseThrow(() ->
                            new LocationNotFoundException(shipmentCreateDto.destinationLocationId()));
        }

        if (shipmentCreateDto.assignedCourierId() != null) {
            assignedCourier = courierRepository.findById(shipmentCreateDto.assignedCourierId())
                    .orElseThrow(() ->
                            new CourierNotFoundException(shipmentCreateDto.assignedCourierId()));
        }

        LocalDateTime currentDate = LocalDateTime.now();

        Shipment newShipment = new Shipment(UUID.randomUUID(), shipmentCreateDto.shipmentName(), ShipmentStatus.CREATED,
                shipmentCreateDto.shippingType(), shipmentCreateDto.shipmentDescription(),
                customer, currentLocation, destinationLocation, assignedCourier,
                shipmentCreateDto.weightKg(), shipmentCreateDto.declaredValue(), null, currentDate);
        Shipment savedShipment = shipmentRepository.save(newShipment);

        Event event = eventRepository
                .findByEventCode(SHIPMENT_CREATED_EVENT_CODE)
                .orElseThrow(() ->
                        new IllegalStateException("CREATED event is not configured"));

        ShipmentEvent shipmentEvent = new ShipmentEvent(UUID.randomUUID(), savedShipment, event, currentDate);
        shipmentEventRepository.save(shipmentEvent);

        return new ShipmentDto(savedShipment.getShipmentId(), savedShipment.getShipmentName());
    }


    @Transactional
    public String updateShipmentStatus(
            UUID shipmentId,
            ShipmentStatus shipmentStatus) {

        Shipment shipment = shipmentRepository.findByShipmentId(shipmentId)
                .orElseThrow(() ->
                        new ShipmentNotFoundException(shipmentId));

        shipment.setShipmentStatus(shipmentStatus);

        Event event = eventRepository.findByEventName(shipmentStatus.name()).orElseThrow(() -> new EventConfigurationException(shipmentStatus.name()));
        ShipmentEvent shipmentEvent = new ShipmentEvent(UUID.randomUUID(), shipment, event, LocalDateTime.now());
        shipmentEventRepository.save(shipmentEvent);

        shipmentEventProducer.publish(
                new ShipmentStatusChangedEvent(
                        shipmentId,
                        shipmentStatus,
                        LocalDateTime.now()
                )
        );

        return "Shipment " + shipmentId + "status updated to: " + shipmentStatus.getLabel();
    }

    @Transactional
    public Page<ShipmentListDto> getShipmentListPage(int page, int size) {
        return shipmentRepository.findShipmentList(PageRequest.of(page, size));
    }

    @Transactional
    public void searchShipmentsByCriteria() {
        ShipmentSearchCriteria shipmentSearchCriteria = new ShipmentSearchCriteria();
        shipmentSearchCriteria.setShipmentName("TEST");
        shipmentSearchCriteria.setStatus(ShipmentStatus.IN_TRANSIT);
        List<Shipment> shipmentList = shipmentSearchByCriteriaRepository.searchShipmentByCriteria(shipmentSearchCriteria);

        shipmentList.forEach(s ->
                System.out.println(
                        s.getShipmentName() + " | " + s.getShipmentStatus()
                )
        );
    }

    @Transactional
    public ShipmentDetailsDto getShipmentById(UUID shipmentId) {
        return shipmentRepository.findDtoByShipmentId(shipmentId);
    }


    @Transactional(readOnly = true)
    public ShipmentPdfDataDto getShipmentPdfDataByShipmentId(UUID shipmentId) {

        Shipment shipment = shipmentRepository.findShipmentWithPdfDetailsByShipmentId(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException(shipmentId));

        List<ShipmentCommentDto> comments = shipmentCommentRepository
                .findByShipment_ShipmentIdOrderByCommentDateDesc(shipmentId)
                .stream()
                .map(c -> new ShipmentCommentDto(
                        c.getCommentId(),
                        c.getCommentText(),
                        c.getCommentDate(),
                        c.getAddedBy(),
                        c.isPriorityAction()))
                .toList();

        return new ShipmentPdfDataDto(
                shipment.getShipmentId(),
                shipment.getShipmentName(),
                shipment.getShipmentDescription(),
                shipment.getShipmentStatus().getLabel(),
                shipment.getShippingType().name(),
                shipment.getCustomer().getCustomerName(),
                shipment.getCustomer().getCustomerEmail(),
                shipment.getCurrentLocation().getLocationName(),
                shipment.getDestinationLocation().getLocationName(),
                comments
        );
    }
}