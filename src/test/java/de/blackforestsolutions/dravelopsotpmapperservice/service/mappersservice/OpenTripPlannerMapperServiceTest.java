package de.blackforestsolutions.dravelopsotpmapperservice.service.mappersservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.station.OpenTripPlannerStationResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.GtfsApiTokenConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.MissingPrefixException;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getRnvGtfsApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getSbgGtfsApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getFurtwangenToWaldkirchJourney;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.JourneyObjectMother.getMannheimHbfLudwigsburgCenterJourney;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.*;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.UUIDObjectMother.TEST_UUID_2;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.UUIDObjectMother.TEST_UUID_3;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.WaypointsObjectMother.getExampleWaypoints;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToListPojo;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.retrieveJsonToPojo;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutil.TestAssertions.getOtpApiNearestStationsAsserts;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutil.TestUtils.convertApiTokensToConfigToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OpenTripPlannerMapperServiceTest {

    private static final String TEST_ID_WITHOUT_PREFIX = "0";
    private static final int FIRST_INDEX = 0;
    private static final int FIRST_LEG_WITH_VEHICLE = 1;
    private static final int FIRST_JOURNEY = 0;
    private static final int FIRST_INTERMEDIATE_STOP = 0;

    private final GeocodingService geocodingService = spy(GeocodingServiceImpl.class);
    private final ZonedDateTimeService zonedDateTimeService = new ZonedDateTimeServiceImpl();
    private final UuidService uuidService = mock(UuidServiceImpl.class);
    private final GtfsApiTokenConfiguration gtfsApiTokenConfiguration = convertApiTokensToConfigToken(List.of(getRnvGtfsApiToken(), getSbgGtfsApiToken()));

    private final OpenTripPlannerMapperService classUnderTest = new OpenTripPlannerMapperServiceImpl(
            geocodingService,
            zonedDateTimeService,
            uuidService,
            gtfsApiTokenConfiguration
    );

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(zonedDateTimeService, "timeZone", "Europe/Berlin");
        doReturn(getExampleWaypoints()).when(geocodingService).decodePolylineFrom(anyString());
        doReturn(TEST_UUID_2).when(uuidService).createUUID();
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
        doReturn(TEST_UUID_3).when(uuidService).createUUID();

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(journeyCallStatus.getThrowable()).isNull();
                    assertThat(journeyCallStatus.getCalledObject()).isEqualToComparingFieldByFieldRecursively(getMannheimHbfLudwigsburgCenterJourney());
                })
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_journeyResponse_without_prefix_in_tripId_returns_failed_call_status_with_MissingPrefixException() {
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo("json/otpSuedbadenJourney.json", OpenTripPlannerJourneyResponse.class);
        testData.getPlan().getItineraries().get(FIRST_JOURNEY).getLegs().get(FIRST_LEG_WITH_VEHICLE).setTripId(TEST_ID_WITHOUT_PREFIX);

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, "", "");

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(journeyCallStatus.getThrowable()).isInstanceOf(MissingPrefixException.class);
                    assertThat(journeyCallStatus.getCalledObject()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_journeyResponse_without_prefix_in_agencyId_returns_failed_call_status_with_MissingPrefixException() {
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo("json/otpSuedbadenJourney.json", OpenTripPlannerJourneyResponse.class);
        testData.getPlan().getItineraries().get(FIRST_JOURNEY).getLegs().get(FIRST_LEG_WITH_VEHICLE).setAgencyId(TEST_ID_WITHOUT_PREFIX);

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, "", "");

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(journeyCallStatus.getThrowable()).isInstanceOf(MissingPrefixException.class);
                    assertThat(journeyCallStatus.getCalledObject()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_journeyResponse_without_prefix_in_arrivalStopId_returns_failed_call_status_with_MissingPrefixException() {
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo("json/otpSuedbadenJourney.json", OpenTripPlannerJourneyResponse.class);
        testData.getPlan().getItineraries().get(FIRST_JOURNEY).getLegs().get(FIRST_LEG_WITH_VEHICLE).getIntermediateStops().get(FIRST_INTERMEDIATE_STOP).setStopId(TEST_ID_WITHOUT_PREFIX);

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, "", "");

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(journeyCallStatus.getThrowable()).isInstanceOf(MissingPrefixException.class);
                    assertThat(journeyCallStatus.getCalledObject()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_journeyResponse_without_prefix_in_intermediateStopId_returns_failed_call_status_with_MissingPrefixException() {
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo("json/otpSuedbadenJourney.json", OpenTripPlannerJourneyResponse.class);
        testData.getPlan().getItineraries().get(FIRST_JOURNEY).getLegs().get(FIRST_LEG_WITH_VEHICLE).getTo().setStopId(TEST_ID_WITHOUT_PREFIX);

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, "", "");

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(journeyCallStatus.getThrowable()).isInstanceOf(MissingPrefixException.class);
                    assertThat(journeyCallStatus.getCalledObject()).isNull();
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
    void test_extractNearestStationFrom_nearestStationsResponse_without_id_prefix_in_first_station_returns_failed_call_status_with_MissingPrefixException_in_first_result() {
        List<OpenTripPlannerStationResponse> testData = retrieveJsonToListPojo("json/otpNearestStations.json", OpenTripPlannerStationResponse.class);
        testData.get(FIRST_INDEX).setId(TEST_ID_WITHOUT_PREFIX);

        Flux<CallStatus<TravelPoint>> result = classUnderTest.extractNearestStationFrom(testData);

        StepVerifier.create(result)
                .assertNext(travelPointResult -> {
                    assertThat(travelPointResult.getStatus()).isEqualTo(Status.FAILED);
                    assertThat(travelPointResult.getThrowable()).isInstanceOf(MissingPrefixException.class);
                    assertThat(travelPointResult.getCalledObject()).isNull();
                })
                .assertNext(getOtpApiNearestStationsAsserts(getFurtwangenGerwigSchoolTravelPoint()))
                .expectNextCount(6L)
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

