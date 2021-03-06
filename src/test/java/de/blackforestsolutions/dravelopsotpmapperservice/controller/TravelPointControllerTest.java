package de.blackforestsolutions.dravelopsotpmapperservice.controller;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.TravelPointApiService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.TravelPointApiServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getConfiguredNearestStationsOtpMapperApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getNearestStationsOtpMapperApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getFurtwangenTownChurchTravelPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TravelPointControllerTest {

    private final TravelPointApiService travelPointApiService = mock(TravelPointApiServiceImpl.class);

    private final TravelPointController classUnderTest = new TravelPointController(travelPointApiService);

    @Test
    void test_getNearestStationsBy_nearestStationOtpApiToken_is_executed_correctly_and_return_travelPoints() {
        ApiToken testData = getNearestStationsOtpMapperApiToken();
        ArgumentCaptor<ApiToken> requestArg = ArgumentCaptor.forClass(ApiToken.class);
        when(travelPointApiService.retrieveNearestStationsFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.just(getFurtwangenTownChurchTravelPoint()));

        Flux<TravelPoint> result = classUnderTest.getNearestStationsBy(testData);

        verify(travelPointApiService, times(1)).retrieveNearestStationsFromApiService(requestArg.capture());
        assertThat(requestArg.getValue()).isEqualToComparingFieldByFieldRecursively(getNearestStationsOtpMapperApiToken());
        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(travelPoint).isEqualToComparingFieldByFieldRecursively(getFurtwangenTownChurchTravelPoint()))
                .verifyComplete();
    }

    @Test
    void test_getNearestStationsBy_nearestStationOtpApiToken_is_executed_correctly_when_no_results_are_available() {
        ApiToken testData = getConfiguredNearestStationsOtpMapperApiToken();
        when(travelPointApiService.retrieveNearestStationsFromApiService(any(ApiToken.class)))
                .thenReturn(Flux.empty());

        Flux<TravelPoint> result = classUnderTest.getNearestStationsBy(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
