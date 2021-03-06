package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.RequestTokenHandlerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getFurtwangenToWaldkirchJourney;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getJourneyWithEmptyFields;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.OpenTripPlannerConfigurationObjectMother.getOtpConfigurationWithNoEmptyFields;
import static org.mockito.Mockito.*;

class JourneyApiServiceTest {

    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);
    private final RequestTokenHandlerService requestTokenHandlerService = spy(RequestTokenHandlerService.class);
    private final OpenTripPlannerConfiguration openTripPlannerConfiguration = getOtpConfigurationWithNoEmptyFields();
    private final OpenTripPlannerApiService openTripPlannerApiService = mock(OpenTripPlannerApiServiceImpl.class);

    private final JourneyApiService classUnderTest = new JourneyApiServiceImpl(requestTokenHandlerService, exceptionHandlerService, openTripPlannerConfiguration, openTripPlannerApiService);

    @BeforeEach
    void init() {
        when(requestTokenHandlerService.getJourneyApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenReturn(Mono.just(getJourneyOtpFastLaneApiToken()))
                .thenReturn(Mono.just(getJourneyOtpSlowLaneApiToken()));

        when(openTripPlannerApiService.getJourneysBy(any(ApiToken.class)))
                .thenReturn(Flux.just(
                        new CallStatus<>(getJourneyWithEmptyFields(), Status.SUCCESS, null),
                        new CallStatus<>(null, Status.FAILED, new Exception())
                ))
                .thenReturn(Flux.just(
                        new CallStatus<>(getFurtwangenToWaldkirchJourney(), Status.SUCCESS, null)
                ));
    }

    @Test
    void test_retrieveJourneysFromApiServices_with_otpMapperToken_requestTokenHandler_exceptionHandler_and_apiService_returns_journeys_async_and_distinct_journeys() {
        ApiToken otpMapperTestToken = getJourneyOtpMapperApiToken();
        List<Journey> expectedJourneys = List.of(getJourneyWithEmptyFields(), getFurtwangenToWaldkirchJourney());

        Flux<Journey> result = classUnderTest.retrieveJourneysFromApiService(otpMapperTestToken);

        StepVerifier.create(result)
                .expectNextMatches(expectedJourneys::contains)
                .expectNextMatches(expectedJourneys::contains)
                .verifyComplete();
    }

    @Test
    void test_retrieveJourneysFromApiService_with_otpMapperToken_requestTokenHandler_exceptionHandler_and_apiService_is_executed_correctly() {
        ApiToken otpMapperTestToken = getJourneyOtpMapperApiToken();

        classUnderTest.retrieveJourneysFromApiService(otpMapperTestToken).collectList().block();

        verify(requestTokenHandlerService, times(2)).getJourneyApiTokenWith(any(ApiToken.class), any(ApiToken.class));
        verify(openTripPlannerApiService, times(2)).getJourneysBy(any(ApiToken.class));
        verify(exceptionHandlerService, times(3)).handleExceptions(any(CallStatus.class));
    }

    @Test
    void test_retrieveJourneysFromApiService_with_otpMapperToken_and_error_by_mocked_returns_zero_results() {
        ApiToken otpMapperTestToken = getJourneyOtpMapperApiToken();
        when(requestTokenHandlerService.getJourneyApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenReturn(Mono.error(new Exception()));

        Flux<Journey> result = classUnderTest.retrieveJourneysFromApiService(otpMapperTestToken);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }

    @Test
    void test_retrieveJourneysFromApiService_with_otpMapperToken_and_error_by_mocked_service_returns_error() {
        ApiToken otpMapperTestToken = getJourneyOtpMapperApiToken();
        when(openTripPlannerApiService.getJourneysBy(any(ApiToken.class)))
                .thenReturn(Flux.error(new Exception()));

        Flux<Journey> result = classUnderTest.retrieveJourneysFromApiService(otpMapperTestToken);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }

    @Test
    void test_retrieveJourneysFromApiService_with_otpMapperToken_and_error_call_status_returns_zero_journeys_when_apiService_failed() {
        ApiToken otpMapperTestToken = getJourneyOtpMapperApiToken();
        when(openTripPlannerApiService.getJourneysBy(any(ApiToken.class)))
                .thenReturn(Flux.just(new CallStatus<>(null, Status.FAILED, new Exception())));

        Flux<Journey> result = classUnderTest.retrieveJourneysFromApiService(otpMapperTestToken);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
