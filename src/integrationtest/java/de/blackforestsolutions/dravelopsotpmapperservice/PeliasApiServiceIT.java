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

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getPeliasApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.PointObjectMother.getSickAgPoint;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeliasApiServiceIT {

    @Autowired
    private PeliasApiService classUnderTest;

    @Test
    void test_extractTravelPointNameFrom_returns_correct_travelPoint() {
        ApiToken testData = getPeliasApiToken();
        Point testPoint = getSickAgPoint();

        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(testData, testPoint);

        StepVerifier.create(result)
                .assertNext(travelPointName -> {
                    assertThat(travelPointName.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPointName.getThrowable()).isNull();
                    assertThat(travelPointName.getCalledObject()).isEqualTo("SICK AG");
                })
                .verifyComplete();
    }
}
