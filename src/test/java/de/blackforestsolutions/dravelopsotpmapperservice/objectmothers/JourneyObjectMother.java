package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Leg;
import de.blackforestsolutions.dravelopsdatamodel.Price;

import java.util.LinkedList;
import java.util.UUID;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.LegObjectMother.*;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.PriceObjectMother.getFurtwangenToWaldkirchPrice;
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
                .build();
    }

    private static LinkedList<Leg> getFurtwangenToWaldkirchLegs() {
        LinkedList<Leg> legs = new LinkedList<>();
        legs.add(getGrosshausbergToFurtwangenIlbenstreetLeg());
        legs.add(getFurtwangenIlbenstreetToBleibachLeg());
        legs.add(getBleibachToWaldkirchKastelberghalleLeg());
        legs.add(getWaldkirchKastelberghalleToSickLeg());
        return legs;
    }

    private static LinkedList<Price> getFurtwangenToWaldkirchPrices() {
        LinkedList<Price> prices = new LinkedList<>();
        prices.add(getFurtwangenToWaldkirchPrice());
        return prices;
    }

    private static LinkedList<Leg> getMannheimHbfToLudwigsburgCenterLegs() {
        LinkedList<Leg> legs = new LinkedList<>();
        legs.add(getMannheimHbfToMannheimUniversityLeg());
        legs.add(getMannheimUniversityToMannheimBerlinerPlaceLeg());
        legs.add(getBerlinerPlaceToDestinationLeg());
        return legs;
    }
}
