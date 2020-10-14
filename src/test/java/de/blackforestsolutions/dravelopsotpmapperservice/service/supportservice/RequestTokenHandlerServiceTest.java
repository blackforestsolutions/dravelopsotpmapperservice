package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.junit.jupiter.api.Test;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.ApiTokenObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;

class RequestTokenHandlerServiceTest {

    private final RequestTokenHandlerService classUnderTest = new RequestTokenHandlerServiceImpl();

    @Test
    void test_getRequestApiTokenWith_configured_token_and_user_token_returns_correct_call_token() {
        ApiToken configuredTestData = getOpenTripPlannerConfiguredApiToken();
        ApiToken requestTestData = getRequestToken();

        ApiToken result = classUnderTest.getRequestApiTokenWith(requestTestData, configuredTestData);

        assertThat(result).isEqualToComparingFieldByField(getOpenTripPlannerApiToken());
    }
}
