package com.shadow.stock_flare_middleware_service.service;

import com.shadow.stock_flare_middleware_service.exception.ResourceNotFoundException;
import com.shadow.stock_flare_middleware_service.repository.PortfolioRepository;
import com.shadow.stock_flare_middleware_service.repository.PortfolioTradeRepository;
import com.shadow.stock_flare_middleware_service.repository.UserRepository;
import com.shadow.stock_flare_middleware_service.repository.entity.Portfolio;
import com.shadow.stock_flare_middleware_service.repository.entity.PortfolioTrade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PortfolioManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioTradeRepository portfolioTradeRepository;

    public Optional<Portfolio> createPortfolio(String userId, String name, String number, String type) {
        log.info("creating portfolio with name [{}] for user with id [{}]", name, userId);

        if (!userRepository.existsById(Integer.valueOf(userId))) {
            log.error("Unable to find user id [{}]", userId);
            throw new ResourceNotFoundException("User details not found", null);
        }

        try{
            Portfolio portfolio = new Portfolio();
            portfolio.setNickname(name);
            portfolio.setExternalId(number);
            portfolio.setUserId(userId);
            portfolio.setType(type);
            portfolio = portfolioRepository.save(portfolio);
            log.info("portfolio [{}] created with id [{}]", name, portfolio.getId());
            return Optional.of(portfolio);
        } catch (Exception e) {
            log.error("{}: {}", e.getClass(), e.getMessage());
            return Optional.empty();
        }
    }

    public PortfolioTrade createTrade(String portfolioId, String symbolId, BigDecimal noOfUnits, BigDecimal amountPerUnit, String type, LocalDate transactionDate, BigDecimal taxes, BigDecimal brokerFees, BigDecimal otherFees) {
        log.info("creating [{}] trade for portfolio [{}]", type, portfolioId);
        log.debug("P_ID: [{}] | SYMBOL: [{}] | TYPE: [{}} | VOLUME: [{}]", portfolioId, symbolId, type, amountPerUnit);

        if (!portfolioRepository.existsById(portfolioId)) {
            log.error("Unable to find portfolio");
            throw new ResourceNotFoundException("Unknown portfolio ID", null);
        }

        PortfolioTrade portfolioTrade = null;

        try {
            PortfolioTrade trade = new PortfolioTrade();
            trade.setSymbol(symbolId);
            trade.setPortfolioId(portfolioId);
            trade.setNoOfUnits(noOfUnits);
            trade.setAmountPerUnit(amountPerUnit);
            trade.setType(type);
            trade.setTransactionDate(transactionDate);
            trade.setTaxes(taxes);
            trade.setBrokerFees(brokerFees);
            trade.setOtherFees(otherFees);
            portfolioTrade = portfolioTradeRepository.save(trade);
            log.info("Trade stored");
        } catch (Exception e) {
            log.error("{}: {}", e.getClass(), e.getMessage());
        }

        return portfolioTrade;
    }
}
