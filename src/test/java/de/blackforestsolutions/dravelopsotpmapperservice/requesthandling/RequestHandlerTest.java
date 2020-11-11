package de.blackforestsolutions.dravelopsotpmapperservice.requesthandling;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOtpMapperApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getJourneyWithEmptyFields;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.UUIDObjectMother.TEST_UUID_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RequestHandlerTest {

    private final JourneyApiService journeyApiService = mock(JourneyApiService.class);
    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);

    private final RequestHandler classUnderTest = new RequestHandler(journeyApiService, exceptionHandlerService);

    @BeforeEach
    void init() {
        when(journeyApiService.retrieveJourneysFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.just(getJourneyWithEmptyFields(TEST_UUID_1)));
    }

    @Test
    void test_retrieveOpenTripPlannerJourneys_with_wrong_apiToken_body_returns__stream_with_ok_status() {
        MockServerRequest testData = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .body(Mono.just(getOtpMapperApiToken()));

        Mono<ServerResponse> result = classUnderTest.retrieveOpenTripPlannerJourneys(testData);

        StepVerifier.create(result)
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(serverResponse.headers().getContentType()).isEqualTo(MediaType.TEXT_EVENT_STREAM);
                })
                .verifyComplete();
    }

    @Test
    void test_retrieveOpenTripPlannerJourneys_is_executed_correctly() {
        ArgumentCaptor<ApiToken> apiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        MockServerRequest testData = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(getOtpMapperApiToken()));

        classUnderTest.retrieveOpenTripPlannerJourneys(testData).block();

        InOrder inOrder = inOrder(exceptionHandlerService, journeyApiService);
        inOrder.verify(journeyApiService, times(1)).retrieveJourneysFromApiService(apiTokenArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(apiTokenArg.getValue()).isEqualToComparingFieldByField(getOtpMapperApiToken());
    }

    @Test
    void test_retrieveOpenTripPlannerJourneys_with_wrong_apiToken_body_returns_empty_stream_with_ok_status() {
        MockServerRequest testData = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just("wrong Body"));

        Mono<ServerResponse> result = classUnderTest.retrieveOpenTripPlannerJourneys(testData);

        StepVerifier.create(result)
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(serverResponse.headers().getContentType()).isEqualTo(MediaType.TEXT_EVENT_STREAM);
                })
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(CallStatus.class));
    }

    @Test
    void test_retrieveOpenTripPlannerJourneys_with_correct_apiToken_and_error_from_apiService_returns_empty_stream_with_ok_status() {
        when(journeyApiService.retrieveJourneysFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.error(new Exception()));

        MockServerRequest testData = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .body(Mono.just(getOtpMapperApiToken()));

        Mono<ServerResponse> result = classUnderTest.retrieveOpenTripPlannerJourneys(testData);

        StepVerifier.create(result)
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(serverResponse.headers().getContentType()).isEqualTo(MediaType.TEXT_EVENT_STREAM);
                })
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(CallStatus.class));
    }

//    private final JourneyApiService journeyApiService = mock(JourneyApiService.class);
//
//    private final RequestHandler classUnderTest = new RequestHandler(journeyApiService);
//
//    @BeforeEach
//    void init() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    void test_retrieveOpenTripPlannerJourneys_with_userRequestToken_is_executed_correctly_and_return_journeys() {
//        ApiToken testData = getOtpMapperApiToken();
//        ArgumentCaptor<ApiToken> requestToken = ArgumentCaptor.forClass(ApiToken.class);
//        when(journeyApiService.retrieveJourneysFromApiService(any(ApiToken.class))).thenReturn(Flux.just(getJourneyWithEmptyFields(TEST_UUID_1)));
//
//        Flux<Journey> result = classUnderTest.retrieveOpenTripPlannerJourneys(testData);
//
//        verify(journeyApiService, times(1)).retrieveJourneysFromApiService(requestToken.capture());
//        assertThat(toJson(requestToken.getValue())).isEqualTo(toJson(getOtpMapperApiToken()));
//        StepVerifier.create(result)
//                .assertNext(journey -> assertThat(deleteWhitespace(toJson(journey))).isEqualTo(deleteWhitespace(toJson(getJourneyWithEmptyFields(TEST_UUID_1)))))
//                .verifyComplete();
//    }
//
//    @Test
//    void test_retrieveOpenTripPlannerJourneys_with_userRequestToken_is_executed_correctly_when_no_results_are_available() {
//        ApiToken testData = getOtpMapperApiToken();
//        when(journeyApiService.retrieveJourneysFromApiService(any(ApiToken.class))).thenReturn(Flux.empty());
//
//        Flux<Journey> result = classUnderTest.retrieveOpenTripPlannerJourneys(testData);
//
//        StepVerifier.create(result)
//                .expectNextCount(0L)
//                .verifyComplete();
//    }

}
