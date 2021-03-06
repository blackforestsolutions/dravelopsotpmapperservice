package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerConfiguration;

import java.util.List;

public class OpenTripPlannerConfigurationObjectMother {

    private static final String DEFAULT_TEST_PROTOCOL = "http";
    private static final String DEFAULT_TEST_HOST = "localhost";
    private static final boolean DEFAULT_TEST_SHOW_INTERMEDIATE_STOPS = true;
    private static final int DEFAULT_TEST_MAX_RESULTS = 10;

    public static OpenTripPlannerConfiguration getOtpConfigurationWithNoEmptyFields() {
        OpenTripPlannerConfiguration otpConfig = new OpenTripPlannerConfiguration();
        otpConfig.setShowIntermediateStops(DEFAULT_TEST_SHOW_INTERMEDIATE_STOPS);
        otpConfig.setMaxResults(DEFAULT_TEST_MAX_RESULTS);
        otpConfig.setApiTokens(getOtpConfigurations());
        return otpConfig;
    }

    private static List<OpenTripPlannerConfiguration.ApiToken> getOtpConfigurations() {
        return List.of(
                getOtpFastLaneConfiguration(),
                getOtpSlowLaneConfiguration()
        );
    }

    private static OpenTripPlannerConfiguration.ApiToken getOtpFastLaneConfiguration() {
        OpenTripPlannerConfiguration.ApiToken fastLaneOtpConfig = new OpenTripPlannerConfiguration.ApiToken();

        fastLaneOtpConfig.setProtocol(DEFAULT_TEST_PROTOCOL);
        fastLaneOtpConfig.setHost(DEFAULT_TEST_HOST);
        fastLaneOtpConfig.setPort(9000);
        fastLaneOtpConfig.setRouter("bw-fast");

        return fastLaneOtpConfig;
    }

    private static OpenTripPlannerConfiguration.ApiToken getOtpSlowLaneConfiguration() {
        OpenTripPlannerConfiguration.ApiToken slowLaneOtpConfig = new OpenTripPlannerConfiguration.ApiToken();

        slowLaneOtpConfig.setProtocol(DEFAULT_TEST_PROTOCOL);
        slowLaneOtpConfig.setHost(DEFAULT_TEST_HOST);
        slowLaneOtpConfig.setPort(9002);
        slowLaneOtpConfig.setRouter("bw-slow");

        return slowLaneOtpConfig;
    }
}
