package com.personalassistant;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.IOUtils;
import com.personalassistant.token.TokenService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class PersonalAssistantBackend implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AmazonS3 amazonS3 = AmazonS3ClientBuilder.defaultClient();
    private final TokenService tokenService = new TokenService(amazonS3);

    public String handleRequest(String input, Context context) {
        return "Hello world!";
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setHeaders(Collections.singletonMap("Content-Type", "text/html; charset=utf-8"));

        System.out.println(request.getBody());
        try {
            if (tokenService.validateToken(request.getHeaders().get("token"))) {
            } else if(request.getBody() != null && request.getBody().equals(System.getenv("login"))) {
                tokenService.createToken();
                responseEvent.withBody(loadPage());
            } else {
                responseEvent.withBody(loadLoginPage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseEvent;
    }

    private String loadLoginPage() throws IOException {
        return amazonS3.getObjectAsString(System.getenv("bucketName"), System.getenv("loginPage"));
    }

    private String loadPage() throws IOException {
        return amazonS3.getObjectAsString(System.getenv("bucketName"), System.getenv("page"));
    }


}
