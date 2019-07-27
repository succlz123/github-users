package org.succlz123.github.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.succlz123.github.model.SearchResult
import org.succlz123.github.model.User
import org.succlz123.github.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by succlz123 on 2019/07/27.
 */
class UserViewModel : ViewModel() {
    private var user = MutableLiveData<UserResponse>()
    private var page = 1
    private var currentSize = 0
    private var totalSize = 0

    var isLoading = false
    val hashMore: Boolean
        get() {
            return currentSize < totalSize
        }

    fun refresh(name: String): MutableLiveData<UserResponse>? {
        return loadUsers(name, false)
    }

    fun loadMore(name: String): MutableLiveData<UserResponse>? {
        return loadUsers(name, true)
    }

    private fun loadUsers(name: String, isLoadMore: Boolean): MutableLiveData<UserResponse>? {
        if (isLoading) {
            return null
        }
        isLoading = true
        if (isLoadMore) {
            page++;
        } else {
            page = 1
        }
        user.value = null
        val callback = object : Callback<SearchResult> {
            override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                val body = response.body()
                val msg = UserResponse()
                if (response.isSuccessful && body != null) {
                    totalSize = body.totalSize
                    body.items?.let { items ->
                        if (page != 1) {
                            currentSize += items.size
                            msg.items = items
                        } else {
                            currentSize = items.size
                            msg.items = items
                        }
                    }
                } else {
                    if (isLoadMore) {
                        page--
                    }
                    val errorMsg = response.errorBody()?.string()
                    msg.errorMessage = errorMsg ?: "Unable to get HTTP response"
                }
                msg.needClear = page == 1
                user.value = msg
                isLoading = false
            }

            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                if (isLoadMore) {
                    page--
                }
                val msg = UserResponse()
                msg.errorMessage = t.message
                user.value = msg
                isLoading = false
            }
        }
        UserRepository.instance.searchUsers(callback, name, page)
        return user
    }

    class UserResponse {
        var items: MutableList<User>? = null
        var errorMessage: String? = null
        var needClear: Boolean = false
    }
}
