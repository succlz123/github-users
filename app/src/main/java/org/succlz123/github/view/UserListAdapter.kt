package org.succlz123.github.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.succlz123.github.R
import org.succlz123.github.model.User

/**
 * Created by succlz123 on 2019/07/27.
 */
class UserListAdapter : RecyclerView.Adapter<UserListAdapter.ListViewHolder>(), View.OnClickListener {
    private var userlist = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder.create(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = userlist[holder.adapterPosition]
        holder.userName.text = user.login
        val score = if (TextUtils.isEmpty(user.score)) "" else user.score
        holder.userUrlScore.text = "url: ${user.htmlUrl}  score: $score"
        val context = holder.itemView.context
        if (!TextUtils.isEmpty(user.avatarUrl)) {
            Picasso.with(context).load(user.avatarUrl).into(holder.userAvatar)
        } else {
            Picasso.with(context).load(DEFAULT_AVATAR).into(holder.userAvatar)
        }
        holder.itemView.tag = user
        holder.itemView.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    fun clear() {
        userlist.clear()
    }

    fun addList(list: MutableList<User>) {
        userlist.addAll(list)
        notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        v?.let {
            val context = it.context
            val obj = it.tag
            if (context != null && obj is User) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(obj.htmlUrl))
                context.startActivity(browserIntent)
            }
        }
    }

    class ListViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        var userName: TextView = itemView.findViewById(R.id.user_name)
        var userUrlScore: TextView = itemView.findViewById(R.id.url_score)
        var userAvatar: ImageView = itemView.findViewById(R.id.user_avatar)

        companion object {

            fun create(parent: ViewGroup): ListViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_user, parent, false)
                return ListViewHolder(view)
            }
        }
    }

    companion object {
        private const val DEFAULT_AVATAR = "https://github.githubassets.com/images/modules/open_graph/github-logo.png"
    }
}
