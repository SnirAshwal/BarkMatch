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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.barkMatch.adapters.ProfileFeedRecyclerAdapter
import com.barkMatch.databinding.FragmentProfileBinding
import com.barkMatch.models.Model
import com.barkMatch.utils.ImagesUtils
import com.barkMatch.viewsModels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private var profileFeedPostsView: RecyclerView? = null
    private var adapter: ProfileFeedRecyclerAdapter? = null
    private var progressBar: ProgressBar? = null
    private var editProfileBtn: Button? = null
    private var tvUsername: TextView? = null
    private var tvDescription: TextView? = null
    private var imgProfileImage: ImageView? = null
    private var tvPosts: TextView? = null
    private var swipeRefreshLayoutFeed: SwipeRefreshLayout? = null

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        progressBar = binding.pbProfileFeed
        progressBar?.visibility = View.VISIBLE

        val userId = auth.currentUser?.uid ?: ""
        viewModel.posts = Model.instance.getPostsForProfileFeed(userId)
        viewModel.user = Model.instance.getUserDetails(userId)
        profileFeedPostsView = binding.rvProfilePosts
        profileFeedPostsView?.setHasFixedSize(true)
        profileFeedPostsView?.layoutManager = GridLayoutManager(context, 2)
        adapter = ProfileFeedRecyclerAdapter(viewModel.posts?.value)
        profileFeedPostsView?.adapter = adapter

        editProfileBtn = binding.btnEditProfile
        editProfileBtn?.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileToEditProfile(
                    description = viewModel.user?.value?.description ?: "",
                    firstName = viewModel.user?.value?.firstName ?: "",
                    lastName = viewModel.user?.value?.lastName ?: "",
                    phoneNumber = viewModel.user?.value?.phoneNumber ?: "",
                    profileImage = viewModel.user?.value?.profileImage ?: ""
                )
            findNavController().navigate(action)
        }

        tvUsername = binding.tvUsername
        tvDescription = binding.tvDescription
        imgProfileImage = binding.imgProfileImage
        tvPosts = binding.tvPosts

        swipeRefreshLayoutFeed = binding.srlFeed
        swipeRefreshLayoutFeed?.setOnRefreshListener {
            reloadData(userId)
        }

        viewModel.posts?.observe(viewLifecycleOwner) {
            adapter?.posts = it
            adapter?.notifyDataSetChanged()
            progressBar?.visibility = View.GONE
        }

        viewModel.user?.observe(viewLifecycleOwner) { user ->
            tvUsername?.text = user?.firstName + " " + user?.lastName
            tvDescription?.text = user?.description
            tvPosts?.text = viewModel.posts?.value?.size.toString()

            if (user?.profileImage?.isNotEmpty() == true) {
                imgProfileImage?.let {
                    ImagesUtils.loadImage(
                        viewModel.user?.value?.profileImage!!,
                        it
                    )
                }
            }
        }

        Model.instance.profilePostsLoadingState.observe(viewLifecycleOwner) { state ->
            swipeRefreshLayoutFeed?.isRefreshing = state == Model.LoadingState.LOADING
        }

        return view
    }

    private fun reloadData(userId: String) {
        progressBar?.visibility = View.VISIBLE
        Model.instance.refreshPostsForProfile(userId)
        Model.instance.refreshUserDetails(userId)
        progressBar?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}