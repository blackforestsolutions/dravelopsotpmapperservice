package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.PeliasHttpCallBuilderServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.PointObjectMother.getStuttgarterStreetPoint;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PeliasApiServiceTest {

    private final PeliasHttpCallBuilderService peliasHttpCallBuilderService = mock(PeliasHttpCallBuilderServiceImpl.class);
    private final CallService callService = mock(CallServiceImpl.class);

    private final PeliasApiService classUnderTest = new PeliasApiServiceImpl(peliasHttpCallBuilderService, callService);

    @BeforeEach
    void init() {
        when(peliasHttpCallBuilderService.buildPeliasReversePathWith(any(ApiToken.class), any(Point.class)))
                .thenReturn("");

        when(callService.getOne(anyString(), any(HttpHeaders.class), eq(PeliasTravelPointResponse.class)))
                .thenReturn(Mono.just(retrieveJsonToPojo("json/peliasReverseResult.json", PeliasTravelPointResponse.class)));
    }

    @Test
    void test_extractTravelPointNameFrom_apiToken_and_coordinate_returns_correct_travelPointName() {
        ApiToken testData = getPeliasReverseApiToken();
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
        ApiToken testData = getPeliasReverseApiToken();
        Point testPoint = getStuttgarterStreetPoint();
        ArgumentCaptor<ApiToken> apiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<Point> pointArg = ArgumentCaptor.forClass(Point.class);
        ArgumentCaptor<String> urlArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpHeaders> httpHeadersArg = ArgumentCaptor.forClass(HttpHeaders.class);

        classUnderTest.extractTravelPointNameFrom(testData, testPoint).block();

        InOrder inOrder = inOrder(peliasHttpCallBuilderService, callService);
        inOrder.verify(peliasHttpCallBuilderService, times(1)).buildPeliasReversePathWith(apiTokenArg.capture(), pointArg.capture());
        inOrder.verify(callService, times(1)).getOne(urlArg.capture(), httpHeadersArg.capture(), eq(PeliasTravelPointResponse.class));
        inOrder.verifyNoMoreInteractions();
        assertThat(apiTokenArg.getValue()).isEqualToComparingFieldByField(getPeliasReverseApiToken());
        assertThat(pointArg.getValue()).isEqualToComparingFieldByField(getStuttgarterStreetPoint());
        assertThat(urlArg.getValue()).isEqualTo("http://localhost:4000");
        assertThat(httpHeadersArg.getValue()).isEqualTo(HttpHeaders.EMPTY);
    }

    @Test
    void test_extractTravelPointNameFrom_apiToken_host_as_null_and_point_returns_failed_call_status() {
        Point testPoint = getStuttgarterStreetPoint();
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        testData.setHost(null);

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
    void test_extractTravelPointNameFrom_apiToken_testPoint_and_emptyResponse_returns_no_result() {
        Point testPoint = getStuttgarterStreetPoint();
        ApiToken testData = getPeliasReverseApiToken();
        when(callService.getOne(anyString(), any(HttpHeaders.class), eq(PeliasTravelPointResponse.class)))
                .thenReturn(Mono.just(retrieveJsonToPojo("json/peliasReverseNoResult.json", PeliasTravelPointResponse.class)));

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(testData, testPoint);

        StepVerifier.create(result)
                .expectNextCount(0L)
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
    void test_extractTravelPointNameFrom_apiToken_and_error_by_callService_returns_failed_callStatus() {
        ApiToken testData = getJourneyOtpMapperApiToken();
        Point testPoint = getStuttgarterStreetPoint();
        when(callService.getOne(anyString(), any(HttpHeaders.class), eq(PeliasTravelPointResponse.class)))
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
        ApiToken testData = getJourneyOtpMapperApiToken();
        Point testPoint = getStuttgarterStreetPoint();
        when(peliasHttpCallBuilderService.buildPeliasReversePathWith(any(ApiToken.class), any(Point.class)))
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
