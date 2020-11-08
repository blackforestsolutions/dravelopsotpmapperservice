package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.exception.NoExternalResultFoundException;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerApiApiTokenConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.OpenTripPlannerApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Point;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Import(OpenTripPlannerApiApiTokenConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenTripPlannerApiServiceIT {

    @Autowired
    private OpenTripPlannerApiService classUnderTest;

    @Autowired
    private ApiToken.ApiTokenBuilder openTripPlannerApiAndCallTokenIT;

    @Test
    void test_getJourneysBy_with_incorrect_apiToken_returns_noExternalResultFoundException() {
        openTripPlannerApiAndCallTokenIT.setArrival("Berlin Mitte");
        openTripPlannerApiAndCallTokenIT.setArrivalCoordinate(new Point(13.409600d, 52.509439d));

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(openTripPlannerApiAndCallTokenIT.build());

        StepVerifier.create(result)
                .assertNext(journey -> {
                    assertThat(journey.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(journey.getThrowable()).isInstanceOf(NoExternalResultFoundException.class);
                    assertThat(journey.getCalledObject()).isNull();
                })
                .verifyComplete();
    }

}
