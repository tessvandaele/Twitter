package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Entities {

    //list of media objects
    public List<Media> media;

    //converts from json object to an entity
    public static Entities fromJson(JSONObject jsonObject) throws JSONException {
        Entities entity = new Entities();
        try {
            entity.media = Media.fromJsonArray(jsonObject.getJSONArray("media"));
        } catch (JSONException e) {
            entity.media = new ArrayList<>();
            entity.media.add(new Media());
        }

        return entity;
    }
}
