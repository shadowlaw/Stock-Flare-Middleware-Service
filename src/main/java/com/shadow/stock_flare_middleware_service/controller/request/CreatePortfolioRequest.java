package com.shadow.stock_flare_middleware_service.controller.request;

import com.shadow.stock_flare_middleware_service.annotations.ValidEnumConstant;
import com.shadow.stock_flare_middleware_service.constants.PortfolioType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.shadow.stock_flare_middleware_service.constants.Validation.PORTFOLIO_NICKNAME_REGEX;
import static com.shadow.stock_flare_middleware_service.constants.Validation.PORTFOLIO_IDENTIFIER_REGEX;

@Schema(name = "Portfolio Creation Request", description = "Request object for creating a portfolio")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CreatePortfolioRequest {

    @Schema(description = "Portfolio nickname", required = true)
    @Pattern(regexp = PORTFOLIO_NICKNAME_REGEX, message = "Nickname invalid. Nickname must be alphanumeric and can contain _")
    @NotBlank(message = "Field is required")
    String nickname;

    @Schema(description = "Portfolio identifier", required = true)
    @Pattern(regexp = PORTFOLIO_IDENTIFIER_REGEX, message = "Identifier invalid. Identifier must be alphanumeric and can contain _")
    @NotBlank(message = "Field is required")
    String identifier;

    @Schema(description = "Type of portfolio", required = true)
    @ValidEnumConstant(enumClazz = PortfolioType.class)
    String type;

}
