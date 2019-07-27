package org.succlz123.github.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import org.succlz123.github.model.SearchResult

/**
 * Created by succlz123 on 2019/07/27.
 */
interface GithubApiService {

    @GET("search/users")
    fun searchUsers(@Query("q") name: String, @Query("page") page: Int): Call<SearchResult>
}
