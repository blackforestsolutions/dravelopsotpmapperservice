package de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice;

import de.blackforestsolutions.dravelopsdatamodel.Leg;
import de.blackforestsolutions.dravelopsdatamodel.Price;
import de.blackforestsolutions.dravelopsdatamodel.*;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.*;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    private final UuidService uuidService;
    private final PolylineDecodingService polylineDecodingService;
    private final ZonedDateTimeService zonedDateTimeService;
    private final CoordinateFormatterService coordinateFormatterService;
    private final DistanceFormatterService distanceFormatterService;

    @Autowired
    public OpenTripPlannerMapperServiceImpl(UuidService uuidService, PolylineDecodingService polylineDecodingService, ZonedDateTimeService zonedDateTimeService, CoordinateFormatterService coordinateFormatterService, DistanceFormatterService distanceFormatterService) {
        this.uuidService = uuidService;
        this.polylineDecodingService = polylineDecodingService;
        this.zonedDateTimeService = zonedDateTimeService;
        this.coordinateFormatterService = coordinateFormatterService;
        this.distanceFormatterService = distanceFormatterService;
    }

    @Override
    public Flux<CallStatus<Journey>> extractJourneysFrom(OpenTripPlannerJourneyResponse response, String departure, String arrival) {
        return Mono.just(response)
                .map(OpenTripPlannerJourneyResponse::getPlan)
                .flatMapMany(plan -> Flux.fromIterable(plan.getItineraries()))
                .map(itinerary -> extractJourneyFrom(itinerary, departure, arrival))
                .onErrorResume(e -> Mono.just(new CallStatus<>(null, Status.FAILED, e)));
    }

    private CallStatus<Journey> extractJourneyFrom(Itinerary itinerary, String departure, String arrival) {
        try {
            return new CallStatus<>(
                    new Journey.JourneyBuilder(uuidService.createUUID())
                            .setLegs(extractLegsFrom(itinerary.getLegs(), departure, arrival))
                            .setPrices(extractPricesFrom(itinerary))
                            .build(),
                    Status.SUCCESS,
                    null
            );
        } catch (Exception e) {
            return new CallStatus<>(null, Status.FAILED, e);
        }
    }

    private LinkedHashMap<UUID, Leg> extractLegsFrom(List<de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.Leg> openTripPlannerLegs, String departure, String arrival) throws MalformedURLException {
        LinkedHashMap<UUID, Leg> legs = new LinkedHashMap<>();
        for (de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.Leg openTripPlannerLeg : openTripPlannerLegs) {
            Leg leg = extractLegFrom(openTripPlannerLeg, departure, arrival);
            legs.put(leg.getId(), leg);
        }
        return legs;
    }

    private Leg extractLegFrom(de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.Leg openTripPlannerLeg, String departure, String arrival) throws MalformedURLException {
        ZonedDateTime departureTime = zonedDateTimeService.convertEpochMillisecondsToDate(openTripPlannerLeg.getStartTime());
        ZonedDateTime arrivalTime = zonedDateTimeService.convertEpochMillisecondsToDate(openTripPlannerLeg.getEndTime());
        return new Leg.LegBuilder(uuidService.createUUID())
                .setDeparture(extractTravelPointFrom(openTripPlannerLeg.getFrom(), departure))
                .setArrival(extractTravelPointFrom(openTripPlannerLeg.getTo(), arrival))
                .setDuration(Duration.between(departureTime, arrivalTime))
                .setDelay(extractDelayFrom(openTripPlannerLeg))
                .setDistance(distanceFormatterService.convertToKilometers(openTripPlannerLeg.getDistance()))
                .setVehicleType(VehicleType.valueOf(openTripPlannerLeg.getMode()))
                .setTrack(polylineDecodingService.decode(openTripPlannerLeg.getLegGeometry().getPoints()))
                .setTravelProvider(extractTravelProviderFrom(openTripPlannerLeg))
                .setVehicleNumber(Optional.ofNullable(openTripPlannerLeg.getRouteShortName()).orElse(""))
                .setVehicleName(Optional.ofNullable(openTripPlannerLeg.getRouteLongName()).orElse(""))
                .setIntermediateStops(extractIntermediateStopsFrom(openTripPlannerLeg.getIntermediateStops()))
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
                .setCoordinates(new Point(coordinateFormatterService.convertToPointWithFixedDecimalPlaces(stop.getLon(), stop.getLat())))
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

    private LinkedHashMap<PriceType, Price> extractPricesFrom(Itinerary itinerary) {
        return Optional.ofNullable(itinerary.getFare())
                .map(Fare::getFare)
                .map(fares -> fares.entrySet().stream()
                        .map(this::extractPriceFrom)
                        .collect(Collectors.toMap(Price::getPriceType, price -> price, (prev, next) -> next, LinkedHashMap::new))
                )
                .orElseGet(LinkedHashMap::new);
    }

    private Price extractPriceFrom(Map.Entry<Fare.FareType, Money> fare) {
        return new Price.PriceBuilder()
                .setPriceType(PriceType.valueOf(fare.getKey().toString().toUpperCase(Locale.GERMANY)))
                .setSmallestCurrencyValue(fare.getValue().getCents())
                .setCurrency(Currency.getInstance(fare.getValue().getCurrency().getCurrencyCode()))
                .build();
    }


}
