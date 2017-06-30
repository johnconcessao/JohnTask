package com.xyzcorp.api;


import com.xyzcorp.environment.EnvironmentConfigurator;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.DecoderConfig;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;


public class RestApiBasicClient {
    private final static Map<String, String> DEFAULT_PARAMS = new HashMap<>();
    private static volatile RestApiBasicClient restClientInstance;


    private RestApiBasicClient() {
        RestAssured.config
                .decoderConfig(new DecoderConfig("UTF-8"))
                .encoderConfig(new EncoderConfig("UTF-8", "UTF-8"));
        RestAssured.baseURI = EnvironmentConfigurator.getInstance().getApiUrl();
        DEFAULT_PARAMS.put("locale", EnvironmentConfigurator.getInstance().getLocalization());
        DEFAULT_PARAMS.put("wa_key", EnvironmentConfigurator.getInstance().getWaKey());
    }

    public static RestApiBasicClient getInstance() {
        RestApiBasicClient sysProps = restClientInstance;
        if (sysProps == null) {
            synchronized (RestApiBasicClient.class) {
                sysProps = restClientInstance;
                if (sysProps == null) {
                    restClientInstance = sysProps = new RestApiBasicClient();
                }
            }
        }
        return sysProps;
    }


    public ValidatableResponse executeGetRequest(String requestUrl, Map<String, String> queryParams) {
        return given()
                .queryParameters(DEFAULT_PARAMS)
                .queryParameters(queryParams)
                .log().all()
                .get(requestUrl)
                .then().log().all();
    }

    public ValidatableResponse executeGetRequest(String requestUrl, String key, String value) {
        return given()
                .queryParameters(DEFAULT_PARAMS)
                .queryParams(key, value)
                .log().all()
                .get(requestUrl)
                .then().log().all();
    }


    public ValidatableResponse executePostRequest(String requestUrl, Map<String, String> queryParams, String bodyAsText) {
        return given()
                .contentType(ContentType.JSON)
                .queryParameters(DEFAULT_PARAMS)
                .queryParameters(queryParams)
                .body(bodyAsText)
                .log().all()
                .post(requestUrl)
                .then().log().all();
    }

}
