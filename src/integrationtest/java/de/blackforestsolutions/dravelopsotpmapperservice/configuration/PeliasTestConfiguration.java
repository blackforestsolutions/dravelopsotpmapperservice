package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Layer;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.LinkedHashMap;
import java.util.Locale;

@TestConfiguration
public class PeliasTestConfiguration {

    @Value("${graphql.playground.tabs.JOURNEY_QUERY.departurePlaceholder}")
    private String departure;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.arrivalPlaceholder}")
    private String arrival;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.language}")
    private Locale language;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.longitude}")
    private Double longitude;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.latitude}")
    private Double latitude;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.maxResults}")
    private Integer maxResults;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasVenue}")
    private Boolean hasVenueLayer;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasAddress}")
    private Boolean hasAddressLayer;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasStreet}")
    private Boolean hasStreetLayer;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers.hasLocality}")
    private Boolean hasLocalityLayer;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken peliasReverseApiToken() {
        ApiToken apiToken = new ApiToken();
        apiToken.setDeparture(departure);
        apiToken.setArrival(arrival);
        apiToken.setLanguage(language);
        apiToken.setMaxResults(maxResults);
        apiToken.setLayers(buildLayersMap());
        return apiToken;
    }

    @Bean
    public Point peliasPoint() {
        return new Point.PointBuilder(longitude, latitude)
                .build();
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