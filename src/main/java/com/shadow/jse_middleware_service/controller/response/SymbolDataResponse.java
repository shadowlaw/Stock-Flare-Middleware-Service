package com.shadow.jse_middleware_service.controller.response;

import com.shadow.jse_middleware_service.repository.entity.Symbol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Schema(name="Symbol Data Response", description = "Response object returned when call for symbol data is made")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SymbolDataResponse extends Response{
    Page<Symbol> page;

    public SymbolDataResponse(int httpStatus, Page<Symbol> symbolPage) {
        super(httpStatus);
        page = symbolPage;
    }
}
