package de.blackforestsolutions.dravelopsotpmapperservice.controller;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getJourneyOtpMapperApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getJourneyWithEmptyFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JourneyControllerTest {

    private final JourneyApiService journeyApiService = mock(JourneyApiServiceImpl.class);

    private final JourneyController classUnderTest = new JourneyController(journeyApiService);

    @Test
    void test_getJourneysBy_is_executed_correctly_and_return_journeys() {
        ApiToken testData = getJourneyOtpMapperApiToken();
        ArgumentCaptor<ApiToken> requestArg = ArgumentCaptor.forClass(ApiToken.class);
        when(journeyApiService.retrieveJourneysFromApiService(any(ApiToken.class))).thenReturn(Flux.just(getJourneyWithEmptyFields()));

        Flux<Journey> result = classUnderTest.getJourneysBy(testData);

        verify(journeyApiService, times(1)).retrieveJourneysFromApiService(requestArg.capture());
        assertThat(requestArg.getValue()).isEqualToComparingFieldByFieldRecursively(getJourneyOtpMapperApiToken());
        StepVerifier.create(result)
                .assertNext(journey -> assertThat(journey).isEqualToComparingFieldByFieldRecursively(getJourneyWithEmptyFields()))
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_is_executed_correctly_when_no_results_are_available() {
        ApiToken testData = getJourneyOtpMapperApiToken();
        when(journeyApiService.retrieveJourneysFromApiService(any(ApiToken.class))).thenReturn(Flux.empty());

        Flux<Journey> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

}


