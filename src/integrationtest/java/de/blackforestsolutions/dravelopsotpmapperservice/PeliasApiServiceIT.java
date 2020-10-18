package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.PeliasApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.PointObjectMother.getSickAgPoint;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeliasApiServiceIT {

    @Autowired
    private ApiToken peliasApiToken;

    @Autowired
    private PeliasApiService classUnderTest;

    @Test
    void test_extractTravelPointNameFrom_returnscorrectTravelPoint() {
        Point testPoint = getSickAgPoint();

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(peliasApiToken, testPoint);

        StepVerifier.create(result)
                .assertNext(travelPointName -> {
                    assertThat(travelPointName.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPointName.getThrowable()).isNull();
                    assertThat(travelPointName.getCalledObject()).isEqualTo("SICK AG");
                })
                .verifyComplete();
    }
}
