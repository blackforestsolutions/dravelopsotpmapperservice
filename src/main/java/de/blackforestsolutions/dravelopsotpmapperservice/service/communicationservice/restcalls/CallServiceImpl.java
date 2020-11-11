package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CallServiceImpl implements CallService {

    private final WebClient webClient;

    @Autowired
    public CallServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public <T> Mono<T> getOne(String url, HttpHeaders httpHeaders, Class<T> returnType) {
        return webClient
                .get()
                .uri(url)
                .headers(headers -> httpHeaders.forEach(headers::addAll))
                .retrieve()
                .bodyToMono(returnType);
    }
}
