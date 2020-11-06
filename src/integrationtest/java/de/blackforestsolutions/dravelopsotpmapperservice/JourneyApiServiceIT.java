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

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOtpMapperApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JourneyApiServiceIT {

    @Autowired
    private JourneyApiService classUnderTest;

    @Test
    void test_retrieveJourneysFromApiService_with_correct_apiToken_returns_results() {
        ApiToken testData = getOtpMapperApiToken();
        String jsonTestData = toJson(testData);

        Flux<String> result = classUnderTest.retrieveJourneysFromApiService(jsonTestData);

        StepVerifier.create(result)
                .expectNextCount(1L)
                .thenConsumeWhile(journey -> {
                    Journey actualJourney = retrieveJsonToPojo(journey, Journey.class);
                    assertThat(actualJourney.getLegs().size()).isGreaterThan(0);
                    assertThat(actualJourney.getLegs())
                            .allMatch(leg -> leg.getId() != null)
                            .allMatch(leg -> leg.getDelayInMinutes().toMillis() >= 0)
                            .allMatch(leg -> leg.getDistanceInKilometers().getValue() > 0)
                            .allMatch(leg -> leg.getVehicleType() != null)
                            .allMatch(leg -> leg.getWaypoints().size() > 0)
                            .allMatch(leg -> leg.getIntermediateStops().size() == 0 || leg.getIntermediateStops().size() > 0)
                            .allMatch(leg -> leg.getVehicleName() != null)
                            .allMatch(leg -> leg.getVehicleNumber() != null)
                            .allMatch(leg -> leg.getDeparture() != null)
                            .allMatch(leg -> !leg.getDeparture().getName().isEmpty())
                            .allMatch(leg -> leg.getDeparture().getPoint() != null)
                            .allMatch(leg -> leg.getArrival() != null)
                            .allMatch(leg -> !leg.getArrival().getName().isEmpty())
                            .allMatch(leg -> leg.getArrival().getPoint() != null);
                    assertThat(actualJourney.getLegs())
                            .first()
                            .matches(leg -> leg.getDeparture().getArrivalTime() == null);
                    assertThat(actualJourney.getLegs())
                            .last()
                            .matches(leg -> leg.getArrival().getDepartureTime() == null);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_retrieveJourneysFromApiService_with_incorrect_apiToken_returns_zero_results() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpMapperApiToken());
        testData.setArrival("Berlin Mitte");
        testData.setArrivalCoordinate(new Point(13.409600d, 52.509439d));
        String jsonTestData = toJson(testData.build());

        Flux<String> result = classUnderTest.retrieveJourneysFromApiService(jsonTestData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
