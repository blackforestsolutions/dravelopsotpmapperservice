package de.blackforestsolutions.dravelopsotpmapperservice.controller;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.TravelPointApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("travelpoints")
public class TravelPointController {

    private final TravelPointApiService travelPointApiService;

    @Autowired
    public TravelPointController(TravelPointApiService travelPointApiService) {
        this.travelPointApiService = travelPointApiService;
    }

    @RequestMapping(value = "/nearest", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TravelPoint> getNearestStationsBy(@RequestBody ApiToken request) {
        return travelPointApiService.retrieveNearestStationsFromApiService(request);
    }
}
