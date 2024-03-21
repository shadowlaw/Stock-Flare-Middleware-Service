package com.shadow.stock_flare_middleware_service.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.shadow.stock_flare_middleware_service.constants.LoggingConstants.IP_ID;

@Component
public class LoggingFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MDC.put(IP_ID, request.getHeader(" X-Forwarded-For ") == null ? request.getRemoteAddr() : request.getHeader(" X-Forwarded-For "));
        filterChain.doFilter(request, response);
        MDC.clear();
    }
}
