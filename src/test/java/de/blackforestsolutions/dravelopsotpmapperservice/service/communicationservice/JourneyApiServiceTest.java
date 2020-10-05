package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.RequestTokenHandlerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.ApiTokenObjectMother.*;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.JourneyObjectMother.getJourneyWithEmptyFields;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.UUIDObjectMother.TEST_UUID_1;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutils.TestUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JourneyApiServiceTest {

    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);
    private final RequestTokenHandlerService requestTokenHandlerService = spy(RequestTokenHandlerService.class);
    private final ApiToken openTripPlannerApiToken = getOpenTripPlannerConfiguredApiToken();
    private final OpenTripPlannerApiService openTripPlannerApiService = mock(OpenTripPlannerApiService.class);

    private final JourneyApiService classUnderTest = new JourneyApiServiceImpl(exceptionHandlerService, requestTokenHandlerService, openTripPlannerApiToken, openTripPlannerApiService);

    @BeforeEach
    void init() {
        when(requestTokenHandlerService.getRequestApiTokenWith(any(ApiToken.class), any(ApiToken.class)))
                .thenReturn(getOpenTripPlannerApiToken());

        when(openTripPlannerApiService.getJourneysBy(any(ApiToken.class))).thenReturn(Flux.just(
                        new CallStatus<>(getJourneyWithEmptyFields(TEST_UUID_1), Status.SUCCESS, null),
                        new CallStatus<>(null, Status.FAILED, new Exception())
                ));
    }

    @Test
    void test_retrieveJourneysFromApiServices_with_userApiToken_requestTokenHandler_exceptionHandler_and_apiService_returns_journeys_as_json_asynchronously_and_sort_out_journeys() {
        String userRequestTokenTestData = toJson(getRequestToken());

        Flux<String> result = classUnderTest.retrieveJourneysFromApiService(userRequestTokenTestData);

        StepVerifier.create(result)
                .expectNext(toJson(getJourneyWithEmptyFields(TEST_UUID_1)))
                .verifyComplete();
    }

    @Test
    void test_retrieveJourneysFromApiService_with_userApiToken_requestTokenHandler_exceptionHandler_and_apiService_is_executed_correctly() {
        ArgumentCaptor<ApiToken> userRequestTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> configuredTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<ApiToken> mergedTokenArg = ArgumentCaptor.forClass(ApiToken.class);
        ArgumentCaptor<CallStatus<Journey>> journeyArg = ArgumentCaptor.forClass(CallStatus.class);
        String userRequestTokenTestData = toJson(getRequestToken());

        classUnderTest.retrieveJourneysFromApiService(userRequestTokenTestData).collectList().block();

        InOrder inOrder = inOrder(requestTokenHandlerService, openTripPlannerApiService, exceptionHandlerService);
        inOrder.verify(requestTokenHandlerService, times(1)).getRequestApiTokenWith(userRequestTokenArg.capture(), configuredTokenArg.capture());
        inOrder.verify(openTripPlannerApiService, times(1)).getJourneysBy(mergedTokenArg.capture());
        inOrder.verify(exceptionHandlerService, times(2)).handleExceptions(journeyArg.capture());
        inOrder.verifyNoMoreInteractions();
        assertThat(userRequestTokenArg.getValue()).isEqualToComparingFieldByField(getRequestToken());
        assertThat(configuredTokenArg.getValue()).isEqualToComparingFieldByField(getOpenTripPlannerConfiguredApiToken());
        assertThat(mergedTokenArg.getValue()).isEqualToComparingFieldByField(getOpenTripPlannerApiToken());
        assertThat(journeyArg.getAllValues().size()).isEqualTo(2);
    }

    @Test
    void test_retrieveJourneysFromApiServices_returns_empty_flux_when_an_exceptions_is_thrown() {
        ArgumentCaptor<Exception> exceptionArg = ArgumentCaptor.forClass(Exception.class);

        Flux<String> result = classUnderTest.retrieveJourneysFromApiService("");

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();

        verify(exceptionHandlerService, times(1)).handleExceptions(exceptionArg.capture());
        assertThat(exceptionArg.getAllValues().size()).isEqualTo(1);
        assertThat(exceptionArg.getValue()).isInstanceOf(MismatchedInputException.class);
    }

    @Test
    void test_retrieveJourneysFromApiService_handles_distinct_exception_correctly() {
        String userRequestTokenTestData = toJson(getRequestToken());
        when(openTripPlannerApiService.getJourneysBy(any(ApiToken.class))).thenReturn(Flux.just(
                new CallStatus<>(getJourneyWithEmptyFields(null), Status.SUCCESS, null)
        ));

        Flux<String> result = classUnderTest.retrieveJourneysFromApiService(userRequestTokenTestData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleExceptions(any(Throwable.class));
    }
}
