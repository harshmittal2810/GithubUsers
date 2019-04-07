package com.harsh.cermati.db

import android.util.Log
import androidx.lifecycle.LiveData
import com.harsh.cermati.model.Users
import java.util.concurrent.Executor

/**
 * Created by Harsh Mittal on 07/04/19.
 */


/**
 * Class that handles the DAO local data source. This ensures that methods are triggered on the
 * correct executor.
 */
class UserLocalCache(
        private val userDao: UserDao,
        private val ioExecutor: Executor
) {

    /**
     * Insert a list of users in the database, on a background thread.
     */
    fun insert(users: List<Users>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            Log.d("UserLocalCache", "inserting ${users.size} users")
            userDao.insert(users)
            insertFinished()
        }
    }

    /**
     * Request a LiveData<List<Users>> from the Dao, based on a repo name. If the name contains
     * multiple words separated by spaces, then we're emulating the GitHub API behavior and allow
     * any characters between the words.
     * @param name repository name
     */
    fun reposByName(name: String): LiveData<List<Users>> {
        // appending '%' so we can allow other characters to be before and after the query string
        val query = "%${name.replace(' ', '%')}%"
        return userDao.reposByName(query)
    }
}