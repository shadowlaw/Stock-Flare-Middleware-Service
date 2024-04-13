package com.shadow.stock_flare_middleware_service.controller.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreatePortfolioResponse extends Response{

    private String id;
    private String name;
    private String number;
    private String type;

    public CreatePortfolioResponse(int status, String id, String name, String number, String type) {
        super(status);
        this.id = id;
        this.name = name;
        this.number = number;
        this.type = type;
    }
}
