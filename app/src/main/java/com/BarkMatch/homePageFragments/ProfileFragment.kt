package com.BarkMatch.homePageFragments

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
import com.BarkMatch.adapters.ProfileFeedRecyclerAdapter
import com.BarkMatch.databinding.FragmentProfileBinding
import com.BarkMatch.models.Model
import com.BarkMatch.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class ProfileFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private var profileFeedPostsView: RecyclerView? = null
    private var posts: List<Post>? = null
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

        progressBar = binding.pbProfileFeed
        progressBar?.visibility = View.VISIBLE

        val userId = auth.currentUser?.uid ?: ""
        Model.instance.getAllPostsByUserId(userId) { posts ->
            getPosts(posts)
        }

        profileFeedPostsView = binding.rvProfilePosts
        profileFeedPostsView?.setHasFixedSize(true)
        profileFeedPostsView?.layoutManager = GridLayoutManager(context, 2)
        adapter = ProfileFeedRecyclerAdapter(posts)
        profileFeedPostsView?.adapter = adapter

        editProfileBtn = binding.btnEditProfile
        editProfileBtn?.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileToActivityEditProfile(
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
            Model.instance.getAllPostsByUserId(userId) { posts ->
                getPosts(posts)
            }

            swipeRefreshLayoutFeed?.isRefreshing = false
        }

        return view;
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onPostClicked(post: Post?)
    }

    override fun onResume() {
        super.onResume()

        progressBar?.visibility = View.VISIBLE

        auth.currentUser?.uid?.let {
            Model.instance.getAllPostsByUserId(it) { posts ->
                getPosts(posts)
            }

            initUserDetails(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getPosts(posts: List<Post>) {
        this.posts = posts
        adapter?.posts = posts
        adapter?.notifyDataSetChanged()

        progressBar?.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun initUserDetails(userId: String) {
        Model.instance.getUserDetails(userId) { user, postCount ->
            tvUsername?.text = user.firstName + " " + user.lastName
            tvDescription?.text = user.description
            tvPosts?.text = postCount.toString()

            if (user.profileImage.isNotEmpty()) {
                Picasso.get()
                    .load(user.profileImage)
                    .transform(RoundedCornersTransformation(50, 0)) // Make the image corners round
                    .fit()
                    .centerCrop()
                    .into(imgProfileImage)
            }

            userFirstName = user.firstName
            userLastName = user.lastName
            userDescription = user.description
            userPhoneNumber = user.phoneNumber
            userProfileImage = user.profileImage
        }
    }
}