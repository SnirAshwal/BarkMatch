package com.BarkMatch.homePageFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.BarkMatch.adapters.FeedRecyclerAdapter
import com.BarkMatch.databinding.FragmentFeedBinding
import com.BarkMatch.models.Model
import com.BarkMatch.models.Post

class FeedFragment : Fragment() {

    private var feedPostsView: RecyclerView? = null
    private var posts: List<Post>? = null
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

        Model.instance.getAllPosts { posts ->
            getPosts(posts)
        }

        feedPostsView = binding.rvFeedPosts
        feedPostsView?.setHasFixedSize(true)
        feedPostsView?.layoutManager = LinearLayoutManager(context)
        adapter = context?.let { FeedRecyclerAdapter(posts, it) }
        feedPostsView?.adapter = adapter

        swipeRefreshLayoutFeed = binding.srlFeed
        swipeRefreshLayoutFeed?.setOnRefreshListener {
            Model.instance.getAllPosts { posts ->
                getPosts(posts)
            }

            swipeRefreshLayoutFeed?.isRefreshing = false
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        progressBar?.visibility = View.VISIBLE

        Model.instance.getAllPosts { posts ->
            getPosts(posts)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    fun getPosts(posts: List<Post>) {
        this.posts = posts
        adapter?.posts = posts
        adapter?.notifyDataSetChanged()

        progressBar?.visibility = View.GONE
    }
}