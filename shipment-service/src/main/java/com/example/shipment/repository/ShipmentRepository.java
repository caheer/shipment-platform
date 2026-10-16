package com.example.shipment.repository;

import com.example.shipment.dto.ShipmentDetailsDto;
import com.example.shipment.dto.ShipmentListDto;
import com.example.shipment.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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


/*    @EntityGraph(attributePaths = {
            "shipmentComments"
    })
    List<Shipment> getShipmentsWithComments();*/

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

    // I'm not currently using the ones below, they are here for reference / potential future use
/*
    @EntityGraph(attributePaths = {
            "insurance",
            "insurance.provider",
            "customer",
            "assignedCourier"
    })
    @Query("select s from Shipment s")
    List<Shipment> findAllWithDetails();

    @Query("""
                        select s from Shipment s where s.shipmentStatus = :status
            """)
    List<Shipment> findShipmentsByStatus(@Param("status") ShipmentStatus status);

    @Query("""
                        SELECT s
                        FROM Shipment s
                        WHERE s.shipmentStatus = :status
                        AND LOWER(s.shipmentName) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    List<Shipment> findShipmentsByStatusAndName(ShipmentStatus status, String name);

    @Query("""
                SELECT DISTINCT s
                FROM Shipment s
                LEFT JOIN FETCH s.shipmentComments
            """)
    List<Shipment> findAllShipmentsWithComments();
*/

}
