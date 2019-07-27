package org.succlz123.github.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.succlz123.github.R
import org.succlz123.github.view.UserListAdapter
import org.succlz123.github.viewmodel.UserViewModel

/**
 * Created by succlz123 on 2019/07/27.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var root: View
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var viewModel: UserViewModel
    private lateinit var observer: Observer<UserViewModel.UserResponse>
    private lateinit var adapter: UserListAdapter
    private lateinit var layoutManger: LinearLayoutManager

    private var searchName: String = DEFAULT_SEARCH_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        root = findViewById(R.id.root)
        searchView = findViewById(R.id.search_view)
        recyclerView = findViewById(R.id.recycler_view)
        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout)

        layoutManger = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManger
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter = UserListAdapter()
        recyclerView.adapter = adapter

        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(str: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(str: String?): Boolean {
                if (str == searchName) {
                    return false
                }
                if (str != null && str != "") {
                    searchName = str
                } else {
                    searchName = DEFAULT_SEARCH_NAME
                }
                refreshData()
                return false
            }
        })
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState != SCROLL_STATE_IDLE) {
                    return
                }
                if (!viewModel.hashMore && !viewModel.isLoading) {
                    return
                }
                if (isVisBottom(layoutManger)) {
                    viewModel.loadMore(searchName)?.observe(this@MainActivity, observer)
                }
            }
        })
        observer = Observer { user ->
            user?.let {
                val msg = user.errorMessage
                if (msg != null) {
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                } else {
                    val list = user.items
                    if (list != null) {
                        if (user.needClear) {
                            adapter.clear()
                            swipeRefreshLayout.isRefreshing = false
                        }
                        adapter.addList(list)
                    }
                }
            }
        }
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
        refreshData()
    }

    fun isVisBottom(lm: LinearLayoutManager?): Boolean {
        if (lm == null) {
            return false
        }
        val lastVisibleItemPosition = lm.findLastVisibleItemPosition()
        val visibleItemCount = lm.childCount
        val totalItemCount = lm.itemCount
        return visibleItemCount > 0 && lastVisibleItemPosition >= totalItemCount - 2
    }

    private fun refreshData() {
        val data = viewModel.refresh(searchName)
        if (data != null) {
            data.observe(this@MainActivity, observer)
        }
    }

    override fun onResume() {
        super.onResume()
        // prevent searchView from getting the focus
        root.requestFocus()
    }

    companion object {
        private const val DEFAULT_SEARCH_NAME = "swift"
    }
}
