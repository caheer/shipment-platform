package com.example.shipment.repository;

import com.example.shipment.dto.ShipmentDetailsDto;
import com.example.shipment.dto.ShipmentListDto;
import com.example.shipment.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    Optional<Shipment> findByShipmentId(UUID shipmentId);

    @Query("""
                select new com.example.shipment.dto.ShipmentListDto(
                    s.shipmentId,
                    c.customerId,
                    c.customerName,
                    s.shipmentName,
                    s.shipmentStatus
                )
                from Shipment s
                left join s.customer c
                            where  s.shipmentId = :shipmentId
            """)
    ShipmentDetailsDto findDtoByShipmentId(UUID shipmentId);

    @Query("""
    SELECT s FROM Shipment s
    LEFT JOIN FETCH s.customer
    LEFT JOIN FETCH s.currentLocation
    LEFT JOIN FETCH s.destinationLocation
    WHERE s.shipmentId = :shipmentId
    """)
    Optional<Shipment> findShipmentWithPdfDetailsByShipmentId(
            @Param("shipmentId") UUID shipmentId);

    @Query("""
                select new com.example.shipment.dto.ShipmentListDto(
                    s.shipmentId,
                    c.customerId,
                    c.customerName,
                    s.shipmentName,
                    s.shipmentStatus
                )
                from Shipment s
                left join s.customer c
            """)
    Page<ShipmentListDto> findShipmentList(Pageable pageable);

/*
    @EntityGraph(attributePaths = {
            "insurance",
            "insurance.provider",
            "customer",
            "assignedCourier",
            "shipmentComments"
    })
    @Query("select s from Shipment s")
    List<Shipment> findAllWithDetails();



    @Query("""
                SELECT DISTINCT s
                FROM Shipment s
                LEFT JOIN FETCH s.shipmentComments
            """)
    List<Shipment> findAllShipmentsWithComments();
*/

}
