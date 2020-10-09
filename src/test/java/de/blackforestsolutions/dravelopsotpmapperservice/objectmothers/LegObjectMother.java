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

    public static Leg getGrosshausbergToFurtwangenIlbenstreetLeg() {
        return new Leg.LegBuilder(TEST_UUID_2)
                .setDelay(Duration.ZERO)
                .setDistanceInKilometers(new Distance(0.977d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.WALK)
                .setDeparture(getGrosshausbergTravelPoint())
                .setArrival(getFurtwangenIlbenstreetTravelPoint())
                .setTrack(getExampleTrack())
                .build();
    }

    public static Leg getFurtwangenIlbenstreetToBleibachLeg() {
        return new Leg.LegBuilder(TEST_UUID_3)
                .setDelay(Duration.ZERO)
                .setDistanceInKilometers(new Distance(26.394d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.BUS)
                .setTravelProvider(getSuedbadenTravelProvider())
                .setDeparture(getFurtwangenIlbenstreetTravelPoint())
                .setArrival(getBleibachSevTravelPoint())
                .setTrack(getExampleTrack())
                .setVehicleNumber("272")
                .setVehicleName("Waldkirch Gymnasium - Furtwangen Rößleplatz")
                .setIntermediateStops(getFurtwangenIlbenStreetToBleibachIntermediateStops())
                .build();
    }

    public static Leg getBleibachToWaldkirchKastelberghalleLeg() {
        return new Leg.LegBuilder(TEST_UUID_4)
                .setDelay(Duration.ZERO)
                .setDistanceInKilometers(new Distance(6.784d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.BUS)
                .setTravelProvider(getSuedbadenTravelProvider())
                .setDeparture(getBleibachSevTravelPoint())
                .setArrival(getWaldkirchKastelberghalleTravelPoint())
                .setTrack(getExampleTrack())
                .setVehicleNumber("201")
                .setVehicleName("Oberprechtal Forellenhof - Emmendingen Bahnhof/ZOB")
                .setIntermediateStops(getBleibachToWaldkirchKastelberghalleIntermediateStops())
                .build();
    }

    public static Leg getWaldkirchKastelberghalleToSickLeg() {
        return new Leg.LegBuilder(TEST_UUID_5)
                .setDelay(Duration.ZERO)
                .setDistanceInKilometers(new Distance(0.445d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.WALK)
                .setDeparture(getWaldkirchKastelberghalleTravelPoint())
                .setArrival(getSickAgTravelPoint())
                .setTrack(getExampleTrack())
                .build();
    }

    public static Leg getMannheimHbfToMannheimUniversityLeg() {
        return new Leg.LegBuilder(TEST_UUID_2)
                .setDelay(Duration.ZERO)
                .setDistanceInKilometers(new Distance(0.320d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.WALK)
                .setDeparture(getMannheimHbfTravelPoint())
                .setArrival(getMannheimUniversityTravelPoint())
                .setTrack(getExampleTrack())
                .build();
    }

    public static Leg getMannheimUniversityToMannheimBerlinerPlaceLeg() {
        return new Leg.LegBuilder(TEST_UUID_3)
                .setDelay(Duration.ZERO)
                .setDistanceInKilometers(new Distance(0.956d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.TRAM)
                .setTravelProvider(getRnvTravelProvider())
                .setDeparture(getMannheimUniversityTravelPoint())
                .setArrival(getBerlinerPlaceTravelPoint())
                .setTrack(getExampleTrack())
                .setVehicleNumber("4")
                .setVehicleName("Bad Dürkheim - LU Oggersheim - LU Hbf - Berliner Platz - MA Hbf - Wasserturm - Paradeplatz - Alte Feuerwache - Schafweide - Universitätsklinikum - Bonafitiuskirche - Ulmenweg - Waldfriedhof")
                .setIntermediateStops(getMannheimUniversityToMannheimBerlinerPlaceIntermediateStop())
                .build();
    }

    public static Leg getBerlinerPlaceToDestinationLeg() {
        return new Leg.LegBuilder(TEST_UUID_4)
                .setDelay(Duration.ZERO)
                .setDistanceInKilometers(new Distance(0.319d, Metrics.KILOMETERS))
                .setVehicleType(VehicleType.WALK)
                .setDeparture(getBerlinerPlaceTravelPoint())
                .setArrival(getLudwigsburgCenterTravelPoint())
                .setTrack(getExampleTrack())
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
