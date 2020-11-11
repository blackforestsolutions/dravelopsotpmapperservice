package de.blackforestsolutions.dravelopsotpmapperservice.requesthandling;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOtpMapperApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getFurtwangenToWaldkirchJourney;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RequestHandlerMockIT {

    @MockBean
    private JourneyApiService journeyApiService;

    @Autowired
    private RouterFunction<ServerResponse> routerFunction;

    @BeforeEach
    void init() {
        when(journeyApiService.retrieveJourneysFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.just(getFurtwangenToWaldkirchJourney()));
    }

    @Test
    void test_() {
        ApiToken testData = getOtpMapperApiToken();

        Flux<Journey> result = routerFunction("/otp/journeys/get", testData)
                .expectStatus()
                .isOk()
                .returnResult(Journey.class)
                .getResponseBody();

        StepVerifier.create(result)
                .assertNext(journey -> assertThat(toJson(journey)).isEqualTo(toJson(getFurtwangenToWaldkirchJourney())))
                .verifyComplete();
    }


    private WebTestClient.ResponseSpec routerFunction(String path, ApiToken body) {
        return WebTestClient.bindToRouterFunction(routerFunction)
                .configureClient()
                .exchangeStrategies(exchangeStrategies())
                .build()
                .post()
                .uri(path)
                .body(Mono.just(body), ApiToken.class)
                .exchange();
    }

    private ExchangeStrategies exchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(new DravelOpsJsonMapper()));
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(new DravelOpsJsonMapper()));
                })
                .build();
    }
}
