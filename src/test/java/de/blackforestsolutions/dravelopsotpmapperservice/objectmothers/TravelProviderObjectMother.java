package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsdatamodel.TravelProvider;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Slf4j
public class TravelProviderObjectMother {

    public static TravelProvider getSuedbadenTravelProvider() {
        return new TravelProvider.TravelProviderBuilder()
                .setName("Sonstige")
                .setUrl(getSuedbadenUrl())
                .build();
    }

    public static TravelProvider getRnvTravelProvider() {
        return new TravelProvider.TravelProviderBuilder()
                .setName("Rhein-Neckar-Verkehr GmbH (rnv)")
                .setUrl()
    }

    private static URL getSuedbadenUrl() {
        try {
            return new URL("https://www.suedbadenbus.de/suedbadenbus/view/index.shtml");
        } catch (Exception e) {
            log.error("URL from TravelProvider was not possble to parse due to: ", e);
            return null;
        }
    }
}
