package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.PeliasApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RequestTokenHandlerServiceTest {

    private final ApiToken peliasApiToken = getConfiguredPeliasReverseApiToken();
    private final PeliasApiService peliasApiService = mock(PeliasApiService.class);
    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);

    private final RequestTokenHandlerService classUnderTest = new RequestTokenHandlerServiceImpl(peliasApiToken, peliasApiService, exceptionHandlerService);

    @BeforeEach
    void init() {
        when(peliasApiService.extractTravelPointNameFrom(any(ApiToken.class), any(Point.class)))
                .thenReturn(Mono.just(new CallStatus<>("Am Großhausberg 8", Status.SUCCESS, null)))
                .thenReturn(Mono.just(new CallStatus<>("Sick AG", Status.SUCCESS, null)));
    }

    @Test
    void test_getJourneyApiTokenWith_configured_fast_lane_token_and_user_token_returns_correct_call_token() {
        ApiToken configuredTestData = getOtpConfiguredFastLaneApiToken();
        ApiToken requestTestData = getJourneyOtpMapperApiToken();

        Mono<ApiToken> result = classUnderTest.getJourneyApiTokenWith(requestTestData, configuredTestData);

        StepVerifier.create(result)
                .assertNext(apiToken -> assertThat(apiToken).isEqualToComparingFieldByFieldRecursively(getJourneyOtpFastLaneApiToken()))
                .verifyComplete();
    }

    @Test
    void test_getJourneyApiTokenWith_configured_slow_lane_token_and_user_token_returns_correct_call_token() {
        ApiToken configuredTestData = getOtpConfiguredSlowLaneApiToken();
        ApiToken requestTestData = getJourneyOtpMapperApiToken();

        Mono<ApiToken> result = classUnderTest.getJourneyApiTokenWith(requestTestData, configuredTestData);

        StepVerifier.create(result)
                .assertNext(apiToken -> assertThat(apiToken).isEqualToComparingFieldByFieldRecursively(getJourneyOtpSlowLaneApiToken()))
                .verifyComplete();
    }

    @Test
    void test_getJourneyApiTokenWith_requestToken_and_otpToken_is_executed_correctly() {
        ApiToken configuredTestData = getOtpConfiguredFastLaneApiToken();
        ApiToken requestTestData = getJourneyOtpMapperApiToken();
        ArgumentCaptor<ApiToken> peliasApiTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<Point> pointArg = ArgumentCaptor.forClass(Point.class);
        ArgumentCaptor<CallStatus<String>> callStatusArg = ArgumentCaptor.forClass(CallStatus.class);

        classUnderTest.getJourneyApiTokenWith(requestTestData, configuredTestData).block();

        InOrder inOrder = inOrder(peliasApiService, exceptionHandlerService);
        inOrder.verify(peliasApiService, times(2)).extractTravelPointNameFrom(peliasApiTokenArg.capture(), pointArg.capture());
        inOrder.verify(exceptionHandlerService, times(2)).handleExceptions(callStatusArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(peliasApiTokenArg.getAllValues().size()).isEqualTo(2);
        assertThat(pointArg.getAllValues().size()).isEqualTo(2);
        assertThat(callStatusArg.getAllValues().size()).isEqualTo(2);
        assertThat(peliasApiTokenArg.getAllValues().get(0)).isEqualToComparingFieldByField(getPeliasReverseApiToken());
        assertThat(peliasApiTokenArg.getAllValues().get(1)).isEqualToComparingFieldByField(getPeliasReverseApiToken());
        assertThat(pointArg.getAllValues().get(0)).isEqualToComparingFieldByField(requestTestData.getDepartureCoordinate());
        assertThat(pointArg.getAllValues().get(1)).isEqualToComparingFieldByField(requestTestData.getArrivalCoordinate());
        assertThat(callStatusArg.getAllValues().get(0).getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(callStatusArg.getAllValues().get(1).getStatus()).isEqualTo(Status.SUCCESS);
    }

    @Test
    void test_getJourneyApiTokenWith_configured_token_and_user_token_returns_apiToken_with_placeholders_when_pelias_call_was_not_successfull() {
        ApiToken configuredTestData = getOtpConfiguredFastLaneApiToken();
        ApiToken requestTestData = getJourneyOtpMapperApiToken();
        when(peliasApiService.extractTravelPointNameFrom(any(ApiToken.class), any(Point.class)))
                .thenReturn(Mono.just(new CallStatus<>(null, Status.FAILED, new Exception())));

        Mono<ApiToken> result = classUnderTest.getJourneyApiTokenWith(requestTestData, configuredTestData);

        StepVerifier.create(result)
                .assertNext(apiToken -> {
                    assertThat(apiToken).isEqualToIgnoringGivenFields(getJourneyOtpFastLaneApiToken(), "departure", "arrival", "departureCoordinate", "arrivalCoordinate");
                    assertThat(apiToken.getDepartureCoordinate()).isEqualToComparingFieldByFieldRecursively(getJourneyOtpMapperApiToken().getDepartureCoordinate());
                    assertThat(apiToken.getArrivalCoordinate()).isEqualToComparingFieldByFieldRecursively(getJourneyOtpMapperApiToken().getArrivalCoordinate());
                    assertThat(apiToken.getDeparture()).isEqualTo("Start");
                    assertThat(apiToken.getArrival()).isEqualTo("Ziel");
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneyApiTokenWith_configured_token_and_user_token_returns_error_when_exception_is_thrown() {
        ApiToken configuredTestData = getOtpConfiguredFastLaneApiToken();
        ApiToken reqeustTestData = new ApiToken(getJourneyOtpMapperApiToken());
        reqeustTestData.setDepartureCoordinate(null);

        Mono<ApiToken> result = classUnderTest.getJourneyApiTokenWith(reqeustTestData, configuredTestData);

        StepVerifier.create(result)
                .expectError(NullPointerException.class)
                .verify();
    }

    @Test
    void test_getNearestStationsApiTokenWith_configured_fast_lane_token_and_user_token_returns_correct_call_token() {
        ApiToken configuredTestData = getOtpConfiguredFastLaneApiToken();
        ApiToken requestTestData = getNearestStationsOtpMapperApiToken();

        ApiToken result = classUnderTest.getNearestStationsApiTokenWith(requestTestData, configuredTestData);

        assertThat(result).isEqualToComparingFieldByFieldRecursively(getNearestStationsOtpFastLaneApiToken());
    }

    @Test
    void test_getNearestStationsApiTokenWith_configured_slow_lane_token_and_user_token_returns_correct_call_token() {
        ApiToken configuredTestData = getOtpConfiguredSlowLaneApiToken();
        ApiToken requestTestData = getNearestStationsOtpMapperApiToken();

        ApiToken result = classUnderTest.getNearestStationsApiTokenWith(requestTestData, configuredTestData);

        assertThat(result).isEqualToComparingFieldByFieldRecursively(getNearestStationsOtpSlowLaneApiToken());
    }

}
