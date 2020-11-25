package com.example.myapplication

import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @GET("video/stream/")
    fun getYoutubeList(): Call<ArrayList<Youtube>>

    @POST("login/")
    @FormUrlEncoded
    fun requestLogin(
        @Field("userid") userID: String,
        @Field("userpw") userPW: String
    ): Call<User>

    @POST("signup/")
    @FormUrlEncoded
    fun register(
        @Field("userid") userID: String,
        @Field("userpw") userPW: String,
        @Field("userpc") userPC: String
    ): Call<User>

    @POST("comment/upload/")
    @FormUrlEncoded
    fun uploadComment(
        @Field("token") token: String,
        @Field("comment") commentText: String,
        @Field("videoid") videoid: String
    ): Call<Comment>

    @GET("comment/view/all")
    fun getComment(
    ): Call<ArrayList<Comment>>

    @POST("comment/view/")
    fun getComment2(
        @Field("url") url: String
    ): Call<ArrayList<Comment>>

    @GET("comment/view/")
    fun getComment3(
        @Path("videoid") videoid: String
    ): Call<ArrayList<Comment>>
}