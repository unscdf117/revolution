package com.unsc.shard.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 * @author DELL
 */
@Configuration
@Order(-1)
@Slf4j
public class MyWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
 
        ServerHttpRequest request =  serverWebExchange.getRequest();
        URI uri = request.getURI();
        PathContainer path = request.getPath().pathWithinApplication();
        InetSocketAddress net = request.getRemoteAddress();
        InetAddress address = net.getAddress();
        String hostName = net.getHostName();
        int port = net.getPort();
        String method = request.getMethodValue();
        log.info("URI: {}, Path: {}, Net: {}, Address: {}, HostName: {}, Port：{}, Method: {}", uri, path, net, address, hostName, port, method);

        return webFilterChain.filter(serverWebExchange);
    }
}