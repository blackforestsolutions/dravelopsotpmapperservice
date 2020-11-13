package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls;

import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

public interface CallService {
    <T> Mono<T> getOne(String url, HttpHeaders httpHeaders, Class<T> returnType);
}
