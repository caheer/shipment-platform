package com.example.shipmentexperiments.randomcontrollers;

import com.example.shipment.dto.ShipmentDetailsDto;
import com.example.shipment.service.ShipmentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cheatsheet")
public class CheatSheetController {

    private final ShipmentService shipmentService;

    public CheatSheetController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

//1. @PathVariable — parameters in the URL path

 //   i.e  GET /api/shipments/123
/*    @PathVariable UUID shipmentId
    @PathVariable UUID ("shipmentId")*/

    // more parameters
    // GET /api/shipments/123/comments/45
//@GetMapping("/{shipmentId}/comments/{commentId}")  - getComment(
//        @PathVariable Long shipmentId,
//        @PathVariable Long commentId

    @GetMapping("/{shipmentId}")
    @ResponseBody  // if we use @RestController and not @Controller this is optional
    public ShipmentDetailsDto getShipmentDetails(@PathVariable UUID shipmentId) {
        return shipmentService.getShipmentById(shipmentId);
    }

    //2. @RequestParam — query string parameters

    // GET /api/shipments?status=NEW&page=0&size=20
    // ? after url, paramName=VALUE and then & between next parameters
    // optional settings for default or mandatory
    @GetMapping("/search")
    public List<ShipmentDetailsDto> findShipments(
            @RequestParam String status,
            @RequestParam(required = false) int page,
            @RequestParam(defaultValue = "20") int size) {
        // ...
        return null;
    }

    // GET /api/shipments?status=NEW&status=DELIVERED
    // variation of the above - but here it's the same parameter name (status) and goes into a List

    @GetMapping("/search-by-statuses")
    public List<ShipmentDetailsDto> findShipmentsByStatuses(
            @RequestParam List<String> status) {
        // ...
        return null;
    }

    // 3. @RequestBody — data in the HTTP request body, Spring deserializes the JSON into a Java object, typically using Jackson.
    //  POST /api/createNewShipment

    @PostMapping("/createNewShipment")
    public ShipmentDetailsDto createNewShipment(@RequestBody ShipmentDetailsDto request) {

        return shipmentService.createNewShipment(request);
    }
    // ...createShipment(@Valid @RequestBody CreateShipmentRequest request)...
    //@Valid triggers validation of constraints defined on the DTO, such as @NotNull or @NotBlank, when Bean Validation is configured.

//4. @RequestHeader — HTTP headers - Used when you need information supplied in HTTP headers.
    //GET /api/shipments/123
    //and in request header we have X-Correlation-ID: abc-123

    @GetMapping("/{id}")
    public ShipmentDetailsDto getShipment(
            @PathVariable Long id,
            @RequestHeader("X-Correlation-ID") String correlationId) {
        // ...
        return null;
    }

    // Less relevant
   // 5. @RequestPart	Multipart request,	Uploaded file or JSON part
  // 6.  @ModelAttribute	Request/form parameters	, Bind multiple fields to DTO



    //RequestEntity<T> — incoming HTTP request
    //Provides access to the request body, headers, HTTP method, and URI.

    @PostMapping("/requestEntExample")
    public ResponseEntity<Void> createShipment(
            RequestEntity<ShipmentDetailsDto> request) {

        ShipmentDetailsDto dto = request.getBody();
        HttpHeaders headers = request.getHeaders();
        HttpMethod method = request.getMethod();
        URI uri = request.getUrl();

        //shipmentService.create(dto);

        return ResponseEntity.ok().build();
    }


    //ResponseEntity<T> — outgoing HTTP response
    //Provides control over the response body, HTTP status, and headers.

    @GetMapping("/responseEntExample/{id}")
    public ResponseEntity<ShipmentDetailsDto> getShipment(
            @PathVariable UUID id) {

        ShipmentDetailsDto dto = shipmentService.getShipmentById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("X-Shipment-Status", "FOUND")
                .body(dto);
    }

}
