package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsdatamodel.Price;
import de.blackforestsolutions.dravelopsdatamodel.PriceType;

import java.util.Currency;

public class PriceObjectMother {

    public static Price getFurtwangenToWaldkirchPrice() {
        return new Price.PriceBuilder()
                .setPriceType(PriceType.REGULAR)
                .setCurrencyCode(Currency.getInstance("EUR"))
                .setSmallestCurrencyValue(200L)
                .build();
    }
}
