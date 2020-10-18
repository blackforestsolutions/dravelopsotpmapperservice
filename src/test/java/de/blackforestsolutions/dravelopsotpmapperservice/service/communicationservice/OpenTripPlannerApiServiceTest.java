package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import com.fasterxml.jackson.core.JsonParseException;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.exception.NoExternalResultFoundException;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.OpenTripPlannerHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopstestdatamodel.objectmothers.ApiTokenObjectMother.getOpenTripPlannerApiToken;
import static de.blackforestsolutions.dravelopstestdatamodel.objectmothers.JourneyObjectMother.getJourneyWithEmptyFields;
import static de.blackforestsolutions.dravelopstestdatamodel.objectmothers.UUIDObjectMother.TEST_UUID_1;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutils.TestUtils.getResourceFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(getResourceFileAsString("json/openTripPlannerSuedbadenJourney.json"), HttpStatus.OK)));

        when(openTripPlannerMapperService.extractJourneysFrom(any(OpenTripPlannerJourneyResponse.class), anyString(), anyString()))
                .thenReturn(Flux.just(new CallStatus<>(getJourneyWithEmptyFields(TEST_UUID_1), Status.SUCCESS, null)));
    }

    @Test
    void test_getJourneysBy_apiToken_returns_journeys() {
        ApiToken testData = getOpenTripPlannerApiToken();

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
        ApiToken testData = getOpenTripPlannerApiToken();
        ArgumentCaptor<ApiToken> apiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<String> urlArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpHeaders> httpHeadersArg = ArgumentCaptor.forClass(HttpHeaders.class);
        ArgumentCaptor<OpenTripPlannerJourneyResponse> responseArg = ArgumentCaptor.forClass(OpenTripPlannerJourneyResponse.class);
        ArgumentCaptor<String> departureArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> arrivalArg = ArgumentCaptor.forClass(String.class);

        classUnderTest.getJourneysBy(testData).collectList().block();

        InOrder inOrder = inOrder(openTripPlannerHttpCallBuilderService, callService, openTripPlannerMapperService);
        inOrder.verify(openTripPlannerHttpCallBuilderService, times(1)).buildOpenTripPlannerJourneyPathWith(apiTokenArg.capture());
        inOrder.verify(callService, times(1)).get(urlArg.capture(), httpHeadersArg.capture());
        inOrder.verify(openTripPlannerMapperService, times(1)).extractJourneysFrom(responseArg.capture(), departureArg.capture(), arrivalArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(apiTokenArg.getValue()).isEqualToComparingFieldByField(getOpenTripPlannerApiToken());
        assertThat(urlArg.getValue()).isEqualTo("http://localhost:8089");
        assertThat(httpHeadersArg.getValue()).isEqualTo(HttpHeaders.EMPTY);
        assertThat(responseArg.getValue()).isInstanceOf(OpenTripPlannerJourneyResponse.class);
        assertThat(departureArg.getValue()).isEqualTo("Am Großhausberg 8");
        assertThat(arrivalArg.getValue()).isEqualTo("Sick AG");
    }

    @Test
    void test_getJourneysBy_apiToken_and_host_as_null_returns_failed_call_status() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
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
    void test_getJourneysBy_apiToken_returns_failed_call_status_when_call_failed() {
        ApiToken testData = getOpenTripPlannerApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>("error", HttpStatus.BAD_REQUEST)));

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);


        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(JsonParseException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_apiToken_returns_failed_call_status_when_exception_is_thrown_inside_of_stream() {
        ApiToken testData = getOpenTripPlannerApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(null, HttpStatus.OK)));

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(IllegalArgumentException.class);
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
        ApiToken testData = getOpenTripPlannerApiToken();
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
    void test_getJourneysBy_apiToken_and_error_json_when_api_is_called_returns_call_status_with_noExternalResultFoundException() {
        ApiToken testData = getOpenTripPlannerApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(getResourceFileAsString("json/openTripPlannerNoJourneyFound.json"), HttpStatus.OK)));

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NoExternalResultFoundException.class);
                })
                .verifyComplete();
    }
}
