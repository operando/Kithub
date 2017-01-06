package com.os.operando.kithub.api

import com.os.operando.kithub.entity.AuthToken
import com.os.operando.kithub.entity.Repo
import com.os.operando.kithub.entity.User
import retrofit2.http.*
import rx.Single


interface GitHubApiClient {

    @PUT("/authorizations/clients/{client_id}/{fingerprint}")
    fun putAuthorization(@Header("Authorization") authorization: String,
                         @Header("X-GitHub-OTP") twoFactorCode: String = "",
                         @Path("client_id") clientId: String,
                         @Path("fingerprint") fingerprint: String,
                         @Body body: AuthorizationBody): Single<AuthToken>

    @GET("/users/{user}/repos")
    fun getUserRepos(@Path("user") user: String,
                     @Query("sort") sort: String = "updated",
                     @Query("page") page: Int = 1,
                     @Query("per_page") perPage: Int = 30): Single<List<Repo>>

    @GET("/users/{user}")
    fun getUser(@Path("user") user: String): Single<User>
}