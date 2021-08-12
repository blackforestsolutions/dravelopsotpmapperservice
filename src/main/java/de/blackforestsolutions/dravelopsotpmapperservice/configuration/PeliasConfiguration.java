package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Layer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;

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
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasVenue}")
    private Boolean hasVenueLayer;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasAddress}")
    private Boolean hasAddressLayer;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasStreet}")
    private Boolean hasStreetLayer;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasLocality}")
    private Boolean hasLocalityLayer;

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
        apiToken.setLayers(buildLayersMap());

        return apiToken;
    }

    private LinkedHashMap<Layer, Boolean> buildLayersMap() {
        LinkedHashMap<Layer, Boolean> layers = new LinkedHashMap<>();

        layers.put(Layer.HAS_VENUE, hasVenueLayer);
        layers.put(Layer.HAS_ADDRESS, hasAddressLayer);
        layers.put(Layer.HAS_STREET, hasStreetLayer);
        layers.put(Layer.HAS_LOCALITY, hasLocalityLayer);

        return layers;
    }
}
