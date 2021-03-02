package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.OpenTripPlannerHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getJourneyWithEmptyFields;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OpenTripPlannerApiServiceTest {

    private final OpenTripPlannerMapperService openTripPlannerMapperService = mock(OpenTripPlannerMapperService.class);
    private final OpenTripPlannerHttpCallBuilderService openTripPlannerHttpCallBuilderService = mock(OpenTripPlannerHttpCallBuilderService.class);
    private final CallService callService = mock(CallService.class);

    private final OpenTripPlannerApiService classUnderTest = new OpenTripPlannerApiServiceImpl(openTripPlannerMapperService, openTripPlannerHttpCallBuilderService, callService);

    @BeforeEach
    void init() {
        when(openTripPlannerHttpCallBuilderService.buildOpenTripPlannerJourneyPathWith(any(ApiToken.class)))
                .thenReturn("");

        when(callService.getOne(anyString(), any(HttpHeaders.class), eq(OpenTripPlannerJourneyResponse.class)))
                .thenReturn(Mono.just(retrieveJsonToPojo("json/openTripPlannerSuedbadenJourney.json", OpenTripPlannerJourneyResponse.class)));

        when(openTripPlannerMapperService.extractJourneysFrom(any(OpenTripPlannerJourneyResponse.class), anyString(), anyString()))
                .thenReturn(Flux.just(new CallStatus<>(getJourneyWithEmptyFields(), Status.SUCCESS, null)));
    }

    @Test
    void test_getJourneysBy_fast_lane_apiToken_returns_journeys() {
        ApiToken testData = getOtpFastLaneApiToken();

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(journey -> {
                    assertThat(journey.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(journey.getCalledObject()).isInstanceOf(Journey.class);
                    assertThat(journey.getThrowable()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_slow_lane_apiToken_returns_journeys() {
        ApiToken testData = getOtpSlowLaneApiToken();

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(journey -> {
                    assertThat(journey.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(journey.getCalledObject()).isInstanceOf(Journey.class);
                    assertThat(journey.getThrowable()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_is_executed_correctly() {
        ApiToken testData = getOtpFastLaneApiToken();
        ArgumentCaptor<ApiToken> apiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<String> urlArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpHeaders> httpHeadersArg = ArgumentCaptor.forClass(HttpHeaders.class);
        ArgumentCaptor<OpenTripPlannerJourneyResponse> responseArg = ArgumentCaptor.forClass(OpenTripPlannerJourneyResponse.class);
        ArgumentCaptor<String> departureArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> arrivalArg = ArgumentCaptor.forClass(String.class);

        classUnderTest.getJourneysBy(testData).collectList().block();

        InOrder inOrder = inOrder(openTripPlannerHttpCallBuilderService, callService, openTripPlannerMapperService);
        inOrder.verify(openTripPlannerHttpCallBuilderService, times(1)).buildOpenTripPlannerJourneyPathWith(apiTokenArg.capture());
        inOrder.verify(callService, times(1)).getOne(urlArg.capture(), httpHeadersArg.capture(), eq(OpenTripPlannerJourneyResponse.class));
        inOrder.verify(openTripPlannerMapperService, times(1)).extractJourneysFrom(responseArg.capture(), departureArg.capture(), arrivalArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(apiTokenArg.getValue()).isEqualToComparingFieldByFieldRecursively(getOtpFastLaneApiToken());
        assertThat(urlArg.getValue()).isEqualTo("http://localhost:9000");
        assertThat(httpHeadersArg.getValue()).isEqualTo(HttpHeaders.EMPTY);
        assertThat(responseArg.getValue()).isInstanceOf(OpenTripPlannerJourneyResponse.class);
        assertThat(departureArg.getValue()).isEqualTo("Am Großhausberg 8");
        assertThat(arrivalArg.getValue()).isEqualTo("Sick AG");
    }

    @Test
    void test_getJourneysBy_apiToken_and_host_as_null_returns_failed_call_status() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setHost(null);

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData.build());

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_apiToken_as_null_returns_failed_call_status_when_exception_is_thrown_outside_of_stream() {

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(null);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_apiToken_returns_failed_call_status_when_exception_is_thrown_by_mapperService() {
        ApiToken testData = getOtpFastLaneApiToken();
        when(openTripPlannerMapperService.extractJourneysFrom(any(OpenTripPlannerJourneyResponse.class), anyString(), anyString()))
                .thenThrow(NullPointerException.class);

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_apiToken_and_empty_json_when_api_is_called_returns_no_result() {
        ApiToken testData = getOtpFastLaneApiToken();
        when(callService.getOne(anyString(), any(HttpHeaders.class), eq(OpenTripPlannerJourneyResponse.class)))
                .thenReturn(Mono.just(retrieveJsonToPojo("json/openTripPlannerNoJourneyFound.json", OpenTripPlannerJourneyResponse.class)));

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_apiToken_and_error_by_callService_returns_failed_callStatus() {
        ApiToken testData = getOtpFastLaneApiToken();
        when(callService.getOne(anyString(), any(HttpHeaders.class), eq(OpenTripPlannerJourneyResponse.class)))
                .thenReturn(Mono.error(new Exception()));

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_apiToken_and_failed_call_status_by_mapper_returns_failed_callStatus() {
        ApiToken testData = getOtpFastLaneApiToken();
        when(openTripPlannerMapperService.extractJourneysFrom(any(OpenTripPlannerJourneyResponse.class), anyString(), anyString()))
                .thenReturn(Flux.just(new CallStatus<>(null, Status.FAILED, new Exception())));

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_apiToken_and_thrown_excpetion_by_httpCallBuiler_returns_failed_callStatus() {
        ApiToken testData = getOtpFastLaneApiToken();
        when(openTripPlannerHttpCallBuilderService.buildOpenTripPlannerJourneyPathWith(any(ApiToken.class)))
                .thenThrow(NullPointerException.class);

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }
}
