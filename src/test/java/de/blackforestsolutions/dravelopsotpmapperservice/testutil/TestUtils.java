package de.blackforestsolutions.dravelopsotpmapperservice.testutil;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.GtfsApiTokenConfiguration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestUtils {
    /**
     * converts configToken to apiToken
     *
     * @param apiTokens from dravelopsdatamodel repo
     * @return GtfsApiTokenConfiguration
     */
    public static GtfsApiTokenConfiguration convertApiTokensToConfigToken(List<ApiToken> apiTokens) {
        GtfsApiTokenConfiguration gtfsApiTokenConfiguration = new GtfsApiTokenConfiguration();
        gtfsApiTokenConfiguration.setApitokens(apiTokens.stream()
                .map(apiToken -> Map.entry(apiToken.getGtfsProvider(), new Object()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        return gtfsApiTokenConfiguration;
    }
}
