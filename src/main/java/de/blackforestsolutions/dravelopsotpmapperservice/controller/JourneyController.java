package de.blackforestsolutions.dravelopsotpmapperservice.controller;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("otp/journeys")
public class JourneyController {

    private final JourneyApiService journeyApiService;

    public JourneyController(JourneyApiService journeyApiService) {
        this.journeyApiService = journeyApiService;
    }

    @RequestMapping(value = "/get", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Journey> retrieveOpenTripPlannerJourneys(@RequestBody ApiToken request) {
        return journeyApiService.retrieveJourneysFromApiService(request);
    }
}