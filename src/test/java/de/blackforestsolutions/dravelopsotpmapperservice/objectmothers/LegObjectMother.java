package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsdatamodel.Leg;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.VehicleType;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.time.Duration;
import java.util.LinkedList;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TrackObjectMother.getExampleTrack;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TrackObjectMother.getFurtwangenExampleTrack;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TravelProviderObjectMother.getSuedbadenTravelProvider;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.UUIDObjectMother.*;

public class LegObjectMother {

    public static Leg getGroßhausbergToFurtwangenIlbenstreetLeg() {
        TravelPoint departure = getGroßhausbergTravelPoint();
        TravelPoint arrival = getFurtwangenIlbenstreetTravelPoint();
        return new Leg.LegBuilder(TEST_UUID_2)
                .setDelay(Duration.ZERO)
                .setDistance(new Distance(0.977d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.WALK)
                .setDeparture(departure)
                .setArrival(arrival)
                .setTrack(getExampleTrack())
                .setDuration(Duration.between(departure.getDepartureTime(), arrival.getArrivalTime()))
                .build();
    }

    public static Leg getFurtwangenIlbenstreetToBleibachLeg() {
        TravelPoint departure = getFurtwangenIlbenstreetTravelPoint();
        TravelPoint arrival = getBleibachSevTravelPoint();
        return new Leg.LegBuilder(TEST_UUID_3)
                .setDelay(Duration.ZERO)
                .setDistance(new Distance(26.394d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.BUS)
                .setTravelProvider(getSuedbadenTravelProvider())
                .setDeparture(departure)
                .setArrival(arrival)
                .setTrack(getExampleTrack())
                .setVehicleNumber("272")
                .setVehicleName("Waldkirch Gymnasium - Furtwangen Rößleplatz")
                .setDuration(Duration.between(departure.getDepartureTime(), arrival.getArrivalTime()))
                .setIntermediateStops(getFurtwangenIlbenStreetToBleibachIntermediateStops())
                .build();
    }

    public static Leg getBleibachToWaldkirchKastelberghalleLeg() {
        TravelPoint departure = getBleibachSevTravelPoint();
        TravelPoint arrival = getWaldkirchKastelberghalleTravelPoint();
        return new Leg.LegBuilder(TEST_UUID_4)
                .setDelay(Duration.ZERO)
                .setDistance(new Distance(6.784d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.BUS)
                .setTravelProvider(getSuedbadenTravelProvider())
                .setDeparture(departure)
                .setArrival(arrival)
                .setTrack(getExampleTrack())
                .setVehicleNumber("201")
                .setVehicleName("Oberprechtal Forellenhof - Emmendingen Bahnhof/ZOB")
                .setDuration(Duration.between(departure.getDepartureTime(), arrival.getArrivalTime()))
                .setIntermediateStops(getBleibachToWaldkirchKastelberghalleIntermediateStops())
                .build();
    }

    public static Leg getWaldkirchKastelberghalleToSickLeg() {
        TravelPoint departure = getWaldkirchKastelberghalleTravelPoint();
        TravelPoint arrival = getSickAgTravelPoint();
        return new Leg.LegBuilder(TEST_UUID_5)
                .setDelay(Duration.ZERO)
                .setDistance(new Distance(0.445d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.WALK)
                .setDeparture(departure)
                .setArrival(arrival)
                .setTrack(getExampleTrack())
                .setDuration(Duration.between(departure.getDepartureTime(), arrival.getArrivalTime()))
                .build();
    }

    public static Leg getMannheimHbfToMannheimUniversityLeg() {
        TravelPoint departure = getMannheimHbfTravelPoint();
        TravelPoint arrival = getMannheimUniversityTravelPoint();
        return new Leg.LegBuilder(TEST_UUID_2)
                .setDelay(Duration.ZERO)
                .setDistance(new Distance(0.320d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.WALK)
                .setDeparture(departure)
                .setArrival(arrival)
                .setTrack(getExampleTrack())
                .setDuration(Duration.between(departure.getDepartureTime(), arrival.getArrivalTime()))
                .build();
    }

    public static Leg getMannheimUniversityToMannheimBerlinerPlaceLeg() {
        return new Leg.LegBuilder(TEST_UUID_3)
                .setDelay(Duration.ZERO)
                .setDistance(new Distance(0.956d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.TRAM)
                .setTravelProvider()
    }

    private static LinkedList<TravelPoint> getFurtwangenIlbenStreetToBleibachIntermediateStops() {
        LinkedList<TravelPoint> intermediateStops = new LinkedList<>();
        intermediateStops.add(getGuetenbachTownHallTravelPoint());
        intermediateStops.add(getSimonswaldTownHallTravelPoint());
        return intermediateStops;
    }

    private static LinkedList<TravelPoint> getBleibachToWaldkirchKastelberghalleIntermediateStops() {
        LinkedList<TravelPoint> intermediateStops = new LinkedList<>();
        intermediateStops.add(getKollnauTrainStationTravelPoint());
        intermediateStops.add(getWaldkirchTownCenterTravelPoint());
        return intermediateStops;
    }
}
