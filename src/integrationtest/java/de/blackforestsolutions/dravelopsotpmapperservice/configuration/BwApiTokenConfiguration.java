package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.Optimization;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;

import java.time.ZonedDateTime;
import java.util.Locale;

@TestConfiguration
public class BwApiTokenConfiguration {

    @Value("${protocol}")
    private String protocol;
    @Value("${host}")
    private String host;
    @Value("${port}")
    private int port;
    @Value("${path}")
    private String path;
    @Value("${optimize}")
    private Optimization optimize;
    @Value("${isArrivalDateTime}")
    private boolean isArrivalDateTime;
    @Value("${OtpMapperApiToken.dateTime}")
    private String dateTime;
    @Value("${OtpMapperApiToken.departureCoordinate_x}")
    private Double departureCoordinate_x;
    @Value("${OtpMapperApiToken.departureCoordinate_y}")
    private Double departureCoordinate_y;
    @Value("${OtpMapperApiToken.arrivalCoordinate_x}")
    private Double arrivalCoordinate_x;
    @Value("${OtpMapperApiToken.arrivalCoordinate_y}")
    private Double arrivalCoordinate_y;
    @Value("${language}")
    private Locale language;

    @Bean("OtpMapperApiTokenConfiguration")
    public ApiToken setOtpMapperApiTokenConfiguration() {
        System.out.println(port);
        return (new ApiToken.ApiTokenBuilder())
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setPath(path)
                .setOptimize(optimize)
                .setIsArrivalDateTime(isArrivalDateTime)
                .setDateTime(ZonedDateTime.parse(dateTime))
                .setDepartureCoordinate(new Point(departureCoordinate_x, departureCoordinate_y))
                .setArrivalCoordinate(new Point(arrivalCoordinate_x, arrivalCoordinate_y))
                .setLanguage(language)
                .build();
    }
}