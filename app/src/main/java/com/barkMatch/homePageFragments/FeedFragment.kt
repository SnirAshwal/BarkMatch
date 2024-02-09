package com.barkMatch.homePageFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.barkMatch.adapters.FeedRecyclerAdapter
import com.barkMatch.databinding.FragmentFeedBinding
import com.barkMatch.models.Model
import com.barkMatch.viewsModels.FeedViewModel

class FeedFragment : Fragment() {

    private var feedPostsView: RecyclerView? = null
    private var adapter: FeedRecyclerAdapter? = null
    private var progressBar: ProgressBar? = null
    private var swipeRefreshLayoutFeed: SwipeRefreshLayout? = null

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FeedViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[FeedViewModel::class.java]

        progressBar = binding.pbFeed
        progressBar?.visibility = View.VISIBLE

        viewModel.posts = Model.instance.getPostsForFeed()

        feedPostsView = binding.rvFeedPosts
        feedPostsView?.setHasFixedSize(true)
        feedPostsView?.layoutManager = LinearLayoutManager(context)
        adapter = context?.let { FeedRecyclerAdapter(viewModel.posts?.value, it) }
        feedPostsView?.adapter = adapter

        swipeRefreshLayoutFeed = binding.srlFeed
        swipeRefreshLayoutFeed?.setOnRefreshListener {
            reloadData()
        }

        viewModel.posts?.observe(viewLifecycleOwner) {
            adapter?.posts = it
            adapter?.notifyDataSetChanged()
            progressBar?.visibility = View.GONE
        }

        Model.instance.feedPostsLoadingState.observe(viewLifecycleOwner) { state ->
            swipeRefreshLayoutFeed?.isRefreshing = state == Model.LoadingState.LOADING
        }

        return view
    }

    private fun reloadData() {
        progressBar?.visibility = View.VISIBLE
        Model.instance.refreshPostsForFeed()
        progressBar?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}