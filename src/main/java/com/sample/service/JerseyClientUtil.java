package com.sample.service;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.sample.model.SpotifyResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by aniketbhide on 9/17/16.
 */
public class JerseyClientUtil {

    private static String BASE_URL = "https://api.spotify.com";
    private static String REQUEST_TYPE = "track";
    private static String LIMIT = "10";

    private static JerseyClientUtil clientUtil = new JerseyClientUtil();

    private WebResource webResource;

    private JerseyClientUtil(){
        Client client = Client.create();
        webResource = client.resource(BASE_URL);
    }

    public static JerseyClientUtil getInstance(){
        return clientUtil;
    }

    public SpotifyResponse getResponseTrack(String trackTitle) throws IOException {

        SpotifyResponse spotifyResponse = null;

        ClientResponse response = webResource.path("/v1/search")
                .queryParam("type", REQUEST_TYPE)
                .queryParam("limit", LIMIT)
                .queryParam("q", "track:" + trackTitle)
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String output = response.getEntity(String.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        spotifyResponse = mapper.readValue(output, SpotifyResponse.class);

        return spotifyResponse;
    }
}
