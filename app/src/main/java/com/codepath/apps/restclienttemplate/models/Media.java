package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Media {

    public String displayUrl;

    //converts from json object to Media object
    public static Media fromJson(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.displayUrl = jsonObject.getString("media_url_https");
        return media;
    }

    //converts from json array to a list of media objects
    public static List<Media> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Media> media = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            media.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return media;
    }
}
