package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface CallService {
    Mono<ResponseEntity<String>> get(String url, HttpHeaders httpHeaders);
}
