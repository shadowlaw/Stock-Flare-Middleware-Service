package com.shadow.template.controller;

import com.shadow.template.controller.response.ErrorResponse;
import com.shadow.template.controller.response.ExampleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.shadow.template.constants.LoggingConstants.REQUEST_ID;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "QR Code Endpoints", description = "QR Code Related Endpoints")
@RestController
@RequestMapping("api")
public class ExampleController {

    Logger logger = LoggerFactory.getLogger(ExampleController.class);

    @Operation(summary = "Example",description = "Example endpoint ", tags = "Home")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request processed", content = @Content(schema = @Schema(implementation = ExampleResponse.class), mediaType =  MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "The user submitted Bad Request.", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping(value = "example")
    public ResponseEntity<?> example() {
        MDC.put(REQUEST_ID, "EXAMPLE_PROCESS");
        ExampleResponse response = new ExampleResponse();
        response.setStatus(HttpStatus.OK.name());
        response.add(linkTo(methodOn(ExampleController.class).example()).withSelfRel());
        return ResponseEntity.ok(response);
    }

}
