package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Layer;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PeliasHttpCallBuilderServiceImpl implements PeliasHttpCallBuilderService {

    private static final int MAX_LAYERS_LENGTH = 0;
    private static final String REVERSE_PATH = "reverse";

    private static final String LATITUDE_PARAM = "point.lat";
    private static final String LONGITUDE_PARAM = "point.lon";
    private static final String SIZE_PARAM = "size";
    private static final String LANGUAGE_PARAM = "lang";
    private static final String LAYERS_PARAM = "layers";
    private static final String LAYERS_VENUE_PARAM_VALUE = "venue";
    private static final String LAYERS_ADDRESS_PARAM_VALUE = "address";
    private static final String LAYERS_STREET_PARAM_VALUE = "street";
    private static final String LAYERS_LOCALITY_PARAM_VALUE = "locality";

    @Override
    public String buildPeliasReversePathWith(ApiToken apiToken, Point coordinate) {
        Objects.requireNonNull(coordinate, "coordinate is not allowed be null");
        Objects.requireNonNull(apiToken.getLanguage(), "language is not allowed to be null");
        Objects.requireNonNull(apiToken.getMaxResults(), "maxResults is not allowed to be null");
        Objects.requireNonNull(apiToken.getApiVersion(), "apiVersion is not allowed to be null");
        Objects.requireNonNull(apiToken.getLayers(), "layers is not allowed to be null");
        return "/"
                .concat(apiToken.getApiVersion())
                .concat("/")
                .concat(REVERSE_PATH)
                .concat("?")
                .concat(LATITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(coordinate.getY()))
                .concat("&")
                .concat(LONGITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(coordinate.getX()))
                .concat("&")
                .concat(SIZE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getMaxResults()))
                .concat("&")
                .concat(LANGUAGE_PARAM)
                .concat("=")
                .concat(apiToken.getLanguage().toLanguageTag())
                .concat("&")
                .concat(LAYERS_PARAM)
                .concat("=")
                .concat(buildLayersParamsWith(apiToken.getLayers()));
    }

    private String buildLayersParamsWith(Map<Layer, Boolean> layers) {
        Objects.requireNonNull(layers.get(Layer.HAS_VENUE), "layers hasVenue is not allowed to be null");
        Objects.requireNonNull(layers.get(Layer.HAS_ADDRESS), "layers hasAddress is not allowed to be null");
        Objects.requireNonNull(layers.get(Layer.HAS_LOCALITY), "layers hasLocality is not allowed to be null");
        Objects.requireNonNull(layers.get(Layer.HAS_STREET), "layers hasStreet is not allowed to be null");

        return layers.entrySet()
                .stream()
                .map(this::getLayerParamBy)
                .filter(layer -> layer.length() != MAX_LAYERS_LENGTH)
                .collect(Collectors.joining(","));
    }

    private String getLayerParamBy(Map.Entry<Layer, Boolean> layer) {
        if (layer.getKey().equals(Layer.HAS_VENUE) && layer.getValue()) {
            return LAYERS_VENUE_PARAM_VALUE;
        }
        if (layer.getKey().equals(Layer.HAS_ADDRESS) && layer.getValue()) {
            return LAYERS_ADDRESS_PARAM_VALUE;
        }
        if (layer.getKey().equals(Layer.HAS_LOCALITY) && layer.getValue()) {
            return LAYERS_LOCALITY_PARAM_VALUE;
        }
        if (layer.getKey().equals(Layer.HAS_STREET) && layer.getValue()) {
            return LAYERS_STREET_PARAM_VALUE;
        }
        return "";
    }
}
