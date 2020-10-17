package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.PointObjectMother.getSickAgPoint;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutils.TestUtils.retrieveJsonToPojo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeliasCallServiceIT {

    @Autowired
    private PeliasHttpCallBuilderService httpCallBuilderService;

    @Autowired
    private CallService callService;

    @Autowired
    private ApiToken peliasApiToken;

    @Test
    void test_travelPointName() {
        Point testPoint = getSickAgPoint();
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(peliasApiToken);
        testData.setPath(httpCallBuilderService.buildPeliasTravelPointNamePathWith(testData.build(), testPoint));

        Mono<ResponseEntity<String>> result = callService.get(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotEmpty();
                    assertThat(retrieveJsonToPojo(response.getBody(), PeliasTravelPointResponse.class).getFeatures().size()).isGreaterThan(0);
                })
                .verifyComplete();



    }
}