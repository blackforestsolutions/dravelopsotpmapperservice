package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.ZonedDateTime;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.ApiTokenObjectMother.getRequestToken;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutils.TestUtils.retrieveJsonToPojo;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutils.TestUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JourneyApiServiceIT {

    @Autowired
    private JourneyApiService classUnderTest;

    @Test
    void test_retrieveJourneysFromApiService_with_correct_apiToken_returns_results() {
        ApiToken testData = getRequestToken();
        String jsonTestData = toJson(testData);

        Flux<String> result = classUnderTest.retrieveJourneysFromApiService(jsonTestData);

        StepVerifier.create(result)
                .expectNextCount(1L)
                .thenConsumeWhile(journey -> {
                    Journey actualJourney = retrieveJsonToPojo(journey, Journey.class);
                    assertThat(actualJourney.getLegs().size()).isGreaterThan(0);
                    assertThat(actualJourney.getLegs().values())
                            .first()
                            .matches(leg -> leg.getDeparture().getDepartureTime().isAfter(ZonedDateTime.from(testData.getDateTime())));
                    assertThat(actualJourney.getLegs().values())
                            .last()
                            .matches(leg -> leg.getArrival().getArrivalTime().isAfter(ZonedDateTime.from(testData.getDateTime())));
                    assertThat(actualJourney.getLegs().values())
                            .allMatch(leg -> leg.getDelay().toMillis() >= 0)
                            .allMatch(leg -> leg.getDistance().getValue() > 0)
                            .allMatch(leg -> leg.getVehicleType() != null)
                            .allMatch(leg -> leg.getDeparture() != null)
                            .allMatch(leg -> leg.getArrival() != null)
                            .allMatch(leg -> leg.getTrack().size() > 0)
                            .allMatch(leg -> leg.getDuration().toMillis() > 0);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_retrieveJourneysFromApiService_with_incorrect_apiToken_returns_zero_results() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getRequestToken());
        testData.setArrival("Berlin Mitte");
        testData.setArrivalCoordinate(new Point(13.409600d, 52.509439d));

        Flux<String> result = classUnderTest.retrieveJourneysFromApiService(toJson(testData.build()));

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
