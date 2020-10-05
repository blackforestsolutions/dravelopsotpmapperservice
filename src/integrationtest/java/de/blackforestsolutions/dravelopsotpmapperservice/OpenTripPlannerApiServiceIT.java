package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Optimization;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.exception.NoExternalResultFoundException;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.OpenTripPlannerApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenTripPlannerApiServiceIT {

    @Autowired
    private ApiToken openTripPlannerApiToken;

    @Autowired
    private OpenTripPlannerApiService classUnderTest;

    @Test
    void test_getJourneysBy_with_incorrect_apiToken_returns_noExternalResultFoundException() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(openTripPlannerApiToken);
        testData.setOptimize(Optimization.QUICK);
        testData.setIsArrivalDateTime(false);
        testData.setDateTime(ZonedDateTime.parse("2020-09-30T13:00:00+02:00"));
        testData.setDepartureCoordinate(new Point(8.209972d, 48.048320d));
        testData.setArrival("Berlin Mitte");
        testData.setArrivalCoordinate(new Point(13.409600d, 52.509439d));

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData.build());

        StepVerifier.create(result)
                .assertNext(journey -> {
                    assertThat(journey.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(journey.getThrowable()).isInstanceOf(NoExternalResultFoundException.class);
                    assertThat(journey.getCalledObject()).isNull();
                })
                .verifyComplete();
    }
}
