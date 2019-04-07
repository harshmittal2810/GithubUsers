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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harsh.cermati.R
import com.harsh.cermati.model.Users

/**
 * View Holder for a [Users] RecyclerView list item.
 */
class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = view.findViewById(R.id.repo_name)
    private val image: AppCompatImageView = view.findViewById(R.id.userImage)

    private var users: Users? = null
    private var mCtx: Context = view.context

    init {
        view.setOnClickListener {
            users?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(users: Users?) {
        if (users == null) {
            val resources = itemView.resources
            name.text = resources.getString(R.string.loading)
            image.setImageResource(R.mipmap.ic_launcher)
        } else {
            showRepoData(users)
        }
    }

    private fun showRepoData(users: Users) {
        this.users = users
        name.text = users.name

        Glide.with(mCtx).load(users.imageUrl).into(image)

    }

    companion object {
        fun create(parent: ViewGroup): UserViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_row, parent, false)
            return UserViewHolder(view)
        }
    }
}