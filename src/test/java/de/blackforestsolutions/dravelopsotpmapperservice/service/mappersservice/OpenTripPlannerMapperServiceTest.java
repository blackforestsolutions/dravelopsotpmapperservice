package de.blackforestsolutions.dravelopsotpmapperservice.service.mappersservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.station.OpenTripPlannerStationResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getFurtwangenToWaldkirchJourney;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getMannheimHbfLudwigsburgCenterJourney;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.WaypointsObjectMother.getExampleWaypoints;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.*;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutil.TestAssertions.getOtpApiNearestStationsAsserts;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OpenTripPlannerMapperServiceTest {

    private final GeocodingService geocodingService = spy(GeocodingServiceImpl.class);
    private final ZonedDateTimeService zonedDateTimeService = new ZonedDateTimeServiceImpl(ZoneId.of("Europe/Berlin"));

    private final OpenTripPlannerMapperService classUnderTest = new OpenTripPlannerMapperServiceImpl(geocodingService, zonedDateTimeService);

    @BeforeEach
    void init() {
        doReturn(getExampleWaypoints()).when(geocodingService).decodePolylineFrom(anyString());
    }

    @Test
    void test_extractJourneysFrom_openTripPlannerSuedbadenJourney_returns_correctly_mapped_journeys() {
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo("json/otpSuedbadenJourney.json", OpenTripPlannerJourneyResponse.class);
        String departureTestData = "Am Großhausberg 8";
        String arrivalTestData = "Sick AG";

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(journeyCallStatus.getThrowable()).isNull();
                    assertThat(journeyCallStatus.getCalledObject()).isEqualToComparingFieldByFieldRecursively(getFurtwangenToWaldkirchJourney());
                })
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_maps_openTripPlannerRnvJourney_correctly_to_journeys() {
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo("json/otpRnvJourney.json", OpenTripPlannerJourneyResponse.class);
        String departureTestData = "Mannheim Hbf";
        String arrivalTestData = "Ludwigsburg Center";

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(journeyCallStatus.getThrowable()).isNull();
                    assertThat(journeyCallStatus.getCalledObject()).isEqualToComparingFieldByFieldRecursively((getMannheimHbfLudwigsburgCenterJourney()));
                })
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_openTripPlannerRnvJourney_returns_failed_callStatus_for_wrong_json() {
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo("json/otpRnvJourney.json", OpenTripPlannerJourneyResponse.class);
        String departureTestData = "Mannheim Hbf";
        String arrivalTestData = "Ludwigsburg Center";
        testData.getPlan().getItineraries().get(0).setLegs(null);

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(journeyCallStatus.getThrowable()).isInstanceOf(NullPointerException.class);
                    assertThat(journeyCallStatus.getCalledObject()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_maps_openTripPlannerRnvJourney_returns_onErrorResume_mono_failed_callStatus() {
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo("json/otpRnvJourney.json", OpenTripPlannerJourneyResponse.class);
        String departureTestData = "Mannheim Hbf";
        String arrivalTestData = "Ludwigsburg Center";
        testData.setPlan(null);

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(journeyCallStatus.getThrowable()).isInstanceOf(NullPointerException.class);
                    assertThat(journeyCallStatus.getCalledObject()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_openTripPlannerRnvJourney_returns_correctly_mapped_journeys_with_arrivalDelay() {
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo("json/otpRnvJourney.json", OpenTripPlannerJourneyResponse.class);
        String departureTestData = "Mannheim Hbf";
        String arrivalTestData = "Ludwigsburg Center";
        testData.getPlan().getItineraries().get(0).getLegs().get(0).setArrivalDelay(300000L);

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> assertThat(journeyCallStatus.getCalledObject().getLegs().get(0).getDelayInMinutes()).isEqualTo(Duration.ofMinutes(5)))
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_openTripPlannerRnvJourney_returns_correctly_mapped_journeys_with_departureDelay() {
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo("json/otpRnvJourney.json", OpenTripPlannerJourneyResponse.class);
        String departureTestData = "Mannheim Hbf";
        String arrivalTestData = "Ludwigsburg Center";
        testData.getPlan().getItineraries().get(0).getLegs().get(0).setDepartureDelay(300000L);

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> assertThat(journeyCallStatus.getCalledObject().getLegs().get(0).getDelayInMinutes()).isEqualTo(Duration.ofMinutes(5)))
                .verifyComplete();
    }

    @Test
    void test_extractNearestStationFrom_otpNearestStations_returns_correctly_mapped_travelPoints() {
        List<OpenTripPlannerStationResponse> testData = retrieveJsonToListPojo("json/otpNearestStations.json", OpenTripPlannerStationResponse.class);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractNearestStationFrom(testData);

        StepVerifier.create(result)
                .assertNext(getOtpApiNearestStationsAsserts(getFurtwangenTownChurchTravelPoint()))
                .assertNext(getOtpApiNearestStationsAsserts(getFurtwangenGerwigSchoolTravelPoint()))
                .assertNext(getOtpApiNearestStationsAsserts(getFurtwangenRabenStreetTravelPoint()))
                .assertNext(getOtpApiNearestStationsAsserts(getFurtwangenFriedrichSchoolTravelPoint()))
                .assertNext(getOtpApiNearestStationsAsserts(getFurtwangenAllmendStreetTravelPoint()))
                .assertNext(getOtpApiNearestStationsAsserts(getFurtwangenOttoHahnSchoolTravelPoint()))
                .assertNext(getOtpApiNearestStationsAsserts(getFurtwangenRoessleTravelPoint()))
                .assertNext(getOtpApiNearestStationsAsserts(getFurtwangenIlbenStreetTravelPoint()))
                .verifyComplete();
    }

    @Test
    void test_extractNearestStationFrom_otpNearestStations_returns_failed_call_status_for_wrong_json() {
        List<OpenTripPlannerStationResponse> testData = retrieveJsonToListPojo("json/otpNearestStations.json", OpenTripPlannerStationResponse.class);
        testData.get(0).setDist(null);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractNearestStationFrom(testData);

        StepVerifier.create(result)
                .assertNext(travelPointCallStatus -> {
                    assertThat(travelPointCallStatus.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(travelPointCallStatus.getThrowable()).isInstanceOf(NullPointerException.class);
                    assertThat(travelPointCallStatus.getCalledObject()).isNull();
                })
                .expectNextCount(7L)
                .verifyComplete();
    }


}

