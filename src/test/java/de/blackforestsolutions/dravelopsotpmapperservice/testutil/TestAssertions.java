package de.blackforestsolutions.dravelopsotpmapperservice.testutil;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class TestAssertions {

    public static Consumer<CallStatus<TravelPoint>> getOtpApiNearestStationsAsserts(TravelPoint expectedTravelPoint) {
        return travelPointResult -> {
            assertThat(travelPointResult.getStatus()).isEqualTo(Status.SUCCESS);
            assertThat(travelPointResult.getThrowable()).isNull();
            assertThat(travelPointResult.getCalledObject()).isEqualToComparingFieldByFieldRecursively(expectedTravelPoint);
        };
    }
}
