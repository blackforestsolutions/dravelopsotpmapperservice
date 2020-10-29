package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class TestConfig {

    /**
     * !!! ÄNDERE NICHT DIE ApiTokens im Testdatenmodell
     * wenn dann hier über
     * return new ApiToken.ApiTokenBuilder(getOtpMapperApiToken())
     * .setBlaBla()
     * .build;
     */

    @Profile("bw-int")
    @Bean
    public ApiToken otpMapperApiToken() {
        return ApiTokenObjectMother.getOtpMapperApiToken();
    }


}
