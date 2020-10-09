package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.stereotype.Service;

@Service
public class RequestTokenHandlerServiceImpl implements RequestTokenHandlerService {

    @Override
    public ApiToken getRequestApiTokenWith(ApiToken request, ApiToken configuredRequestData) {
        ApiToken.ApiTokenBuilder builderCopy = new ApiToken.ApiTokenBuilder(configuredRequestData);
        builderCopy.setArrival(request.getArrival());
        builderCopy.setArrivalCoordinate(request.getArrivalCoordinate());
        builderCopy.setDeparture(request.getDeparture());
        builderCopy.setDepartureCoordinate(request.getDepartureCoordinate());
        builderCopy.setDateTime(request.getDateTime());
        builderCopy.setOptimize(request.getOptimize());
        builderCopy.setIsArrivalDateTime(request.getIsArrivalDateTime());
        builderCopy.setLanguage(request.getLanguage());
        return builderCopy.build();
    }
}
