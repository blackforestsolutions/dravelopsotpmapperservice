package de.blackforestsolutions.dravelopsotpmapperservice.controller;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("journeys")
public class JourneyController {

    private final JourneyApiService journeyApiService;

    @Autowired
    public JourneyController(JourneyApiService journeyApiService) {
        this.journeyApiService = journeyApiService;
    }

    @RequestMapping(value = "/otp", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Journey> getJourneysBy(@RequestBody ApiToken request) {
        return journeyApiService.retrieveJourneysFromApiService(request);
    }
}