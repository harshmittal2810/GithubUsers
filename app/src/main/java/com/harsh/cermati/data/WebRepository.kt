package com.harsh.cermati.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.harsh.cermati.api.WebService
import com.harsh.cermati.api.searchRepos
import com.harsh.cermati.db.UserLocalCache
import com.harsh.cermati.model.UserSearchResult

/**
 * Created by Harsh Mittal on 07/04/19.
 */

/**
 * Repository class that works with local and remote data sources.
 */
class GithubRepository(
    private val service: WebService,
    private val cache: UserLocalCache
) {

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

    // LiveData of network errors.
    private val networkErrors = MutableLiveData<String>()

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    /**
     * Search repositories whose names match the query.
     */
    fun search(query: String): UserSearchResult {
        Log.d("WebRepository", "New query: $query")
        lastRequestedPage = 1
        requestAndSaveData(query)

        // Get data from the local cache
        val data = cache.reposByName(query)

        return UserSearchResult(data, networkErrors)
    }

    fun requestMore(query: String) {
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        searchRepos(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, { repos ->
            cache.insert(repos) {
                lastRequestedPage++
                isRequestInProgress = false
            }
        }, { error ->
            networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }
}