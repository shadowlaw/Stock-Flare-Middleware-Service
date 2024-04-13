package com.shadow.stock_flare_middleware_service.service;

import com.shadow.stock_flare_middleware_service.repository.SymbolRepository;
import com.shadow.stock_flare_middleware_service.repository.entity.Symbol;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
@Slf4j
public class SymbolService {

    @Autowired
    SymbolRepository symbolRepository;

    public Page<Symbol> getSymbols(PageRequest pageRequest) {
        return symbolRepository.findAll(pageRequest);
    }
}
