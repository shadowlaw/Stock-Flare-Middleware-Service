package com.shadow.stock_flare_middleware_service.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Schema(name="Paged Data Response", description = "Response object returned when call for paged data is made")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PagedDataResponse extends Response{

    Page<?> page;
    public PagedDataResponse(int httpStatus, Page<?> pageData) {
        super(httpStatus);
        page=pageData;
    }
}
