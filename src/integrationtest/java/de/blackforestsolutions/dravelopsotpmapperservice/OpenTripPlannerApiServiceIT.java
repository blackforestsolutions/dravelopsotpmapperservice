package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.*;
import de.blackforestsolutions.dravelopsdatamodel.exception.NoExternalResultFoundException;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.OpenTripPlannerApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Import(OpenTripPlannerTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenTripPlannerApiServiceIT {

    @Autowired
    private OpenTripPlannerApiService classUnderTest;

    @Autowired
    private ApiToken.ApiTokenBuilder openTripPlannerApiTokenIT;

    @Test
    void test_getJourneysBy_with_incorrect_apiToken_returns_noExternalResultFoundException() {
        openTripPlannerApiTokenIT.setArrival("Berlin Mitte");
        openTripPlannerApiTokenIT.setArrivalCoordinate(new Point.PointBuilder(13.409600d, 52.509439d).build());

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(openTripPlannerApiTokenIT.build());

        StepVerifier.create(result)
                .assertNext(journey -> {
                    assertThat(journey.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(journey.getThrowable()).isInstanceOf(NoExternalResultFoundException.class);
                    assertThat(journey.getCalledObject()).isNull();
                })
                .verifyComplete();
    }

}
