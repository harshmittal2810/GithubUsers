package com.harsh.cermati.api

import com.google.gson.annotations.SerializedName
import com.harsh.cermati.model.Users

/**
 * Created by Harsh Mittal on 07/04/19.
 */

/**
 * Data class to hold repo responses from searchRepo API calls.
 */
data class UserSearchResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<Users> = emptyList(),
        val nextPage: Int? = null
)
