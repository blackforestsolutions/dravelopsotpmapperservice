package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.*;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.OpenTripPlannerApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Import(OpenTripPlannerTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenTripPlannerApiServiceIT {

    @Autowired
    private OpenTripPlannerApiService classUnderTest;

    @Autowired
    private ApiToken.ApiTokenBuilder openTripPlannerApiTokenIT;

    @Test
    void test_getJourneysBy_with_correct_apiToken_returns_only_successful_callStatus() {
        ApiToken testData = openTripPlannerApiTokenIT.build();

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData);

        StepVerifier.create(result)
                .assertNext(journey -> {
                    assertThat(journey.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(journey.getThrowable()).isNull();
                    assertThat(journey.getCalledObject()).isInstanceOf(Journey.class);
                })
                .thenConsumeWhile(journey -> true, journey -> {
                    assertThat(journey.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(journey.getThrowable()).isNull();
                    assertThat(journey.getCalledObject()).isInstanceOf(Journey.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getJourneysBy_with_incorrect_apiToken_returns_no_result() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(openTripPlannerApiTokenIT);
        testData.setDepartureCoordinate(new Point.PointBuilder(1.0d, 1.0d).build());
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());

        Flux<CallStatus<Journey>> result = classUnderTest.getJourneysBy(testData.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getNearestStationsBy_with_correct_apiToken_returns_only_successful_callStatus() {
        ApiToken testData = openTripPlannerApiTokenIT.build();

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getNearestStationsBy(testData);

        StepVerifier.create(result)
                .assertNext(travelPoint -> {
                    assertThat(travelPoint.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(travelPoint.getThrowable()).isNull();
                    assertThat(travelPoint.getCalledObject()).isInstanceOf(TravelPoint.class);
                })
                .thenConsumeWhile(journey -> true, journey -> {
                    assertThat(journey.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(journey.getThrowable()).isNull();
                    assertThat(journey.getCalledObject()).isInstanceOf(TravelPoint.class);
                })
                .verifyComplete();
    }

    @Test
    void test_getNearestStationsBy_with_incorrect_apiToken_returns_no_result() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(openTripPlannerApiTokenIT);
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, -90.0d).build());
        testData.setRadiusInKilometers(new Distance(1.0d, Metrics.KILOMETERS));

        Flux<CallStatus<TravelPoint>> result = classUnderTest.getNearestStationsBy(testData.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

}
