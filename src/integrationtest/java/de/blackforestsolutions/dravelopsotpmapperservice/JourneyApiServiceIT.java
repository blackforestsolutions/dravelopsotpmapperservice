package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.BwApiTokenConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Point;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.ZonedDateTime;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOtpMapperApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;

@Import(BwApiTokenConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JourneyApiServiceIT {

    @Autowired
    private JourneyApiService classUnderTest;

    @Autowired
    private BwApiTokenConfiguration bwApiTokenConfiguration;

    @Test
    void test_retrieveJourneysFromApiService_with_correct_apiToken_returns_results() {
        ApiToken testData = bwApiTokenConfiguration.setOtpMapperApiTokenConfiguration();

        String jsonTestData = toJson(new ApiToken.ApiTokenBuilder().build());

        Flux<String> result = classUnderTest.retrieveJourneysFromApiService(jsonTestData);

        StepVerifier.create(result)
                .expectNextCount(1L)
                .thenConsumeWhile(journey -> {
                    Journey actualJourney = retrieveJsonToPojo(journey, Journey.class);
                    assertThat(actualJourney.getLegs().size()).isGreaterThan(0);
                    assertThat(actualJourney.getLegs())
                            .first()
                            .matches(leg -> leg.getDeparture().getDepartureTime().isAfter(ZonedDateTime.from(testData.getDateTime())))
                            .matches(leg -> leg.getDeparture().getName().equals("Am Großhausberg"));
                    assertThat(actualJourney.getLegs())
                            .last()
                            .matches(leg -> leg.getArrival().getArrivalTime().isAfter(ZonedDateTime.from(testData.getDateTime())))
                            .matches(leg -> leg.getArrival().getName().equals("SICK AG"));
                    assertThat(actualJourney.getLegs())
                            .allMatch(leg -> leg.getDelayInMinutes().toMillis() >= 0)
                            .allMatch(leg -> leg.getDistanceInKilometers().getValue() > 0)
                            .allMatch(leg -> leg.getVehicleType() != null)
                            .allMatch(leg -> leg.getDeparture() != null)
                            .allMatch(leg -> leg.getArrival() != null)
                            .allMatch(leg -> leg.getWaypoints().size() > 0);
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