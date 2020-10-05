package de.blackforestsolutions.dravelopsotpmapperservice.controller;

import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.ApiTokenObjectMother.getRequestToken;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.JourneyObjectMother.getJourneyWithEmptyFields;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.UUIDObjectMother.TEST_UUID_1;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutils.TestUtils.toJson;
import static org.apache.commons.lang.StringUtils.deleteWhitespace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JourneyControllerTest {

    private final JourneyApiService journeyApiService = mock(JourneyApiService.class);

    private final WebTestClient classUnderTest = WebTestClient.bindToController(new JourneyController(journeyApiService)).build();

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void test_if_call_is_executed_correctly_and_return_journeys() {
        ArgumentCaptor<String> requestToken = ArgumentCaptor.forClass(String.class);
        when(journeyApiService.retrieveJourneysFromApiService(anyString())).thenReturn(Flux.just(toJson(getJourneyWithEmptyFields(TEST_UUID_1))));

        Flux<String> result = classUnderTest
                .post()
                .uri("/otp/journeys/get")
                .body(Mono.just(toJson(getRequestToken())), String.class)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();


        verify(journeyApiService, times(1)).retrieveJourneysFromApiService(requestToken.capture());
        assertThat(requestToken.getValue()).isEqualTo(toJson(getRequestToken()));
        StepVerifier.create(result)
                .assertNext(journey -> assertThat(deleteWhitespace(journey)).isEqualTo(deleteWhitespace(toJson(getJourneyWithEmptyFields(TEST_UUID_1)))))
                .verifyComplete();
    }

    @Test
    void test_if_call_is_executed_correctly_when_no_results_are_available() {
        when(journeyApiService.retrieveJourneysFromApiService(anyString())).thenReturn(Flux.empty());

        Flux<String> result = classUnderTest
                .post()
                .uri("/otp/journeys/get")
                .body(Mono.just(toJson(getRequestToken())), String.class)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

}
