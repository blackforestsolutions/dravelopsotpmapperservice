package de.blackforestsolutions.dravelopsotpmapperservice.testutils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestUtils {

    /**
     * Parse the given json into object of type pojo
     *
     * @param json the given json
     * @param pojo the class the json has to be parsed
     * @param <T> type of the class the json has to be parsed
     * @return object
     */
    public static <T> T retrieveJsonToPojo(String json, Class<T> pojo) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            return mapper.readValue(json, pojo);
        } catch (Exception e) {
            log.info("Exception while parsing string to pojo: ", e);
            return null;
        }
    }

}
