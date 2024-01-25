package com.barkMatch.homePageFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.barkMatch.adapters.FeedRecyclerAdapter
import com.barkMatch.databinding.FragmentFeedBinding
import com.barkMatch.models.Model
import com.barkMatch.models.Post

class FeedFragment : Fragment() {

    private var feedPostsView: RecyclerView? = null
    private var posts: MutableList<Post>? = null
    private var adapter: FeedRecyclerAdapter? = null
    private var progressBar: ProgressBar? = null
    private var swipeRefreshLayoutFeed: SwipeRefreshLayout? = null

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root

        progressBar = binding.pbFeed
        progressBar?.visibility = View.VISIBLE

        Model.instance.getPostsForFeed { posts ->
            getPosts(posts)
        }

        feedPostsView = binding.rvFeedPosts
        feedPostsView?.setHasFixedSize(true)
        feedPostsView?.layoutManager = LinearLayoutManager(context)
        adapter = context?.let { FeedRecyclerAdapter(posts, it) }
        feedPostsView?.adapter = adapter
        feedPostsView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!FeedRecyclerAdapter.isLoading && !FeedRecyclerAdapter.isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= FeedRecyclerAdapter.FEED_PAGE_SIZE
                    ) {
                        // Load more posts
                        Model.instance.getPostsForFeed { posts ->
                            addPosts(posts)
                        }
                    }
                }
            }
        })

        swipeRefreshLayoutFeed = binding.srlFeed
        swipeRefreshLayoutFeed?.setOnRefreshListener {
            FeedRecyclerAdapter.lastVisiblePost = null // To get the first items
            Model.instance.getPostsForFeed { posts ->
                getPosts(posts)
            }

            swipeRefreshLayoutFeed?.isRefreshing = false
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun addPosts(posts: MutableList<Post>) {
        val startPosition = this.posts?.size ?: 0
        this.posts?.addAll(posts)
        adapter?.notifyItemRangeInserted(startPosition, posts.size)
        progressBar?.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPosts(posts: MutableList<Post>) {
        this.posts = posts
        adapter?.posts = posts
        adapter?.notifyDataSetChanged()
        view?.visibility = View.VISIBLE
        progressBar?.visibility = View.GONE
    }
}