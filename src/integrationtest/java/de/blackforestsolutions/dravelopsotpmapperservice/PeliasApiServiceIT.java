package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.PeliasApiApiTokenConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.PeliasApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Point;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Import(PeliasApiApiTokenConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeliasApiServiceIT {

    @Autowired
    private PeliasApiService classUnderTest;

    @Autowired
    private ApiToken.ApiTokenBuilder peliasApiApiTokenIT;

    @Autowired
    private Point peliasPoint;

    @Test
    void test_extractTravelPointNameFrom_returns_correct_travelPoint() {
        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(peliasApiApiTokenIT.build(), peliasPoint);

        StepVerifier.create(result)
                .assertNext(travelPointName -> {
                    assertThat(travelPointName.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPointName.getThrowable()).isNull();
                    assertThat(travelPointName.getCalledObject()).isEqualTo("SICK AG");
                })
                .verifyComplete();
    }
}
