package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.PeliasTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;
import static org.assertj.core.api.Assertions.assertThat;

@Import(PeliasTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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
        testData.setPath(httpCallBuilderService.buildPeliasReversePathWith(peliasReverseApiToken.build(), peliasPoint));

        Mono<PeliasTravelPointResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, PeliasTravelPointResponse.class);

        StepVerifier.create(result)
                .assertNext(peliasTravelPointResponse -> assertThat(peliasTravelPointResponse.getFeatures().size()).isGreaterThan(0))
                .verifyComplete();
    }

    @Test
    void test_travelPointName_with_wrong_path() {
        String testUrl = "wrongPath";
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(peliasReverseApiToken.build());
        testData.setPath(testUrl);

        Mono<OpenTripPlannerJourneyResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, OpenTripPlannerJourneyResponse.class);

        StepVerifier.create(result)
                .expectError(WebClientResponseException.class)
                .verify();
    }
}
