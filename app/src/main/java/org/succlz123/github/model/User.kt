package org.succlz123.github.model

import com.google.gson.annotations.SerializedName

/**
 * Created by succlz123 on 2019/07/27.
 */
class SearchResult {
    @SerializedName("total_count")
    val totalSize: Int = 0
    val items: MutableList<User>? = null
}

class User {
    @SerializedName("avatar_url")
    val avatarUrl: String? = null
    val login: String? = null
    @SerializedName("html_url")
    val htmlUrl: String? = null
    val score: String? = null
}
