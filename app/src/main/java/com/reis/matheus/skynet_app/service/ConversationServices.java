package com.reis.matheus.skynet_app.service;

import com.reis.matheus.skynet_app.repository.RepoConversation;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by matheus on 4/28/18.
 */

public interface ConversationServices {
    @FormUrlEncoded
    @POST("/api/conversation")
    Call<RepoConversation> postMessage(@Field("message") String message);
}
