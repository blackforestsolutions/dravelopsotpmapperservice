package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.ApiServiceTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.TravelPointApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsotpmapperservice.configuration.CoordinateConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

@Import(ApiServiceTestConfiguration.class)
@SpringBootTest
class TravelPointApiServiceIT {

    @Autowired
    private TravelPointApiService classUnderTest;

    @Autowired
    private ApiToken.ApiTokenBuilder otpMapperApiToken;

    @Test
    void test_retrieveNearestStationsFromApiService_with_correct_apiToken_returns_results() {
        ApiToken testData = otpMapperApiToken.build();

        Flux<TravelPoint> result = classUnderTest.retrieveNearestStationsFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(1L)
                .thenConsumeWhile(travelPoint -> {
                    assertThat(travelPoint).isNotNull();
                    assertThat(travelPoint.getName()).isNotEmpty();
                    assertThat(travelPoint.getPoint()).isNotNull();
                    assertThat(travelPoint.getPoint().getX()).isGreaterThanOrEqualTo(MIN_WGS_84_LONGITUDE);
                    assertThat(travelPoint.getPoint().getX()).isLessThanOrEqualTo(MAX_WGS_84_LONGITUDE);
                    assertThat(travelPoint.getPoint().getY()).isGreaterThanOrEqualTo(MIN_WGS_84_LATITUDE);
                    assertThat(travelPoint.getPoint().getY()).isLessThanOrEqualTo(MAX_WGS_84_LATITUDE);
                    assertThat(travelPoint.getDistanceInKilometers()).isNotNull();
                    assertThat(travelPoint.getDistanceInKilometers().getValue()).isGreaterThanOrEqualTo(MIN_DISTANCE_IN_KILOMETERS_TO_POINT);
                    assertThat(travelPoint.getDepartureTime()).isNull();
                    assertThat(travelPoint.getArrivalTime()).isNull();
                    assertThat(travelPoint.getPlatform()).isEmpty();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void test_retrieveNearestStationsFromApiService_with_incorrect_apiToken_returns_zero_results() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(otpMapperApiToken);
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, -90.0d).build());
        testData.setRadiusInKilometers(new Distance(1.0d, Metrics.KILOMETERS));

        Flux<TravelPoint> result = classUnderTest.retrieveNearestStationsFromApiService(testData.build());

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
