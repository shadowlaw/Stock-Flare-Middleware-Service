package com.shadow.jse_middleware_service.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Response extends RepresentationModel<Response> {

    @Schema(description = "Response Status Code")
    private int status;
}
