package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.*;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.RequestTokenHandlerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.OpenTripPlannerConfigurationObjectMother.getOtpConfigurationWithNoEmptyFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TravelPointApiServiceTest {

    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);
    private final RequestTokenHandlerService requestTokenHandlerService = spy(RequestTokenHandlerService.class);
    private final OpenTripPlannerConfiguration openTripPlannerConfiguration = getOtpConfigurationWithNoEmptyFields();
    private final OpenTripPlannerApiService openTripPlannerApiService = mock(OpenTripPlannerApiServiceImpl.class);

    private final TravelPointApiService classUnderTest = new TravelPointApiServiceImpl(requestTokenHandlerService, exceptionHandlerService, openTripPlannerConfiguration, openTripPlannerApiService);

    @BeforeEach
    void init() {
        when(requestTokenHandlerService.getNearestStationsApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenReturn(getNearestStationsOtpFastLaneApiToken())
                .thenReturn(getNearestStationsOtpSlowLaneApiToken());

        when(openTripPlannerApiService.getNearestStationsBy(any(ApiToken.class)))
                .thenReturn(getExampleNearestStationResult())
                .thenReturn(getExampleNearestStationResult());
    }

    @Test
    void test_retrieveNearestStationsFromApiService_with_otpMapperToken_requestTokenHandler_exceptionHandler_and_apiService_returns_travelPoints_async_sorted_and_distinct_journeys() {
        ApiToken otpMapperTestToken = getNearestStationsOtpMapperApiToken();

        Flux<TravelPoint> result = classUnderTest.retrieveNearestStationsFromApiService(otpMapperTestToken);

        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenTownChurchTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenGerwigSchoolTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenAllmendStreetTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenRoessleTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenFriedrichSchoolTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenIlbenStreetTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenRabenStreetTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenOttoHahnSchoolTravelPoint()))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getAmGrosshaubergTravelPoint(new Distance(1.0d, Metrics.KILOMETERS))))
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getAmGrosshaubergTravelPoint(new Distance(1.5d, Metrics.KILOMETERS))))
                .verifyComplete();
    }

    @Test
    void test_retrieveJourneysFromApiService_with_otpMapperToken_requestTokenHandler_exceptionHandler_and_apiService_is_executed_correctly() {
        ApiToken otpMapperTestToken = getNearestStationsOtpMapperApiToken();

        classUnderTest.retrieveNearestStationsFromApiService(otpMapperTestToken).collectList().block();

        verify(requestTokenHandlerService, times(2)).getNearestStationsApiTokenWith(any(ApiToken.class), any(ApiToken.class));
        verify(openTripPlannerApiService, times(2)).getNearestStationsBy(any(ApiToken.class));
        verify(exceptionHandlerService, times(24)).handleExceptions(any(CallStatus.class));
    }

    @Test
    void test_retrieveNearestStationsFromApiService_with_otpMapperToken_and_thrown_exception_returns_zero_results() {
        ApiToken otpMapperTestToken = getNearestStationsOtpMapperApiToken();
        when(requestTokenHandlerService.getNearestStationsApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenThrow(new NullPointerException());

        Flux<TravelPoint> result = classUnderTest.retrieveNearestStationsFromApiService(otpMapperTestToken);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }

    @Test
    void test_retrieveNearestStationsFromApiService_with_otpMapperToken_and_error_by_mocked_service_returns_error() {
        ApiToken otpMapperTestToken = getNearestStationsOtpMapperApiToken();
        when(openTripPlannerApiService.getNearestStationsBy(any(ApiToken.class)))
                .thenReturn(Flux.error(new Exception()));

        Flux<TravelPoint> result = classUnderTest.retrieveNearestStationsFromApiService(otpMapperTestToken);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }

    @Test
    void test_retrieveNearestStationsFromApiService_with_otpMapperToken_and_error_call_status_returns_zero_journeys_when_apiService_failed() {
        ApiToken otpMapperTestToken = getNearestStationsOtpMapperApiToken();
        when(openTripPlannerApiService.getNearestStationsBy(any(ApiToken.class)))
                .thenReturn(Flux.just(new CallStatus<>(null, Status.FAILED, new Exception())));

        Flux<TravelPoint> result = classUnderTest.retrieveNearestStationsFromApiService(otpMapperTestToken);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    private Flux<CallStatus<TravelPoint>> getExampleNearestStationResult() {
        return Flux.just(
                new CallStatus<>(null, Status.FAILED, new Exception()),
                new CallStatus<>(getFurtwangenTownChurchTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getFurtwangenGerwigSchoolTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getFurtwangenRabenStreetTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getFurtwangenFriedrichSchoolTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getFurtwangenAllmendStreetTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getFurtwangenOttoHahnSchoolTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getFurtwangenRoessleTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getFurtwangenIlbenStreetTravelPoint(), Status.SUCCESS, null),
                new CallStatus<>(getAmGrosshaubergTravelPoint(new Distance(1.0d, Metrics.KILOMETERS)), Status.SUCCESS, null),
                new CallStatus<>(getAmGrosshaubergTravelPoint(new Distance(1.5d, Metrics.KILOMETERS)), Status.SUCCESS, null),
                new CallStatus<>(getFurtwangenLocalityTravelPoint(new Distance(2.0d, Metrics.KILOMETERS)), Status.SUCCESS, null)
        );
    }
}
