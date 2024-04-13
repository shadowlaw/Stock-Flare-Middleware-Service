package com.shadow.stock_flare_middleware_service.controller.response;

import com.shadow.stock_flare_middleware_service.repository.entity.PortfolioTrade;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class CreatePortfolioTradeResponse extends Response{

    private List<PortfolioTrade> trades;
    public CreatePortfolioTradeResponse(int status, List<PortfolioTrade> portfolioTrades) {
        super(status);
        this.trades = portfolioTrades;
    }
}
