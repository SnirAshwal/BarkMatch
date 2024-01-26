package com.barkMatch.homePageFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.barkMatch.adapters.ProfileFeedRecyclerAdapter
import com.barkMatch.adapters.ProfileFeedRecyclerAdapter.Companion.PROFILE_PAGE_SIZE
import com.barkMatch.databinding.FragmentProfileBinding
import com.barkMatch.models.Model
import com.barkMatch.models.Post
import com.barkMatch.utils.ImagesUtils
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private var profileFeedPostsView: RecyclerView? = null
    private var posts: MutableList<Post>? = null
    private var adapter: ProfileFeedRecyclerAdapter? = null
    private var progressBar: ProgressBar? = null
    private var editProfileBtn: Button? = null
    private var tvUsername: TextView? = null
    private var tvDescription: TextView? = null
    private var imgProfileImage: ImageView? = null
    private var tvPosts: TextView? = null
    private var swipeRefreshLayoutFeed: SwipeRefreshLayout? = null

    private var userFirstName: String = ""
    private var userLastName: String = ""
    private var userDescription: String = ""
    private var userPhoneNumber: String = ""
    private var userProfileImage: String = ""

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        view.visibility = View.GONE
        progressBar = binding.pbProfileFeed
        progressBar?.visibility = View.VISIBLE

        val userId = auth.currentUser?.uid ?: ""
        ProfileFeedRecyclerAdapter.lastVisiblePost = null // To get the first items
        Model.instance.getPostsForProfileFeed(userId) { posts ->
            getPosts(posts)
        }

        profileFeedPostsView = binding.rvProfilePosts
        profileFeedPostsView?.setHasFixedSize(true)
        profileFeedPostsView?.layoutManager = GridLayoutManager(context, 2)
        adapter = ProfileFeedRecyclerAdapter(posts)
        profileFeedPostsView?.adapter = adapter
        profileFeedPostsView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!ProfileFeedRecyclerAdapter.isLoading && !ProfileFeedRecyclerAdapter.isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PROFILE_PAGE_SIZE
                    ) {
                        // Load more posts
                        Model.instance.getPostsForProfileFeed(userId) { posts ->
                            addPosts(posts)
                        }
                    }
                }
            }
        })

        editProfileBtn = binding.btnEditProfile
        editProfileBtn?.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileToEditProfile(
                    description = userDescription,
                    firstName = userFirstName,
                    lastName = userLastName,
                    phoneNumber = userPhoneNumber,
                    profileImage = userProfileImage
                )
            findNavController().navigate(action)
        }

        tvUsername = binding.tvUsername
        tvDescription = binding.tvDescription
        imgProfileImage = binding.imgProfileImage
        tvPosts = binding.tvPosts

        initUserDetails(userId)

        swipeRefreshLayoutFeed = binding.srlFeed
        swipeRefreshLayoutFeed?.setOnRefreshListener {
            ProfileFeedRecyclerAdapter.lastVisiblePost = null // To get the first items
            Model.instance.getPostsForProfileFeed(userId) { posts ->
                getPosts(posts)
            }

            swipeRefreshLayoutFeed?.isRefreshing = false
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        auth.currentUser?.uid?.let {
            initUserDetails(it)
        }
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

    @SuppressLint("SetTextI18n")
    private fun initUserDetails(userId: String) {
        Model.instance.getUserDetails(userId) { user, postCount ->
            tvUsername?.text = user.firstName + " " + user.lastName
            tvDescription?.text = user.description
            tvPosts?.text = postCount.toString()

            if (user.profileImage.isNotEmpty()) {
                imgProfileImage?.let { ImagesUtils.loadImage(user.profileImage, it) }
            }

            userFirstName = user.firstName
            userLastName = user.lastName
            userDescription = user.description
            userPhoneNumber = user.phoneNumber
            userProfileImage = user.profileImage
        }
    }
}