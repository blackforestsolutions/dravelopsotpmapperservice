package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerApiApiTokenConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.OpenTripPlannerHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;
import static org.assertj.core.api.Assertions.assertThat;

@Import(OpenTripPlannerApiApiTokenConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenTripPlannerCallServiceIT {

    @Autowired
    private OpenTripPlannerHttpCallBuilderService httpCallBuilderService;

    @Autowired
    private CallService callService;

    @Autowired
    private ApiToken.ApiTokenBuilder openTripPlannerApiAndCallTokenIT;

    @Test
    void test_journey() {
        openTripPlannerApiAndCallTokenIT.setPath(httpCallBuilderService.buildOpenTripPlannerJourneyPathWith(openTripPlannerApiAndCallTokenIT.build()));

        Mono<ResponseEntity<String>> result = callService.get(buildUrlWith(openTripPlannerApiAndCallTokenIT.build()).toString(), HttpHeaders.EMPTY);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotEmpty();
                    assertThat(retrieveJsonToPojo(response.getBody(), OpenTripPlannerJourneyResponse.class).getPlan().getItineraries().size()).isGreaterThan(0);
                })
                .verifyComplete();
    }
}
