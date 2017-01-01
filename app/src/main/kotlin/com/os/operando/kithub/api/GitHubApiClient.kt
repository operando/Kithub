package com.os.operando.kithub.api

import com.os.operando.kithub.entity.Repo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Single


interface GitHubApiClient {

    @GET("/users/{user}/repos")
    fun getUserRepos(@Path("user") user: String,
                     @Query("sort") sort: String = "updated",
                     @Query("page") page: Int = 1,
                     @Query("per_page") perPage: Int = 30): Single<List<Repo>>
}