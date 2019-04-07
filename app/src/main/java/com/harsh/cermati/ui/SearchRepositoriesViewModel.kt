/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harsh.cermati.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.harsh.cermati.data.GithubRepository
import com.harsh.cermati.model.Users
import com.harsh.cermati.model.UserSearchResult


/**
 * ViewModel for the [MainActivity] screen.
 * The ViewModel works with the [GithubRepository] to get the data.
 */
class SearchRepositoriesViewModel(private val repository: GithubRepository) : ViewModel() {

    private val queryLiveData = MutableLiveData<String>()
    private val userResult: LiveData<UserSearchResult> = Transformations.map(queryLiveData) {
        repository.search(it)
    }

    val repos: LiveData<List<Users>> = Transformations.switchMap(
            userResult
    ) { it.data }
    val networkErrors: LiveData<String> = Transformations.switchMap(
            userResult
    ) { it.networkErrors }

    /**
     * Search a repository based on a query string.
     */
    fun searchRepo(queryString: String) {
        if (queryString.isNotBlank())
            queryLiveData.postValue(queryString)
    }

    fun listScrolled() {
        val immutableQuery = lastQueryValue()
        if (immutableQuery != null) {
            repository.requestMore(immutableQuery)
        }
    }

    /**
     * Get the last query value.
     */
    fun lastQueryValue(): String? = queryLiveData.value
}