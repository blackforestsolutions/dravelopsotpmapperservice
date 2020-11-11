package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.PeliasTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;
import static org.assertj.core.api.Assertions.assertThat;

@Import(PeliasTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeliasCallServiceIT {

    @Autowired
    private PeliasHttpCallBuilderService httpCallBuilderService;

    @Autowired
    private CallService callService;

    @Autowired
    private ApiToken.ApiTokenBuilder peliasReverseApiToken;

    @Autowired
    private Point peliasPoint;

    @Test
    void test_travelPointName() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(peliasReverseApiToken);
        testData.setPath(httpCallBuilderService.buildPeliasTravelPointNamePathWith(peliasReverseApiToken.build(), peliasPoint));

        Mono<PeliasTravelPointResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, PeliasTravelPointResponse.class);

        StepVerifier.create(result)
                .assertNext(peliasTravelPointResponse -> assertThat(peliasTravelPointResponse.getFeatures().size()).isGreaterThan(0))
                .verifyComplete();
    }
}
