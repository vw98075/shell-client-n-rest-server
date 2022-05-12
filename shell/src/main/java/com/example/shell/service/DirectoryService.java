package com.example.shell.service;

import com.squareup.moshi.JsonAdapter;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DirectoryService extends AbstractService {

    final Logger log = LoggerFactory.getLogger(DirectoryService.class);

    private final LoginService loginService;

    private final JsonAdapter<DirectoryData> loginDataJsonAdapter = moshi.adapter(DirectoryData.class);

    public DirectoryService(LoginService loginService) {
        this.loginService = loginService;
    }

    public boolean uploadDirectories(String dir) {

        String postBody = loginDataJsonAdapter.toJson(new DirectoryData(dir));
        RequestBody body = RequestBody.create(postBody, okhttp3.MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(apiBaseurl + "/directories")
//                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Authorization", "Bearer " + loginService.token)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error(response.code() + " - " + response.message());
                return false;
            }
            return true;
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
            return false;
        }
    }

    public record DirectoryData(String path) {}
}
