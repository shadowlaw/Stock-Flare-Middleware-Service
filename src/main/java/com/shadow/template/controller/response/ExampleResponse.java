package com.shadow.template.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExampleResponse extends RepresentationModel<ExampleResponse> {

    @Schema(description = "Response Status Code")
    private String status;
}
