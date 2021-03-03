package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PeliasHttpCallBuilderServiceImpl implements PeliasHttpCallBuilderService {

    private static final String REVERSE_PATH = "reverse";

    private static final String LATITUDE_PARAM = "point.lat";
    private static final String LONGITUDE_PARAM = "point.lon";
    private static final String SIZE_PARAM = "size";
    private static final String LANGUAGE_PARAM = "lang";
    private static final String LAYERS_PARAM = "layers";

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
                .concat(String.join(",", apiToken.getLayers()));
    }
}
