package com.example.shell.service;

import com.squareup.moshi.Moshi;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;

public class AbstractService {
    @Value("${service.url}")
    protected String apiBaseurl;
    protected final OkHttpClient client = new OkHttpClient();
    protected final Moshi moshi = new Moshi.Builder().build();

}
