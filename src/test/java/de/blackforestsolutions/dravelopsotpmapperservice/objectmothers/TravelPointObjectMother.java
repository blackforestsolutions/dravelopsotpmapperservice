package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

public class TravelPointObjectMother {

    public static TravelPoint getGroßhausbergTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Am Großhausberg 8")
                .setCoordinates(new Point(8.209972d, 48.04832d))
                .setDepartureTime(LocalDateTime.parse("2020-09-30T13:08:13"))
                .build();
    }

    public static TravelPoint getFurtwangenIlbenstreetTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Furtwangen Ilbenstraße")
                .setCoordinates(new Point(8.198995d, 48.047922d))
                .setArrivalTime(LocalDateTime.parse("2020-09-30T13:20:59"))
                .setDepartureTime(LocalDateTime.parse("2020-09-30T13:21"))
                .build();
    }

    public static TravelPoint getBleibachSevTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Bleibach SEV")
                .setCoordinates(new Point(7.998644d, 48.127233d))
                .setArrivalTime(LocalDateTime.parse("2020-09-30T14:05"))
                .setDepartureTime(LocalDateTime.parse("2020-09-30T14:12"))
                .build();
    }

    public static TravelPoint getGuetenbachTownHallTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Gütenbach Rathaus")
                .setCoordinates(new Point(8.138826d, 48.044378d))
                .setArrivalTime(LocalDateTime.parse("2020-09-30T13:31"))
                .setDepartureTime(LocalDateTime.parse("2020-09-30T13:31"))
                .build();
    }

    public static TravelPoint getSimonswaldTownHallTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Simonswald Rathaus")
                .setCoordinates(new Point(8.056936d, 48.100225d))
                .setArrivalTime(LocalDateTime.parse("2020-09-30T13:52"))
                .setDepartureTime(LocalDateTime.parse("2020-09-30T13:52"))
                .build();
    }

    public static TravelPoint getWaldkirchKastelberghalleTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Waldkirch Kastelberghalle")
                .setCoordinates(new Point(7.952947d, 48.090458d))
                .setArrivalTime(LocalDateTime.parse("2020-09-30T14:29"))
                .setDepartureTime(LocalDateTime.parse("2020-09-30T14:29:01"))
                .build();
    }

    public static TravelPoint getKollnauTrainStationTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Kollnau Bf (Bus)")
                .setCoordinates(new Point(7.972467d, 48.101665d))
                .setArrivalTime(LocalDateTime.parse("2020-09-30T14:22"))
                .setDepartureTime(LocalDateTime.parse("2020-09-30T14:22"))
                .build();
    }

    public static TravelPoint getWaldkirchTownCenterTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Waldkirch Stadtmitte")
                .setCoordinates(new Point(7.961104d, 48.093896d))
                .setArrivalTime(LocalDateTime.parse("2020-09-30T14:26"))
                .setDepartureTime(LocalDateTime.parse("2020-09-30T14:26"))
                .build();
    }

    public static TravelPoint getSickAgTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Sick AG")
                .setCoordinates(new Point(7.950507d, 48.088204d))
                .setArrivalTime(LocalDateTime.parse("2020-09-30T14:34:56"))
                .build();
    }

    public static TravelPoint getMannheimHbfTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Mannheim Hbf")
                .setCoordinates(new Point(8.464279174804688d, 49.483628140481024d))
                .setDepartureTime(LocalDateTime.parse("2020-10-06T01:20:11"))
                .build();
    }

    public static TravelPoint getMannheimUniversityTravelPoint() {
        return new TravelPoint.TravelPointBuilder()
                .setName("Universität")
                .setPlatform("C")
                .setCoordinates(new Point(8.4633481d, 49.4821881d))
                .setArrivalTime(LocalDateTime.parse("2020-10-06T01:24:59"))
                .setDepartureTime(LocalDateTime.parse("2020-10-06T01:25"))
                .build();
    }


}
