package com.example.dan;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;


public class Main {
    private static final String NETWORK_NAME = "DAN";
    private static final String PROTECTED_RESOURCE_URL = "https://sso.gov.mn/oauth2/api/v1/service";

    String readScope() throws URISyntaxException {
        byte[] scopeBytes = null;
        URL resource = getClass().getResource("danScope.json");
        if (resource == null){
            throw new IllegalArgumentException("Scope file not found");
        }
        try{
            scopeBytes = Files.readAllBytes(Paths.get(resource.toURI()));
        }catch(IOException ex){
            ex.printStackTrace();
        }

        assert scopeBytes != null;
        return Base64.getEncoder().encodeToString(scopeBytes);
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, URISyntaxException {
        Main main = new Main();
        final String clientId = System.getenv("CLIENT_ID");
        final String clientSecret = System.getenv("CLIENT_SECRET");
        final String callbackUrl = System.getenv("CALLBACK_URL");
        final String secretState = "secret" + new Random().nextInt(999_999);
        final OAuth20Service service = new ServiceBuilder(clientId)
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .defaultScope(main.readScope()) // replace with desired scope
                .callback(callbackUrl)
                .build(DanApi.instance());
        final Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        final Map<String, String> additionalParams = new HashMap<>();
        final String authorizationUrl = service.createAuthorizationUrlBuilder()
                .state(secretState)
                .additionalParams(additionalParams)
                .build();
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        final String code = in.nextLine();
        System.out.println();

        System.out.println("And paste the state from server here. We have set 'secretState'='" + secretState + "'.");
        System.out.print(">>");
        final String value = in.nextLine();
        if (secretState.equals(value)) {
            System.out.println("State value does match!");
        } else {
            System.out.println("Expected = " + secretState);
            System.out.println("Got      = " + value);
            System.out.println();
        }

        System.out.println("Trading the Authorization Code for an Access Token...");
        OAuth2AccessToken accessToken = service.getAccessToken(code);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");

        System.out.println("Now we're going to access citizen data");

        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        System.out.println();
        try (Response response = service.execute(request)) {
            System.out.println(response.getCode());
            System.out.println(response.getBody());
        }
        System.out.println();
    }
}
