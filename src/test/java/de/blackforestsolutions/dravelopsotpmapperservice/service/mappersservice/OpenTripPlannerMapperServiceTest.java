package de.blackforestsolutions.dravelopsotpmapperservice.service.mappersservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperServiceImpl;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.PolylineDecodingService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.UuidService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.JourneyObjectMother.getFurtwangenToWaldkirchJourney;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TrackObjectMother.getExampleTrack;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.UUIDObjectMother.*;
import static de.blackforestsolutions.dravelopsotpmapperservice.testutils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenTripPlannerMapperServiceTest {

    private final UuidService uuidService = mock(UuidService.class);
    private final PolylineDecodingService polyLineDecodingService = mock(PolylineDecodingService.class);

    private final OpenTripPlannerMapperService classUnderTest = new OpenTripPlannerMapperServiceImpl(uuidService, polyLineDecodingService);

    @BeforeEach
    void init() {
        when(uuidService.createUUID())
                .thenReturn(TEST_UUID_1)
                .thenReturn(TEST_UUID_2)
                .thenReturn(TEST_UUID_3)
                .thenReturn(TEST_UUID_4)
                .thenReturn(TEST_UUID_5);

        when(polyLineDecodingService.decode(anyString()))
                .thenReturn(getExampleTrack());
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
}
