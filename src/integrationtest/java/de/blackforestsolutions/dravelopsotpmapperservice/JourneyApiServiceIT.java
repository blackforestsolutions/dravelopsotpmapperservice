package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.ApiServiceTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsotpmapperservice.testutil.TestAssertions.getOtpMapperServiceLegAssertions;

@Import(ApiServiceTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JourneyApiServiceIT {

    @Autowired
    private JourneyApiService classUnderTest;

    @Autowired
    private ApiToken journeyOtpMapperApiToken;

    @Test
    void test_retrieveJourneysFromApiService_with_correct_apiToken_returns_correct_leg_properties() {
        ApiToken testData = new ApiToken(journeyOtpMapperApiToken);

        Flux<Journey> result = classUnderTest.retrieveJourneysFromApiService(testData);

        StepVerifier.create(result)
                .assertNext(getOtpMapperServiceLegAssertions())
                .thenConsumeWhile(journey -> true, getOtpMapperServiceLegAssertions())
                .verifyComplete();
    }

    @Test
    void test_retrieveJourneysFromApiService_with_incorrect_apiToken_returns_zero_results() {
        ApiToken testData = new ApiToken(journeyOtpMapperApiToken);
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());
        testData.setDepartureCoordinate(new Point.PointBuilder(1.0d, 1.0d).build());

        Flux<Journey> result = classUnderTest.retrieveJourneysFromApiService(testData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}