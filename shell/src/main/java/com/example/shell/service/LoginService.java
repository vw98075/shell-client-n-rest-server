package com.example.shell.service;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LoginService extends AbstractService {

    final Logger log = LoggerFactory.getLogger(LoginService.class);

    String token;

    private String username;

    private final JsonAdapter<LoginData> loginDataJsonAdapter = moshi.adapter(LoginData.class);

    private final JsonAdapter<IdToken> idTokenJsonAdapter = moshi.adapter(IdToken.class);

    public boolean login(String username, String password) {

        String postBody = loginDataJsonAdapter.toJson(new LoginData(username, password, true));
        RequestBody body = RequestBody.create(postBody, okhttp3.MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(apiBaseurl + "/authenticate")
//                .addHeader("Accept", "application/json; q=0.5")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error(response.code() + " - " + response.message());
                return false;
            }
            IdToken token = idTokenJsonAdapter.fromJson(response.body().string());
            this.token = token.idToken();
            this.username = username;
            log.debug(token.toString());
            return true;
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
            return false;
        }
    }

    public void logout() {
        this.token = null;
        this.username = null;
    }

    public boolean isLoggedIn() {
        return this.token != null; // TODO: and the token is not expired
    }

    public String loggedInUser() {
        return username;
    }

    public record LoginData(String username, String password, boolean rememberMe) {
    }

    public record IdToken(@Json(name = "id_token") String idToken) {
    }
}

