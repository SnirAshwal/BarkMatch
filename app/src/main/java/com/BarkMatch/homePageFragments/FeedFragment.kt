package com.BarkMatch.homePageFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.BarkMatch.adapters.FeedRecyclerAdapter
import com.BarkMatch.adapters.ProfileFeedRecyclerAdapter
import com.BarkMatch.databinding.FragmentFeedBinding
import com.BarkMatch.models.Model
import com.BarkMatch.models.Post

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

        Model.instance.getInitialFeedPosts { posts ->
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
                        Model.instance.loadMorePostsForFeed { posts ->
                            addPosts(posts)
                        }
                    }
                }
            }
        })

        swipeRefreshLayoutFeed = binding.srlFeed
        swipeRefreshLayoutFeed?.setOnRefreshListener {
            Model.instance.getInitialFeedPosts { posts ->
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
        this.posts?.addAll(posts)
        adapter?.posts = this.posts

        adapter?.notifyDataSetChanged()

        progressBar?.visibility = View.GONE
    }

    private fun getPosts(posts: MutableList<Post>) {
        this.posts = posts
        adapter?.posts = posts

        adapter?.notifyDataSetChanged()

        progressBar?.visibility = View.GONE
    }
}