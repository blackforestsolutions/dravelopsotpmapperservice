package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsdatamodel.Leg;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsdatamodel.VehicleType;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.time.Duration;
import java.util.LinkedList;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TrackObjectMother.getExampleTrack;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TravelProviderObjectMother.getRnvTravelProvider;
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
        TravelPoint departure = getMannheimUniversityTravelPoint();
        TravelPoint arrival = getBerlinerPlaceTravelPoint();
        return new Leg.LegBuilder(TEST_UUID_3)
                .setDelay(Duration.ZERO)
                .setDistance(new Distance(0.956d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.TRAM)
                .setTravelProvider(getRnvTravelProvider())
                .setDeparture(departure)
                .setArrival(arrival)
                .setTrack(getExampleTrack())
                .setVehicleNumber("4")
                .setVehicleName("Bad Dürkheim - LU Oggersheim - LU Hbf - Berliner Platz - MA Hbf - Wasserturm - Paradeplatz - Alte Feuerwache - Schafweide - Universitätsklinikum - Bonafitiuskirche - Ulmenweg - Waldfriedhof")
                .setDuration(Duration.between(departure.getDepartureTime(), arrival.getArrivalTime()))
                .setIntermediateStops(getMannheimUniversityToMannheimBerlinerPlaceIntermediateStop())
                .build();
    }

    public static Leg getBerlinerPlaceToDestinationLeg() {
        TravelPoint departure = getBerlinerPlaceTravelPoint();
        TravelPoint arrival = getLudwigsburgCenterTravelPoint();
        return new Leg.LegBuilder(TEST_UUID_4)
                .setDelay(Duration.ZERO)
                .setDistance(new Distance(0.319d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.WALK)
                .setDeparture(departure)
                .setArrival(arrival)
                .setTrack(getExampleTrack())
                .setDuration(Duration.between(departure.getDepartureTime(), arrival.getArrivalTime()))
                .build();

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

    private static LinkedList<TravelPoint> getMannheimUniversityToMannheimBerlinerPlaceIntermediateStop() {
        LinkedList<TravelPoint> intermediateStops = new LinkedList<>();
        intermediateStops.add(getKonradAdenauerBrTravelPoint());
        return intermediateStops;
    }
}
