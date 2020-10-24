package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.exception.NoExternalResultFoundException;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.PeliasHttpCallBuilderServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOpenTripPlannerApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getPeliasApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.PointObjectMother.getStuttgarterStreetPoint;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.getResourceFileAsString;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PeliasApiServiceTest {

    private final PeliasHttpCallBuilderService peliasHttpCallBuilderService = mock(PeliasHttpCallBuilderServiceImpl.class);
    private final CallService callService = mock(CallServiceImpl.class);

    private final PeliasApiService classUnderTest = new PeliasApiServiceImpl(peliasHttpCallBuilderService, callService);

    @BeforeEach
    void init() {
        when(peliasHttpCallBuilderService.buildPeliasTravelPointNamePathWith(any(ApiToken.class), any(Point.class)))
                .thenReturn("");

        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(getResourceFileAsString("json/peliasResult.json"), HttpStatus.OK)));
    }

    @Test
    void test_extractTravelPointNameFrom_apiToken_and_coordinate_returns_correct_travelPointName() {
        ApiToken testData = getPeliasApiToken();
        Point testPoint = getStuttgarterStreetPoint();

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(testData, testPoint);

        StepVerifier.create(result)
                .assertNext(travelPointName -> {
                    assertThat(travelPointName.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPointName.getThrowable()).isNull();
                    assertThat(travelPointName.getCalledObject()).isEqualTo("Stuttgarter Straße 1");
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointNameFrom_is_executed_correctly() {
        ApiToken testData = getPeliasApiToken();
        Point testPoint = getStuttgarterStreetPoint();
        ArgumentCaptor<ApiToken> apiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<Point> pointArg = ArgumentCaptor.forClass(Point.class);
        ArgumentCaptor<String> urlArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpHeaders> httpHeadersArg = ArgumentCaptor.forClass(HttpHeaders.class);

        classUnderTest.extractTravelPointNameFrom(testData, testPoint).block();

        InOrder inOrder = inOrder(peliasHttpCallBuilderService, callService);
        inOrder.verify(peliasHttpCallBuilderService, times(1)).buildPeliasTravelPointNamePathWith(apiTokenArg.capture(), pointArg.capture());
        inOrder.verify(callService, times(1)).get(urlArg.capture(), httpHeadersArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(apiTokenArg.getValue()).isEqualToComparingFieldByField(getPeliasApiToken());
        assertThat(pointArg.getValue()).isEqualToComparingFieldByField(getStuttgarterStreetPoint());
        assertThat(urlArg.getValue()).isEqualTo("http://localhost:4000");
        assertThat(httpHeadersArg.getValue()).isEqualTo(HttpHeaders.EMPTY);
    }

    @Test
    void test_extractTravelPointNameFrom_apiToken_host_as_null_and_point_returns_failed_call_status() {
        Point testPoint = getStuttgarterStreetPoint();
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasApiToken());
        testData.setHost(null);

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(testData.build(), testPoint);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointNameFrom_apiToken_and_point_returns_failed_call_status_when_call_failed() {
        Point testPoint = getStuttgarterStreetPoint();
        ApiToken testData = getPeliasApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>("error", HttpStatus.BAD_REQUEST)));

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(testData, testPoint);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(JsonParseException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointNameFrom_apiToken_and_point_throws_exception_when_name_is_null() throws JsonProcessingException {
        Point testPoint = getStuttgarterStreetPoint();
        ApiToken testData = getPeliasApiToken();
        String peliasJson = getResourceFileAsString("json/peliasResult.json");
        PeliasTravelPointResponse travelPointResponse = retrieveJsonToPojo(peliasJson, PeliasTravelPointResponse.class);
        travelPointResponse.getFeatures().get(0).getProperties().setName(null);
        peliasJson = new ObjectMapper().writeValueAsString(travelPointResponse);
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(peliasJson, HttpStatus.OK)));

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(testData, testPoint);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointNameFrom_apiToken_testPoint_and_emptyResponse_returns_noExternalResultFoundException() {
        Point testPoint = getStuttgarterStreetPoint();
        ApiToken testData = getPeliasApiToken();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.just(new ResponseEntity<>(getResourceFileAsString("json/peliasNoResult.json"), HttpStatus.OK)));

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(testData, testPoint);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NoExternalResultFoundException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointNameFrom_apiToken_and_point_returns_failed_call_status_when_exception_is_thrown_outside_of_stream() {
        Point testPoint = getStuttgarterStreetPoint();

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(null, testPoint);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_apiToken_and_error_by_callService_returns_failed_callStatus() {
        ApiToken testData = getOpenTripPlannerApiToken();
        Point testPoint = getStuttgarterStreetPoint();
        when(callService.get(anyString(), any(HttpHeaders.class)))
                .thenReturn(Mono.error(new Exception()));

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(testData, testPoint);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(Exception.class);
                })
                .verifyComplete();
    }

    @Test
    void test_extractTravelPointNameFrom_apiToken_point_and_thrown_exception_by_httpCallBuilder_returns_failed_call_status() {
        ApiToken testData = getOpenTripPlannerApiToken();
        Point testPoint = getStuttgarterStreetPoint();
        when(peliasHttpCallBuilderService.buildPeliasTravelPointNamePathWith(any(ApiToken.class), any(Point.class)))
                .thenThrow(NullPointerException.class);

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(testData, testPoint);

        StepVerifier.create(result)
                .assertNext(error -> {
                    assertThat(error.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(error.getCalledObject()).isNull();
                    assertThat(error.getThrowable()).isInstanceOf(NullPointerException.class);
                })
                .verifyComplete();
    }
}
