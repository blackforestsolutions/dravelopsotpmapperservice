package de.blackforestsolutions.dravelopsotpmapperservice.service.mappersservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.ZoneId;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.JourneyObjectMother.getFurtwangenToWaldkirchJourney;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.JourneyObjectMother.getMannheimHbfLudwigsburgCenterJourney;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TrackObjectMother.getExampleTrack;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.UUIDObjectMother.*;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OpenTripPlannerMapperServiceTest {

    private final UuidService uuidService = mock(UuidService.class);
    private final PolylineDecodingService polyLineDecodingService = mock(PolylineDecodingService.class);
    private final ZonedDateTimeService zonedDateTimeService = new ZonedDateTimeServiceImpl(ZoneId.of("Europe/Berlin"));
    private final CoordinateFormatterService coordinateFormatterService = new CoordinateFormatterServiceImpl();
    private final DistanceFormatterService distanceFormatterService = new DistanceFormatterServiceImpl();

    private final OpenTripPlannerMapperService classUnderTest = new OpenTripPlannerMapperServiceImpl(uuidService, polyLineDecodingService, zonedDateTimeService, coordinateFormatterService, distanceFormatterService);

    @BeforeEach
    void init() {
        when(uuidService.createUUID())
                .thenReturn(TEST_UUID_1)
                .thenReturn(TEST_UUID_2)
                .thenReturn(TEST_UUID_3)
                .thenReturn(TEST_UUID_4)
                .thenReturn(TEST_UUID_5);

        doReturn(getExampleTrack()).when(polyLineDecodingService).decode(anyString());
    }

    @Test
    void test_extractJourneysFrom_maps_openTripPlannerSuedbadenJourney_correctly_to_journeys() {
        String testDataJson = getResourceFileAsString("json/openTripPlannerSuedbadenJourney.json");
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo(testDataJson, OpenTripPlannerJourneyResponse.class);
        String departureTestData = "Am Großhausberg 8";
        String arrivalTestData = "Sick AG";

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(journeyCallStatus.getThrowable()).isNull();
                    assertThat(toJson(journeyCallStatus.getCalledObject())).isEqualTo(toJson(getFurtwangenToWaldkirchJourney()));
                })
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_maps_openTripPlannerRnvJourney_correctly_to_journeys() {
        String testDataJson = getResourceFileAsString("json/openTripPlannerRnvJourney.json");
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo(testDataJson, OpenTripPlannerJourneyResponse.class);
        String departureTestData = "Mannheim Hbf";
        String arrivalTestData = "Ludwigsburg Center";

        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> {
                    assertThat(journeyCallStatus.getStatus()).isEqualTo(Status.SUCCESS);
                    assertThat(journeyCallStatus.getThrowable()).isNull();
                    assertThat(toJson(journeyCallStatus.getCalledObject())).isEqualTo(toJson(getMannheimHbfLudwigsburgCenterJourney()));
                })
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_maps_openTripPlannerRnvJourney_returns_failed_CallStatus() {
        String testDataJson = getResourceFileAsString("json/openTripPlannerRnvJourney.json");
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo(testDataJson, OpenTripPlannerJourneyResponse.class);
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
        String testDataJson = getResourceFileAsString("json/openTripPlannerRnvJourney.json");
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo(testDataJson, OpenTripPlannerJourneyResponse.class);
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
    void test_extractJourneysFrom_maps_openTripPlannerRnvJourney_correctly_to_journeys_with_arrivalDelay() {

        String testDataJson = getResourceFileAsString("json/openTripPlannerRnvJourney.json");
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo(testDataJson, OpenTripPlannerJourneyResponse.class);
        String departureTestData = "Mannheim Hbf";
        String arrivalTestData = "Ludwigsburg Center";

        testData.getPlan().getItineraries().get(0).getLegs().get(0).setArrivalDelay(300000L);
        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> assertThat(journeyCallStatus.getCalledObject().getLegs().get(TEST_UUID_2).getDelay()).isEqualTo(Duration.ofMinutes(5)))
                .verifyComplete();
    }

    @Test
    void test_extractJourneysFrom_maps_openTripPlannerRnvJourney_correctly_to_journeys_with_departureDelay() {

        String testDataJson = getResourceFileAsString("json/openTripPlannerRnvJourney.json");
        OpenTripPlannerJourneyResponse testData = retrieveJsonToPojo(testDataJson, OpenTripPlannerJourneyResponse.class);
        String departureTestData = "Mannheim Hbf";
        String arrivalTestData = "Ludwigsburg Center";

        testData.getPlan().getItineraries().get(0).getLegs().get(0).setDepartureDelay(300000L);
        Flux<CallStatus<Journey>> result = classUnderTest.extractJourneysFrom(testData, departureTestData, arrivalTestData);

        StepVerifier.create(result)
                .assertNext(journeyCallStatus -> assertThat(journeyCallStatus.getCalledObject().getLegs().get(TEST_UUID_2).getDelay()).isEqualTo(Duration.ofMinutes(5)))
                .verifyComplete();
    }
}

