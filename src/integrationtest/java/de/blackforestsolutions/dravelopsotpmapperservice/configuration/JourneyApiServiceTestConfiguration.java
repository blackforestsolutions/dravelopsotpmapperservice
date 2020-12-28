package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Point;

import java.util.Locale;

@TestConfiguration
@Import(ZonedDateTimeConfiguration.class)
public class JourneyApiServiceTestConfiguration {

    @Value("${test.apitokens[0].language}")
    private Locale language;
    @Value("${test.apitokens[0].departureCoordinateLongitude}")
    private Double departureCoordinateLongitude;
    @Value("${test.apitokens[0].departureCoordinateLatitude}")
    private Double departureCoordinateLatitude;
    @Value("${test.apitokens[0].arrivalCoordinateLongitude}")
    private Double arrivalCoordinateLongitude;
    @Value("${test.apitokens[0].arrivalCoordinateLatitude}")
    private Double arrivalCoordinateLatitude;

    @Bean
    @ConfigurationProperties(prefix = "test.apitokens[0]")
    public ApiToken.ApiTokenBuilder otpMapperApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setLanguage(language)
                .setDepartureCoordinate(new Point(departureCoordinateLongitude, departureCoordinateLatitude))
                .setArrivalCoordinate(new Point(arrivalCoordinateLongitude, arrivalCoordinateLatitude));
    }

}