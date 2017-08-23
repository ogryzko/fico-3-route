package ru.mbtc.fico3.route.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

public class EndpointManager {
    static final String URL_PRIMARY_KEY = "url.primary";
    static final String URL_SECONDARY_KEY = "url.secondary";
    private static final int DIMENSION_COEFFICIENT = 100;
    static final String PROBABILITY_KEY_PREFIX = "probability.";
    private final Properties properties;
    private final Random random;
    private final URI defaultEndpointURI;
    private final URI secondaryEndpointURI;

    private static final Logger log = LogManager.getLogger(EndpointManager.class);
    public EndpointManager(Properties properties) throws InvalidPropertiesFormatException {
        this.properties = properties;

        if(!properties.containsKey(URL_PRIMARY_KEY)){
            throw new InvalidPropertiesFormatException("Key: " + URL_PRIMARY_KEY + " is required");
        } else if (!properties.containsKey(URL_SECONDARY_KEY)){
            throw new InvalidPropertiesFormatException("Key: " + URL_SECONDARY_KEY + " is required");
        }

        String defaultEndpointString = properties.getProperty(URL_PRIMARY_KEY);
        String secondaryEndpointString = properties.getProperty(URL_SECONDARY_KEY);
        try {
            URL defaultEndpointUrl = new URL(defaultEndpointString);
            URL secondaryEndpointUrl = new URL(secondaryEndpointString);
            defaultEndpointURI = defaultEndpointUrl.toURI();
            secondaryEndpointURI = secondaryEndpointUrl.toURI();
        }
        catch (MalformedURLException | URISyntaxException e) {
            throw new InvalidPropertiesFormatException(e);
        }
        random = new Random();
    }


    public URI getEndpoint(String memberCode){
        if(memberCode != null){
            String probabilityKey = PROBABILITY_KEY_PREFIX + memberCode;
            String probabilityString = properties.getProperty(probabilityKey);

            try {
                if (probabilityString != null) {
                    double n = (Double.parseDouble(probabilityString) / DIMENSION_COEFFICIENT);
                    if(n > 100 || n < 0) {
                        log.warn("Probability value is out of range");
                    }
                    log.debug("Probability: " + n);
                    if (random.nextDouble() <= n){
                        return secondaryEndpointURI;
                    }
                }
            } catch (NumberFormatException e){
                log.warn("Invalid property value with key: " + probabilityKey + " and value: " + probabilityString);
            }
            return defaultEndpointURI;
        }
        log.warn("MemberCode not found!" );
        return defaultEndpointURI;
    }
}
