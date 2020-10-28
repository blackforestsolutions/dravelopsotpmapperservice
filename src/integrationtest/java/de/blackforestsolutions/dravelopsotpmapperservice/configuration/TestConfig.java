package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.testutils.ZonedDateTimeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;

@TestConfiguration
public class TestConfig {

    @Value("${test.apitokens.otpmapper.departureCoordinate.longitude}")
    private double otpMapperDepartureLongitude;
    @Value("${test.apitokens.otpmapper.departureCoordinate.latitude}")
    private double otpMapperDepartureLatitude;


    @Bean
    @ConfigurationPropertiesBinding
    public ZonedDateTimeConverter zonedDateTimeConverter() {
        return new ZonedDateTimeConverter();
    }

    @Bean
    @ConfigurationProperties("test.apitokens.otpmapper")
    public ApiToken.ApiTokenBuilder otpMapperApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setDepartureCoordinate(new Point(otpMapperDepartureLongitude, otpMapperDepartureLatitude));
    }


}
