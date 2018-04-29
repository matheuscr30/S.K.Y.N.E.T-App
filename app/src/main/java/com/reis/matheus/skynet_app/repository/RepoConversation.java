package com.reis.matheus.skynet_app.repository;

import com.google.gson.annotations.SerializedName;

/**
 * Created by matheus on 4/28/18.
 */

public class RepoConversation{
    @SerializedName("response")
    private String response;

    @SerializedName("language")
    private String language;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}