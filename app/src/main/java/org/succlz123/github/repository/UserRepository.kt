package org.succlz123.github.repository

import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.succlz123.github.api.GithubApiService
import org.succlz123.github.model.SearchResult

/**
 * Created by succlz123 on 2019/07/27.
 */
class UserRepository private constructor() {
    private val apiApiService: GithubApiService

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        apiApiService = retrofit.create(GithubApiService::class.java)
    }

    fun searchUsers(callback: Callback<SearchResult>, name: String, page: Int) {
        val call = apiApiService.searchUsers(name, page)
        call.enqueue(callback)
    }

    private object SingletonHolder {
        val holder = UserRepository()
    }

    companion object {
        private const val BASE_URL = "https://api.github.com/"
        val instance = SingletonHolder.holder
    }
}