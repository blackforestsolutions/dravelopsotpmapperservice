package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@RefreshScope
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
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.departurePlaceholder}")
    private String departurePlaceholder;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.arrivalPlaceholder}")
    private String arrivalPlaceholder;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.maxResults}")
    private int maxResults;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers}")
    private String[] layers;

    @RefreshScope
    @Bean
    public ApiToken peliasApiToken() {
        ApiToken apiToken = new ApiToken();

        apiToken.setProtocol(protocol);
        apiToken.setHost(host);
        apiToken.setPort(port);
        apiToken.setApiVersion(apiVersion);
        apiToken.setMaxResults(maxResults);
        apiToken.setDeparture(departurePlaceholder);
        apiToken.setArrival(arrivalPlaceholder);
        apiToken.setLayers(Arrays.asList(layers));

        return apiToken;
    }

}
