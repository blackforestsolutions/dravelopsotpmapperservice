package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.PeliasTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.PeliasApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Import(PeliasTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeliasApiServiceIT {

    @Autowired
    private PeliasApiService classUnderTest;

    @Autowired
    private ApiToken peliasReverseApiToken;

    @Autowired
    private Point peliasPoint;

    @Test
    void test_extractTravelPointNameFrom_returns_correct_travelPoint() {
        Mono<CallStatus<String>> result = classUnderTest.extractTravelPointNameFrom(peliasReverseApiToken, peliasPoint);

        StepVerifier.create(result)
                .assertNext(travelPointName -> {
                    assertThat(travelPointName.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPointName.getThrowable()).isNull();
                    assertThat(travelPointName.getCalledObject()).isNotEmpty();
                    assertThat(travelPointName.getCalledObject()).isInstanceOf(String.class);
                })
                .verifyComplete();
    }
}
