package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PeliasConfiguration {

    @Value("${pelias.protocol}")
    private String protocol;
    @Value("${pelias.host}")
    private String host;
    @Value("${pelias.port}")
    private int port;
    @Value("${pelias.apiVersion}")
    private String apiVersion;
    @Value("${graphql.playground.tabs[0].maxResults}")
    private int maxResults;
    @Value("${graphql.playground.tabs[0].departurePlaceholder}")
    private String departurePlaceholder;
    @Value("${graphql.playground.tabs[0].arrivalPlaceholder}")
    private String arrivalPlaceholder;
    @Value("${graphql.playground.tabs[3].layers}")
    private List<String> layers;

    @Bean(name = "peliasApiToken")
    public ApiToken apiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setApiVersion(apiVersion)
                .setMaxResults(maxResults)
                .setDeparture(departurePlaceholder)
                .setArrival(arrivalPlaceholder)
                .setLayers(layers)
                .build();
    }

}
