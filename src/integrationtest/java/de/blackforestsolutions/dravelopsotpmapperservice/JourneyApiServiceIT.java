package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.ApiServiceTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Metrics;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsotpmapperservice.configuration.CoordinateConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

@Import(ApiServiceTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JourneyApiServiceIT {

    @Autowired
    private JourneyApiService classUnderTest;

    @Autowired
    private ApiToken journeyOtpMapperApiToken;

    @Test
    void test_retrieveJourneysFromApiService_with_correct_apiToken_returns_correct_leg_properties() {
        ApiToken testData = new ApiToken(journeyOtpMapperApiToken);

        Flux<Journey> result = classUnderTest.retrieveJourneysFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(1L)
                .thenConsumeWhile(journey -> {
                    assertThat(journey.getLanguage().getLanguage().length()).isEqualTo(2);
                    assertThat(journey.getLegs().size()).isGreaterThan(0);
                    assertThat(journey.getLegs())
                            .allMatch(leg -> leg.getTripId() != null)
                            .allMatch(leg -> leg.getDelayInMinutes().toMillis() >= 0)
                            .allMatch(leg -> leg.getDistanceInKilometers().getValue() > 0)
                            .allMatch(leg -> leg.getVehicleType() != null)
                            .allMatch(leg -> leg.getPolyline().length() > 0)
                            .allMatch(leg -> leg.getWaypoints() != null)
                            .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getX() >= MIN_WGS_84_LONGITUDE))
                            .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getX() <= MAX_WGS_84_LONGITUDE))
                            .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getY() >= MIN_WGS_84_LATITUDE))
                            .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getY() <= MAX_WGS_84_LATITUDE))
                            .allMatch(leg -> leg.getVehicleName() != null)
                            .allMatch(leg -> leg.getVehicleNumber() != null)
                            .allMatch(leg -> leg.getIntermediateStops() != null)
                            .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getName().length() > 0))
                            .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPoint() != null))
                            .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPoint().getX() >= MIN_WGS_84_LONGITUDE))
                            .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPoint().getX() <= MAX_WGS_84_LONGITUDE))
                            .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPoint().getY() >= MIN_WGS_84_LATITUDE))
                            .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPoint().getY() <= MAX_WGS_84_LATITUDE))
                            .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getDepartureTime() != null))
                            .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getArrivalTime() != null))
                            .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPlatform() != null))
                            .allMatch(leg -> leg.getWalkSteps() != null)
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStreetName().length() > 0))
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getDistanceInKilometers().getValue() >= 0d))
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getDistanceInKilometers().getMetric().equals(Metrics.KILOMETERS)))
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStartPoint() != null))
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStartPoint().getX() >= MIN_WGS_84_LONGITUDE))
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStartPoint().getX() <= MAX_WGS_84_LONGITUDE))
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStartPoint().getY() >= MIN_WGS_84_LATITUDE))
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStartPoint().getY() <= MAX_WGS_84_LATITUDE))
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getWalkingDirection() != null))
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getCompassDirection() != null))
                            .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getCircleExit() != null));
                    assertThat(journey.getLegs())
                            .first()
                            .matches(leg -> leg.getDeparture().getArrivalTime() == null);
                    assertThat(journey.getLegs())
                            .last()
                            .matches(leg -> leg.getArrival().getDepartureTime() == null);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_retrieveJourneysFromApiService_with_correct_apiToken_returns_correct_leg_properties_for_departure_and_arrival() {
        ApiToken testData = new ApiToken(journeyOtpMapperApiToken);

        Flux<Journey> result = classUnderTest.retrieveJourneysFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(1L)
                .thenConsumeWhile(journey -> {
                    assertThat(journey.getLanguage().getLanguage().length()).isEqualTo(2);
                    assertThat(journey.getLegs().size()).isGreaterThan(0);
                    assertThat(journey.getLegs())
                            .allMatch(leg -> leg.getDeparture() != null)
                            .allMatch(leg -> !leg.getDeparture().getName().isEmpty())
                            .allMatch(leg -> leg.getDeparture().getPoint() != null)
                            .allMatch(leg -> leg.getDeparture().getPoint().getX() >= MIN_WGS_84_LONGITUDE)
                            .allMatch(leg -> leg.getDeparture().getPoint().getX() <= MAX_WGS_84_LONGITUDE)
                            .allMatch(leg -> leg.getDeparture().getPoint().getY() >= MIN_WGS_84_LATITUDE)
                            .allMatch(leg -> leg.getDeparture().getPoint().getY() <= MAX_WGS_84_LATITUDE)
                            .allMatch(leg -> leg.getDeparture().getDistanceInKilometers() == null)
                            .allMatch(leg -> leg.getDeparture().getPlatform() != null)
                            .allMatch(leg -> leg.getArrival() != null)
                            .allMatch(leg -> !leg.getArrival().getName().isEmpty())
                            .allMatch(leg -> leg.getArrival().getPoint() != null)
                            .allMatch(leg -> leg.getArrival().getPoint().getX() >= MIN_WGS_84_LONGITUDE)
                            .allMatch(leg -> leg.getArrival().getPoint().getX() <= MAX_WGS_84_LONGITUDE)
                            .allMatch(leg -> leg.getArrival().getPoint().getY() >= MIN_WGS_84_LATITUDE)
                            .allMatch(leg -> leg.getArrival().getPoint().getY() <= MAX_WGS_84_LATITUDE)
                            .allMatch(leg -> leg.getArrival().getDistanceInKilometers() == null)
                            .allMatch(leg -> leg.getArrival().getPlatform() != null);
                    assertThat(journey.getLegs())
                            .first()
                            .matches(leg -> leg.getDeparture().getArrivalTime() == null);
                    assertThat(journey.getLegs())
                            .last()
                            .matches(leg -> leg.getArrival().getDepartureTime() == null);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_retrieveJourneysFromApiService_with_incorrect_apiToken_returns_zero_results() {
        ApiToken testData = new ApiToken(journeyOtpMapperApiToken);
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());
        testData.setDepartureCoordinate(new Point.PointBuilder(1.0d, 1.0d).build());

        Flux<Journey> result = classUnderTest.retrieveJourneysFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}