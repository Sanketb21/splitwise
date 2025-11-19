package com.splitwise.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter for logging HTTP requests and responses
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Log incoming request
        logger.info("Incoming Request: {} {} from {}", 
            request.getMethod(), 
            request.getURI(), 
            request.getRemoteAddress());
        
        logger.debug("Request Headers: {}", request.getHeaders());
        
        // Log response after the request is processed
        ServerHttpResponse response = exchange.getResponse();
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            logger.info("Outgoing Response: {} {} - Status: {}", 
                request.getMethod(), 
                request.getURI(), 
                response.getStatusCode());
            
            logger.debug("Response Headers: {}", response.getHeaders());
        }));
    }

    @Override
    public int getOrder() {
        // Set order to run early in the filter chain
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

