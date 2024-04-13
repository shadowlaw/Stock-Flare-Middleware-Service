package com.shadow.stock_flare_middleware_service.service;

import com.shadow.stock_flare_middleware_service.constants.PortfolioType;
import com.shadow.stock_flare_middleware_service.constants.TradeType;
import com.shadow.stock_flare_middleware_service.exception.RequestDateRangeException;
import com.shadow.stock_flare_middleware_service.exception.ResourceNotFoundException;
import com.shadow.stock_flare_middleware_service.repository.DividendPaymentRepository;
import com.shadow.stock_flare_middleware_service.repository.PortfolioRepository;
import com.shadow.stock_flare_middleware_service.repository.PortfolioTradeRepository;
import com.shadow.stock_flare_middleware_service.repository.UserRepository;
import com.shadow.stock_flare_middleware_service.repository.entity.DividendPayment;
import com.shadow.stock_flare_middleware_service.repository.entity.Portfolio;
import com.shadow.stock_flare_middleware_service.repository.entity.PortfolioTrade;
import org.hibernate.JDBCException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.shadow.stock_flare_middleware_service.util.CustomLocalDateUtils.addDays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioManagementServiceTest {

    @Value("${app.api.portfolio.dividend.default_date_range_days}")
    long dividendPaymentDefaultDays;

    @Mock
    UserRepository userRepositoryMock;

    @Mock
    PortfolioRepository portfolioRepositoryMock;

    @Mock
    PortfolioTradeRepository portfolioTradeRepositoryMock;

    @Mock
    DividendPaymentRepository dividendPaymentRepositoryMock;

    @InjectMocks
    PortfolioManagementService portfolioManagementService;

    @Test
    @DisplayName("Throw exception while creating portfolio given unknown user ID")
    public void testCreatePortfolioGivenPortfolioDetailsWhenUserIDIsInvalidThenThrowResourceNotFoundException() {
        when(userRepositoryMock.existsById(any())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            portfolioManagementService.createPortfolio("2", null, null, null);
        });

        assertEquals("User details not found", exception.getMessage());
    }

    @Test
    @DisplayName("Return empty optional on error while saving portfolio details")
    public void testCreatePortfolioGivenPortfolioDetailsWhenExceptionIsThrownWhileSavingPortfolioDetailsThenReturnEmptyOptional() {
        when(userRepositoryMock.existsById(any())).thenReturn(true);

        when(portfolioRepositoryMock.save(any())).thenThrow(JDBCException.class);

        Optional<Portfolio> portfolioOpt = portfolioManagementService.createPortfolio("0", null, null, null);

        assertTrue(portfolioOpt.isEmpty());

    }

    @Test
    @DisplayName("Return portfolio details when saved")
    public void testCreatePortfolioGivenPortfolioDetailsWhenPortfolioIsSavedThenReturnPortfolioDetailsOptional() {

        Portfolio expectedPortfolio = new Portfolio();
        expectedPortfolio.setNickname("Nickname");
        expectedPortfolio.setUserId("1");
        expectedPortfolio.setType(PortfolioType.STOCK.name());

        when(userRepositoryMock.existsById(any())).thenReturn(true);
        when(portfolioRepositoryMock.save(any())).thenReturn(expectedPortfolio);

        Optional<Portfolio> portfolioOpt = portfolioManagementService.createPortfolio("1", "Nickname", "912397123", PortfolioType.STOCK.name());

        assertEquals(expectedPortfolio, portfolioOpt.get());
    }

    @Test
    @DisplayName("Throw exception while creating portfolio trade given unknown portfolio ID")
    public void testCreateTradeGivenTradeDetailsWhenPortfolioIDDosNotExistThenThrowResourceNotFoundException() {
        when(portfolioRepositoryMock.existsById(any())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            portfolioManagementService.createTrade(null, null, null, null, null, null, null, null, null);
        });

        assertEquals("Unknown portfolio ID", exception.getMessage());
    }

    @Test
    @DisplayName("Return null on error while saving portfolio trade details")
    public void testCreateTradeGivenTradeDetailsWhenExceptionIsThrownWhileSavingTradeThenReturnEmpty() {
        when(portfolioRepositoryMock.existsById(any())).thenReturn(true);

        when(portfolioTradeRepositoryMock.save(any())).thenThrow(JDBCException.class);

        PortfolioTrade portfolioTrade = portfolioManagementService.createTrade(null, null, null, null, null, null, null, null, null);

        assertNull(portfolioTrade);

    }

    @Test
    @DisplayName("Return portfolio Trade details when saved")
    public void testCreateTradeGivenTradeDetailsWhenTradeIsSavedThenReturnTradeDetailsDetails() {

        PortfolioTrade expectedTrade = new PortfolioTrade();
        expectedTrade.setPortfolioId("12345");
        expectedTrade.setSymbol("SYM");
        expectedTrade.setNoOfUnits(new BigDecimal(1));
        expectedTrade.setAmountPerUnit(new BigDecimal(1));
        expectedTrade.setType(TradeType.BUY.name());
        expectedTrade.setTransactionDate(LocalDate.now());
        expectedTrade.setTaxes(new BigDecimal(0));
        expectedTrade.setBrokerFees(new BigDecimal(0));
        expectedTrade.setOtherFees(new BigDecimal(0));

        when(portfolioRepositoryMock.existsById(any())).thenReturn(true);
        when(portfolioTradeRepositoryMock.save(any())).thenReturn(expectedTrade);

        PortfolioTrade portfolioTrade = portfolioManagementService.createTrade("12345", "SYM",
                new BigDecimal(1), new BigDecimal(1), TradeType.BUY.name(), expectedTrade.getTransactionDate(),
                null, null, null);

        assertEquals(expectedTrade, portfolioTrade);
    }
    
    //  Get Dividend Payments Tests
    @Test
    public void testGetDividends_givenUnknownPortfolioId_thenThrowResourceNotFoundException() {
        String portfolioId = "123456";

        when(portfolioRepositoryMock.existsById(any())).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            portfolioManagementService.getDividends("123456", null, LocalDate.now(), LocalDate.now());
        });

        assertEquals("Unknown portfolio ID", exception.getMessage());
    }

    @Test
    public void testGetDividends_givenStartAndEndDate_whenDateRangeIsGreaterThenConfiguredAmount_thenThrowDateRangeException () {
        Exception exception = assertThrows(RequestDateRangeException.class, () -> {
            portfolioManagementService.getDividends("", "", LocalDate.now(), addDays(LocalDate.now(), dividendPaymentDefaultDays+1));
        });

        assertEquals(String.format("Date range provided greater then [%s] days", dividendPaymentDefaultDays), exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("getDividendsParams")
    public void testGetDividends_givenInoutParams_thenReturnExpectedValue(List<DividendPayment> mockReturnList, Integer expectedSize) {
        when(portfolioRepositoryMock.existsById(any())).thenReturn(true);
        when(dividendPaymentRepositoryMock.getDividendPayments(any(), any(), any(), any())).thenReturn(mockReturnList);

        List<DividendPayment> dividendPaymentList = portfolioManagementService.getDividends(null, null, null, null);

        assertEquals(dividendPaymentList.size(), expectedSize);
    }

    public static Stream<Arguments> getDividendsParams() {
        return Stream.of(
                Arguments.of(Collections.singletonList(new DividendPayment()), 1),
                Arguments.of(new ArrayList<>(), 0)
        );
    }
}