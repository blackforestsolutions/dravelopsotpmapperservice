package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.OpenTripPlannerApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Import(OpenTripPlannerTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenTripPlannerApiServiceIT {

    @Autowired
    private OpenTripPlannerApiService classUnderTest;

    @Autowired
    private ApiToken.ApiTokenBuilder openTripPlannerApiTokenIT;

    @Test
    void test_getJourneysBy_with_incorrect_apiToken_returns_no_result() {
        openTripPlannerApiTokenIT.setArrival("Berlin Mitte");
        openTripPlannerApiTokenIT.setArrivalCoordinate(new Point.PointBuilder(13.409600d, 52.509439d).build());

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(openTripPlannerApiTokenIT.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

}
