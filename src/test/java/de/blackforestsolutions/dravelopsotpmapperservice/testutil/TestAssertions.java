package de.blackforestsolutions.dravelopsotpmapperservice.testutil;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.springframework.data.geo.Metrics;

import java.util.function.Consumer;

import static de.blackforestsolutions.dravelopsotpmapperservice.configuration.CoordinateConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestAssertions {

    public static Consumer<CallStatus<TravelPoint>> getOtpApiNearestStationsAsserts(TravelPoint expectedTravelPoint) {
        return travelPointResult -> {
            assertThat(travelPointResult.getStatus()).isEqualTo(Status.SUCCESS);
            assertThat(travelPointResult.getThrowable()).isNull();
            assertThat(travelPointResult.getCalledObject()).isEqualToComparingFieldByFieldRecursively(expectedTravelPoint);
        };
    }

    public static Consumer<TravelPoint> getNearestStationsAssertions() {
        return travelPoint -> {
            assertThat(travelPoint).isNotNull();
            assertThat(travelPoint.getStopId()).isNotEmpty();
            assertThat(travelPoint.getStopSequence()).isEqualTo(-1L);
            assertThat(travelPoint.getName()).isNotEmpty();
            assertThat(travelPoint.getPoint()).isNotNull();
            assertThat(travelPoint.getPoint().getX()).isGreaterThanOrEqualTo(MIN_WGS_84_LONGITUDE);
            assertThat(travelPoint.getPoint().getX()).isLessThanOrEqualTo(MAX_WGS_84_LONGITUDE);
            assertThat(travelPoint.getPoint().getY()).isGreaterThanOrEqualTo(MIN_WGS_84_LATITUDE);
            assertThat(travelPoint.getPoint().getY()).isLessThanOrEqualTo(MAX_WGS_84_LATITUDE);
            assertThat(travelPoint.getDistanceInKilometers()).isNotNull();
            assertThat(travelPoint.getDistanceInKilometers().getValue()).isGreaterThanOrEqualTo(MIN_DISTANCE_IN_KILOMETERS_TO_POINT);
            assertThat(travelPoint.getDepartureTime()).isNull();
            assertThat(travelPoint.getDepartureDelayInSeconds().isZero()).isTrue();
            assertThat(travelPoint.getArrivalTime()).isNull();
            assertThat(travelPoint.getArrivalDelayInSeconds().isZero()).isTrue();
            assertThat(travelPoint.getPlatform()).isEmpty();
        };
    }

    public static Consumer<Journey> getOtpMapperServiceLegAssertions() {
        return getLegPropertiesAssertions().andThen(journey -> {
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getDepartureDelayInSeconds().isZero()))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getArrivalDelayInSeconds().isZero()));
        });
    }

    public static Consumer<Journey> getLegPropertiesAssertions() {
        return getBaseLegPropertiesAssertions().andThen(journey -> {
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getTripId() != null)
                    .allMatch(leg -> leg.getPolyline().length() > 0)
                    .allMatch(leg -> leg.getIntermediateStops().stream().noneMatch(travelPoint -> travelPoint.getStopId().isEmpty()));
        });
    }

    private static Consumer<Journey> getBaseLegPropertiesAssertions() {
        return getArrivalAndDepartureLegAssertions()
                .andThen(getWaypointsLegAssertions())
                .andThen(getIntermediateStopsLegAssertions())
                .andThen(getWalkStepsLegAssertions())
                .andThen(journey -> {
                    assertThat(journey.getLegs())
                            .allMatch(leg -> leg.getDistanceInKilometers().getValue() > 0)
                            .allMatch(leg -> leg.getVehicleType() != null)
                            .allMatch(leg -> leg.getVehicleName() != null)
                            .allMatch(leg -> leg.getVehicleNumber() != null);
                    assertThat(journey.getLegs())
                            .first()
                            .matches(leg -> leg.getDeparture().getArrivalTime() == null);
                    assertThat(journey.getLegs())
                            .last()
                            .matches(leg -> leg.getArrival().getDepartureTime() == null);
                });
    }

    private static Consumer<Journey> getArrivalAndDepartureLegAssertions() {
        return journey -> {
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
        };
    }

    private static Consumer<Journey> getWaypointsLegAssertions() {
        return journey -> {
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getWaypoints() != null)
                    .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getX() >= MIN_WGS_84_LONGITUDE))
                    .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getX() <= MAX_WGS_84_LONGITUDE))
                    .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getY() >= MIN_WGS_84_LATITUDE))
                    .allMatch(leg -> leg.getWaypoints().stream().allMatch(waypoint -> waypoint.getY() <= MAX_WGS_84_LATITUDE));
        };
    }

    private static Consumer<Journey> getIntermediateStopsLegAssertions() {
        return journey -> {
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getIntermediateStops().size() == 0 || leg.getIntermediateStops().size() > 0)
                    // actually all fields should have a stop sequence greater than equal zero but this field is not provided anymore in otp api
                    // .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getStopSequence() >= 0L))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getStopSequence() >= -1L))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getName().length() > 0))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPoint() != null))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPoint().getX() >= MIN_WGS_84_LONGITUDE))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPoint().getX() <= MAX_WGS_84_LONGITUDE))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPoint().getY() >= MIN_WGS_84_LATITUDE))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPoint().getY() <= MAX_WGS_84_LATITUDE))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getDepartureTime() != null))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getArrivalTime() != null))
                    .allMatch(leg -> leg.getIntermediateStops().stream().allMatch(travelPoint -> travelPoint.getPlatform() != null));
        };
    }

    private static Consumer<Journey> getWalkStepsLegAssertions() {
        return journey -> {
            assertThat(journey.getLegs())
                    .allMatch(leg -> leg.getWalkSteps() != null)
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStreetName().length() > 0))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getDistanceInKilometers().getValue() >= 0))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getDistanceInKilometers().getMetric().equals(Metrics.KILOMETERS)))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStartPoint() != null))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStartPoint().getX() >= MIN_WGS_84_LONGITUDE))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStartPoint().getX() <= MAX_WGS_84_LONGITUDE))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStartPoint().getY() >= MIN_WGS_84_LATITUDE))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getStartPoint().getY() <= MAX_WGS_84_LATITUDE))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getEndPoint() != null))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getEndPoint().getX() >= MIN_WGS_84_LONGITUDE))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getEndPoint().getX() <= MAX_WGS_84_LONGITUDE))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getEndPoint().getY() >= MIN_WGS_84_LATITUDE))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getEndPoint().getY() <= MAX_WGS_84_LATITUDE))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getWalkingDirection() != null))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getCompassDirection() != null))
                    .allMatch(leg -> leg.getWalkSteps().stream().allMatch(walkStep -> walkStep.getCircleExit() != null));
        };
    }
}
