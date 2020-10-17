package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Locale;

@SpringBootApplication
public class PeliasConfiguration {

    @Value("${pelias.protocol}")
    private String protocol;
    @Value("${pelias.host}")
    private String host;
    @Value("${pelias.port}")
    private int port;
    @Value("${pelias.apiVersion}")
    private String apiVersion;
    @Value("${pelias.maxResults}")
    private int maxResults;
    @Value("${language}")
    private String language;
    @Value("${pelias.departurePlaceholder}")
    private String departurePlaceholder;
    @Value("${pelias.arrivalPlaceholder}")
    private String arrivalPlaceholder;

    @Bean(name = "peliasApiToken")
    public ApiToken apiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setApiVersion(apiVersion)
                .setMaxResults(maxResults)
                .setLanguage(Locale.forLanguageTag(language))
                .setDeparture(departurePlaceholder)
                .setArrival(arrivalPlaceholder)
                .build();

    }

}
