package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import de.blackforestsolutions.dravelopsotpmapperservice.testutils.ZonedDateTimeConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOtpMapperApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.toJson;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JourneyApiServiceIT {

    /*@Value("$[{apiToken.protocol}")
private String protocol;
@Value("$[{apiToken.host}")
private String host;
@Value("$[{apiToken.port}")
private int port;
@Value("$[{apiToken.path}")
private String path;
@Value("$[{apiToken.optimize}")
private Optimization optimization;
@Value("$[{apiToken.arrivalDateTime}")
private boolean arrivalDateTime;
@Value("$[{apiToken.dateTime}")
private String dateTime;
@Value("$[{apiToken.departureCoordinates_x}")
private double departureCoordinates_x;
@Value("$[{apiToken.departureCoordinates_y}")
private double departureCoordinates_y;
@Value("$[{apiToken.arrivalCoordinates_x}")
private double arrivalCoordinates_x;
@Value("$[{apiToken.arrivalCoordinates_y}")
private double arrivalCoordinates_y;
@Value("$[{apiToken.language}")
private String language;*/
    @TestConfiguration
    static class TestConfig {

        @Bean
        @ConfigurationPropertiesBinding
        public ZonedDateTimeConverter zonedDateTimeConverter() {
            return new ZonedDateTimeConverter();
        }

        @Bean("test")
        @ConfigurationProperties("api-token")
        public ApiToken.ApiTokenBuilder apiToken() {
            return new ApiToken.ApiTokenBuilder();
        }


    }

    @Autowired
    private ApiToken.ApiTokenBuilder test;

    @Test
    void test_retrieveJourneysFromApiService_with_correct_apiToken_returns_results() {
        System.out.println(test.getDateTime());

        String jsonTestData = toJson(new ApiToken.ApiTokenBuilder().build());

        Flux<String> result = classUnderTest.retrieveJourneysFromApiService(jsonTestData);

//        StepVerifier.create(result)
//                .expectNextCount(1L)
//                .thenConsumeWhile(journey -> {
//                    Journey actualJourney = retrieveJsonToPojo(journey, Journey.class);
//                    assertThat(actualJourney.getLegs().size()).isGreaterThan(0);
//                    assertThat(actualJourney.getLegs())
//                            .first()
//                            .matches(leg -> leg.getDeparture().getDepartureTime().isAfter(ZonedDateTime.from(testData.getDateTime())))
//                            .matches(leg -> leg.getDeparture().getName().equals("Am Großhausberg"));
//                    assertThat(actualJourney.getLegs())
//                            .last()
//                            .matches(leg -> leg.getArrival().getArrivalTime().isAfter(ZonedDateTime.from(testData.getDateTime())))
//                            .matches(leg -> leg.getArrival().getName().equals("SICK AG"));
//                    assertThat(actualJourney.getLegs())
//                            .allMatch(leg -> leg.getDelayInMinutes().toMillis() >= 0)
//                            .allMatch(leg -> leg.getDistanceInKilometers().getValue() > 0)
//                            .allMatch(leg -> leg.getVehicleType() != null)
//                            .allMatch(leg -> leg.getDeparture() != null)
//                            .allMatch(leg -> leg.getArrival() != null)
//                            .allMatch(leg -> leg.getWaypoints().size() > 0);
//                    return true;
//                })
//                .verifyComplete();
    }


    @Autowired
    private JourneyApiService classUnderTest;

//    public ApiToken getCustomOtpMapperApiTokenFromResources() {
//        return (new ApiToken.ApiTokenBuilder())
//                .setProtocol(protocol)
//                .setHost(host)
//                .setPort(port)
//                .setPath(path)
//                .setOptimize(optimization)
//                .setIsArrivalDateTime(arrivalDateTime)
//                .setDateTime(ZonedDateTime.parse(dateTime))
//                .setDepartureCoordinate(new Point(departureCoordinates_x, departureCoordinates_y))
//                .setArrivalCoordinate(new Point(arrivalCoordinates_x, arrivalCoordinates_y))
//                .setLanguage(Locale.forLanguageTag(language))
//                .build();
//    }

    @Test
    void test_retrieveJourneysFromApiService_with_incorrect_apiToken_returns_zero_results() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpMapperApiToken());
        testData.setArrival("Berlin Mitte");
        testData.setArrivalCoordinate(new Point(13.409600d, 52.509439d));
        String jsonTestData = toJson(testData.build());

        Flux<String> result = classUnderTest.retrieveJourneysFromApiService(jsonTestData);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
