package com.harsh.cermati.api

import android.util.Log
import com.harsh.cermati.model.Users
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Harsh Mittal on 07/04/19.
 */
private const val TAG = "GithubService"
private const val IN_QUALIFIER = "in:name,description"

/**
 * Search repos based on a query.
 * Trigger a request to the Github searchRepo API with the following params:
 * @param query searchRepo keyword
 * @param page request page index
 * @param itemsPerPage number of repositories to be returned by the Github API per page
 *
 * The result of the request is handled by the implementation of the functions passed as params
 * @param onSuccess function that defines how to handle the list of repos received
 * @param onError function that defines how to handle request failure
 */
fun searchRepos(
        service: WebService,
        query: String,
        page: Int,
        itemsPerPage: Int,
        onSuccess: (users: List<Users>) -> Unit,
        onError: (error: String) -> Unit
) {
    Log.d(TAG, "query: $query, page: $page, itemsPerPage: $itemsPerPage")

//    val apiQuery = query + IN_QUALIFIER
    val apiQuery = query

    service.searchRepos(apiQuery, page, itemsPerPage).enqueue(
        object : Callback<UserSearchResponse> {
            override fun onFailure(call: Call<UserSearchResponse>?, t: Throwable) {
                Log.d(TAG, "fail to get data")
                onError(t.message ?: "unknown error")
            }

            override fun onResponse(
                    call: Call<UserSearchResponse>?,
                    response: Response<UserSearchResponse>
            ) {
                Log.d(TAG, "got a response $response")
                if (response.isSuccessful) {
                    val repos = response.body()?.items ?: emptyList()
                    onSuccess(repos)
                } else {
                    onError(response.errorBody()?.string() ?: "Unknown error")
                }
            }
        }
    )
}

interface WebService {

    /**
     * Get repos ordered by stars.
     */
    @GET("search/users?sort=stars")
    fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): Call<UserSearchResponse>

    //https://api.github.com/search/users?q=tom+repos:%3E42+followers:%3E1000
//    https://api.github.com/search/users/?q=mittalharsh2810in:name,description&page=1&per_page=50

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): WebService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WebService::class.java)
        }
    }

}