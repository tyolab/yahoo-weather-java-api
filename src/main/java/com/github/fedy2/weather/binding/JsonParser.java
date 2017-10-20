package com.github.fedy2.weather.binding;

import com.github.fedy2.weather.data.HttpResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 11/10/17.
 */

public class JsonParser implements Parser<HttpResult> {

    private final Gson gson;

    public JsonParser() {
        GsonBuilder builder = new GsonBuilder().setDateFormat("EEE, d MMM yyyy hh:mm a z");
        this.gson = builder.create();
    }

    @Override
    public HttpResult parse(String txt) throws Exception {
        return gson.fromJson(txt, HttpResult.class);
    }
}
