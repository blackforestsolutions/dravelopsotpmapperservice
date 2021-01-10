package de.blackforestsolutions.dravelopsotpmapperservice.controller;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOtpMapperApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getJourneyWithEmptyFields;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.UUIDObjectMother.TEST_UUID_1;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.toJson;
import static org.apache.commons.lang.StringUtils.deleteWhitespace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JourneyControllerTest {

    private final JourneyApiService journeyApiService = mock(JourneyApiService.class);

    private final JourneyController classUnderTest = new JourneyController(journeyApiService);

    @Test
    void test_retrieveOpenTripPlannerJourneys_is_executed_correctly_and_return_journeys() {
        ApiToken testData = getOtpMapperApiToken();
        ArgumentCaptor<ApiToken> requestArg = ArgumentCaptor.forClass(ApiToken.class);
        when(journeyApiService.retrieveJourneysFromApiService(any(ApiToken.class))).thenReturn(Flux.just(getJourneyWithEmptyFields(TEST_UUID_1)));

        Flux<Journey> result = classUnderTest.retrieveOpenTripPlannerJourneys(testData);

        verify(journeyApiService, times(1)).retrieveJourneysFromApiService(requestArg.capture());
        assertThat(requestArg.getValue()).isEqualToComparingFieldByField(getOtpMapperApiToken());
        StepVerifier.create(result)
                .assertNext(journey -> assertThat(deleteWhitespace(toJson(journey))).isEqualTo(deleteWhitespace(toJson(getJourneyWithEmptyFields(TEST_UUID_1)))))
                .verifyComplete();
    }

    @Test
    void test_retrieveOpenTripPlannerJourneys_is_executed_correctly_when_no_results_are_available() {
        ApiToken testData = getOtpMapperApiToken();
        when(journeyApiService.retrieveJourneysFromApiService(any(ApiToken.class))).thenReturn(Flux.empty());

        Flux<Journey> result = classUnderTest.retrieveOpenTripPlannerJourneys(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

}


