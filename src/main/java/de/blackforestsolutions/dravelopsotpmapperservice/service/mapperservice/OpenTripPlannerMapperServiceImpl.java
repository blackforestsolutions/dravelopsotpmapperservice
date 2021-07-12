package de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice;

import de.blackforestsolutions.dravelopsdatamodel.*;
import de.blackforestsolutions.dravelopsdatamodel.Leg;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.*;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.station.OpenTripPlannerStationResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.GeocodingService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.UuidService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.ZonedDateTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OpenTripPlannerMapperServiceImpl implements OpenTripPlannerMapperService {

    private static final String ORIGIN_PLACEHOLDER = "Origin";
    private static final String DESTINATION_PLACEHOLDER = "Destination";

    private final GeocodingService geocodingService;
    private final ZonedDateTimeService zonedDateTimeService;
    private final UuidService uuidService;

    @Autowired
    public OpenTripPlannerMapperServiceImpl(GeocodingService geocodingService, ZonedDateTimeService zonedDateTimeService, UuidService uuidService) {
        this.geocodingService = geocodingService;
        this.zonedDateTimeService = zonedDateTimeService;
        this.uuidService = uuidService;
    }

    @Override
    public Flux<CallStatus<Journey>> extractJourneysFrom(OpenTripPlannerJourneyResponse response, String departure, String arrival) {
        return Mono.just(response)
                .map(OpenTripPlannerJourneyResponse::getPlan)
                .flatMapMany(plan -> Flux.fromIterable(plan.getItineraries()))
                .map(itinerary -> extractJourneyCallStatusFrom(itinerary, departure, arrival, response.getRequestParameters().getLocale()))
                .onErrorResume(e -> Mono.just(new CallStatus<>(null, Status.FAILED, e)));
    }

    @Override
    public Flux<CallStatus<TravelPoint>> extractNearestStationFrom(List<OpenTripPlannerStationResponse> response) {
        return Flux.fromIterable(response)
                .map(this::extractStationCallStatusFrom)
                .onErrorResume(e -> Mono.just(new CallStatus<>(null, Status.FAILED, e)));
    }

    private CallStatus<Journey> extractJourneyCallStatusFrom(Itinerary itinerary, String departure, String arrival, String language) {
        try {
            return new CallStatus<>(extractJourneyFrom(itinerary, departure, arrival, language), Status.SUCCESS, null);
        } catch (Exception e) {
            return new CallStatus<>(null, Status.FAILED, e);
        }
    }

    private Journey extractJourneyFrom(Itinerary itinerary, String departure, String arrival, String language) throws IOException {
        return new Journey.JourneyBuilder(uuidService.createUUID())
                .setLanguage(new Locale(language))
                .setLegs(extractLegsFrom(itinerary.getLegs(), departure, arrival))
                .setPrices(extractPricesFrom(itinerary))
                .build();
    }

    private LinkedList<Leg> extractLegsFrom(List<de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.Leg> openTripPlannerLegs, String departure, String arrival) throws MalformedURLException {
        LinkedList<Leg> legs = new LinkedList<>();
        for (de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.Leg openTripPlannerLeg : openTripPlannerLegs) {
            Leg leg = extractLegFrom(openTripPlannerLeg, departure, arrival);
            legs.add(leg);
        }
        return legs;
    }

    private Leg extractLegFrom(de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.Leg openTripPlannerLeg, String departure, String arrival) throws MalformedURLException {
        return new Leg.LegBuilder()
                .setDeparture(extractTravelPointFrom(openTripPlannerLeg.getFrom(), departure))
                .setArrival(extractTravelPointFrom(openTripPlannerLeg.getTo(), arrival))
                .setDelayInMinutes(extractDelayFrom(openTripPlannerLeg))
                .setDistanceInKilometers(geocodingService.extractKilometersFrom(openTripPlannerLeg.getDistance()))
                .setVehicleType(VehicleType.valueOf(openTripPlannerLeg.getMode()))
                .setWaypoints(geocodingService.decodePolylineFrom(openTripPlannerLeg.getLegGeometry().getPoints()))
                .setTravelProvider(extractTravelProviderFrom(openTripPlannerLeg))
                .setVehicleNumber(Optional.ofNullable(openTripPlannerLeg.getRouteShortName()).orElse(""))
                .setVehicleName(Optional.ofNullable(openTripPlannerLeg.getRouteLongName()).orElse(""))
                .setIntermediateStops(Optional.ofNullable(openTripPlannerLeg.getIntermediateStops())
                        .map(this::extractIntermediateStopsFrom)
                        .orElse(new LinkedList<>())
                )
                .build();
    }

    private LinkedList<TravelPoint> extractIntermediateStopsFrom(List<Stop> intermediateStops) {
        return intermediateStops.stream()
                .map(intermediateStop -> extractTravelPointFrom(intermediateStop, ""))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private TravelPoint extractTravelPointFrom(Stop stop, String optionalStopName) {
        return new TravelPoint.TravelPointBuilder()
                .setName(extractStopNameFrom(stop, optionalStopName))
                .setPoint(geocodingService.extractCoordinateWithFixedDecimalPlacesFrom(stop.getLon(), stop.getLat()))
                .setDepartureTime(Optional.ofNullable(stop.getDeparture()).map(this::extractDateTime).orElse(null))
                .setArrivalTime(Optional.ofNullable(stop.getArrival()).map(this::extractDateTime).orElse(null))
                .setPlatform(Optional.ofNullable(stop.getPlatformCode()).orElse(""))
                .build();
    }

    private String extractStopNameFrom(Stop stop, String optionalStopName) {
        if (stop.getName().equals(ORIGIN_PLACEHOLDER) || stop.getName().equals(DESTINATION_PLACEHOLDER)) {
            return optionalStopName;
        }
        return stop.getName();
    }

    private ZonedDateTime extractDateTime(long epochMilliseconds) {
        return zonedDateTimeService.convertEpochMillisecondsToDate(epochMilliseconds);
    }

    private Duration extractDelayFrom(de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.Leg openTripPlannerLeg) {
        Optional<Long> arrivalDelay = Optional.ofNullable(openTripPlannerLeg.getArrivalDelay());
        Optional<Long> departureDelay = Optional.ofNullable(openTripPlannerLeg.getDepartureDelay());

        if (arrivalDelay.isPresent() && arrivalDelay.get() > 0) {
            return Duration.ofMinutes(TimeUnit.MILLISECONDS.toMinutes(arrivalDelay.get()));
        }
        if (departureDelay.isPresent() && departureDelay.get() > 0) {
            return Duration.ofMinutes(TimeUnit.MILLISECONDS.toMinutes(departureDelay.get()));
        }
        return Duration.ZERO;
    }

    private TravelProvider extractTravelProviderFrom(de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.Leg openTripPlannerLeg) throws MalformedURLException {
        Optional<String> optionalAgencyName = Optional.ofNullable(openTripPlannerLeg.getAgencyName());
        Optional<String> optionalAgencyUrl = Optional.ofNullable(openTripPlannerLeg.getAgencyUrl());

        if (optionalAgencyName.isPresent() && optionalAgencyUrl.isPresent()) {
            return new TravelProvider.TravelProviderBuilder()
                    .setName(optionalAgencyName.get())
                    .setUrl(new URL(optionalAgencyUrl.get()))
                    .build();
        }
        return null;
    }

    private LinkedList<Price> extractPricesFrom(Itinerary itinerary) {
        return Optional.ofNullable(itinerary.getFare())
                .map(Fare::getFare)
                .map(fares -> fares.entrySet().stream()
                        .map(this::extractPriceFrom)
                        .collect(Collectors.toMap(Price::getPriceType, price -> price, (prev, next) -> next, LinkedHashMap::new))
                )
                .map(priceMap -> new LinkedList<>(priceMap.values()))
                .orElseGet(LinkedList::new);
    }

    private Price extractPriceFrom(Map.Entry<Fare.FareType, Money> fare) {
        return new Price.PriceBuilder()
                .setPriceType(PriceType.valueOf(fare.getKey().toString().toUpperCase(Locale.GERMANY)))
                .setSmallestCurrencyValue(fare.getValue().getCents())
                .setCurrencyCode(Currency.getInstance(fare.getValue().getCurrency().getCurrencyCode()))
                .build();
    }

    private CallStatus<TravelPoint> extractStationCallStatusFrom(OpenTripPlannerStationResponse stationResponse) {
        try {
            return new CallStatus<>(extractStationFrom(stationResponse), Status.SUCCESS, null);
        } catch (Exception e) {
            return new CallStatus<>(null, Status.FAILED, e);
        }
    }

    private TravelPoint extractStationFrom(OpenTripPlannerStationResponse stationResponse) {
        return new TravelPoint.TravelPointBuilder()
                .setName(stationResponse.getName())
                .setPoint(new Point.PointBuilder(stationResponse.getLon(), stationResponse.getLat()).build())
                .setDistanceInKilometers(geocodingService.extractKilometersFrom(stationResponse.getDist()))
                .build();
    }


}
