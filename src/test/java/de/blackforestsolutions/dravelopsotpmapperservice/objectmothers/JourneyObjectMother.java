package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Leg;
import de.blackforestsolutions.dravelopsdatamodel.Price;
import de.blackforestsolutions.dravelopsdatamodel.PriceType;

import java.util.LinkedHashMap;
import java.util.UUID;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.LegObjectMother.*;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.PriceObjectMother.getFurtwangenToWaldkirchPrice;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.PriceObjectMother.getMannheimHbfToLudwigsburgCenterPrice;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.UUIDObjectMother.TEST_UUID_1;

public class JourneyObjectMother {

    public static Journey getJourneyWithEmptyFields(UUID id) {
        return new Journey.JourneyBuilder(id)
                .build();
    }

    public static Journey getFurtwangenToWaldkirchJourney() {
        return new Journey.JourneyBuilder(TEST_UUID_1)
                .setLegs(getFurtwangenToWaldkirchLegs())
                .setPrices(getFurtwangenToWaldkirchPrices())
                .build();
    }

    public static Journey getMannheimHbfLudwigsburgCenterJourney() {
        return new Journey.JourneyBuilder(TEST_UUID_1)
                .setLegs(getMannheimHbfToLudwigsburgCenterLegs())
                .setPrices(getMannheimHbfToLudwigsburgCenterPrices())
                .build();
    }

    private static LinkedHashMap<UUID, Leg> getFurtwangenToWaldkirchLegs() {
        LinkedHashMap<UUID, Leg> legs = new LinkedHashMap<>();
        legs.put(getGroßhausbergToFurtwangenIlbenstreetLeg().getId(), getGroßhausbergToFurtwangenIlbenstreetLeg());
        legs.put(getFurtwangenIlbenstreetToBleibachLeg().getId(), getFurtwangenIlbenstreetToBleibachLeg());
        legs.put(getBleibachToWaldkirchKastelberghalleLeg().getId(), getBleibachToWaldkirchKastelberghalleLeg());
        legs.put(getWaldkirchKastelberghalleToSickLeg().getId(), getWaldkirchKastelberghalleToSickLeg());
        return legs;
    }

    private static LinkedHashMap<PriceType, Price> getFurtwangenToWaldkirchPrices() {
        LinkedHashMap<PriceType, Price> prices = new LinkedHashMap<>();
        prices.put(getFurtwangenToWaldkirchPrice().getPriceType(), getFurtwangenToWaldkirchPrice());
        return prices;
    }

    private static LinkedHashMap<UUID, Leg> getMannheimHbfToLudwigsburgCenterLegs() {
        LinkedHashMap<UUID, Leg> legs = new LinkedHashMap<>();
        legs.put(getMannheimHbfToMannheimUniversityLeg().getId(), getMannheimHbfToMannheimUniversityLeg());
        legs.put(getMannheimUniversityToMannheimBerlinerPlaceLeg().getId(), getMannheimUniversityToMannheimBerlinerPlaceLeg());
        legs.put(getBerlinerPlaceToDestinationLeg().getId(), getBerlinerPlaceToDestinationLeg());
        return legs;
    }

    private static LinkedHashMap<PriceType, Price> getMannheimHbfToLudwigsburgCenterPrices() {
        LinkedHashMap<PriceType, Price> prices = new LinkedHashMap<>();
        prices.put(getMannheimHbfToLudwigsburgCenterPrice().getPriceType(), getMannheimHbfToLudwigsburgCenterPrice());
        return prices;
    }
}
