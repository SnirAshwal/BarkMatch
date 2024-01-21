package com.barkMatch.homePageFragments

import android.content.Intent
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
import androidx.navigation.fragment.navArgs
import com.barkMatch.BreedInfoActivity
import com.barkMatch.R
import com.barkMatch.databinding.FragmentProfilePostBinding
import com.barkMatch.models.Model
import com.barkMatch.models.Post
import com.barkMatch.utils.DialogUtils
import com.barkMatch.utils.DialogUtils.ButtonType
import com.barkMatch.utils.ImagesUtils
import com.barkMatch.utils.SnackbarUtils

class ProfilePostFragment : Fragment() {

    private var btnProfilePostBack: ImageView? = null
    private var tvPostDescription: TextView? = null
    private var ivPostImage: ImageView? = null
    private var imPostBreedInfo: ImageView? = null
    private var tvPostBreed: TextView? = null
    private var tvPostUsername: TextView? = null
    private var imPostContactInfo: ImageView? = null
    private var btnDeletePost: Button? = null
    private var btnEditPost: Button? = null
    private var pbProfilePost: ProgressBar? = null
    private var post: Post? = null

    private var _binding: FragmentProfilePostBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePostBinding.inflate(inflater, container, false)
        val view = binding.root
        view.visibility = View.GONE

        btnProfilePostBack = binding.btnProfilePostBack
        btnProfilePostBack?.setOnClickListener {
            findNavController().navigate(R.id.ProfileFragment)
        }

        pbProfilePost = binding.pbProfilePost
        pbProfilePost?.visibility = View.VISIBLE

        val containerForPostLayout = binding.containerForPostLayout
        val postLayout = inflater.inflate(R.layout.post_layout, containerForPostLayout, false)
        tvPostUsername = postLayout.findViewById(R.id.tvPostUsername)
        tvPostDescription = postLayout.findViewById(R.id.tvPostDescription)
        ivPostImage = postLayout.findViewById(R.id.ivPostImage)
        imPostBreedInfo = postLayout.findViewById(R.id.imPostBreedInfo)
        tvPostBreed = postLayout.findViewById(R.id.tvPostBreed)
        imPostContactInfo = postLayout.findViewById(R.id.imPostContactInfo)

        val args: ProfilePostFragmentArgs by navArgs()
        val postId = args.postId ?: ""

        Model.instance.getEditPostDetails(postId) { post, fullName, phoneNumber ->
            // Loading details
            this.post = post
            tvPostUsername?.text = fullName
            tvPostDescription?.text = post.description
            tvPostBreed?.text = post.breedName

            // Loading image
            ImagesUtils.loadImage(post.image, ivPostImage!!)

            // Init buttons
            imPostBreedInfo?.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("breedId", post.breedId.toString())
                val intent = Intent(context, BreedInfoActivity::class.java)
                intent.putExtras(bundle)
                context?.startActivity(intent)
            }

            imPostContactInfo?.setOnClickListener {
                DialogUtils.openDialog(
                    requireContext(), "Contact details", """
                        Name: $fullName

                        Phone number: $phoneNumber
                    """.trimIndent()
                )
            }

            pbProfilePost?.visibility = View.GONE
            view.visibility = View.VISIBLE
        }

        containerForPostLayout.addView(postLayout)

        btnDeletePost = binding.btnDeletePost
        btnDeletePost?.setOnClickListener {
            val buttonActions = arrayOf(
                DialogUtils.ButtonAction("Confirm", ButtonType.POSITIVE) { _ ->
                    pbProfilePost?.visibility = View.VISIBLE

                    Model.instance.deletePost(postId) { isSuccess ->
                        if (isSuccess) {
                            // Going back to profile
                            findNavController().popBackStack()
                        } else {
                            SnackbarUtils.showSnackbar(view, "Failed to delete post")
                            pbProfilePost?.visibility = View.GONE
                        }
                    }
                }
            )

            DialogUtils.openDialog(
                requireContext(),
                "Delete post",
                "Are you sure you want to delete this post?",
                buttonActions
            )
        }

        btnEditPost = binding.btnEditPost
        btnEditPost?.setOnClickListener {
            val action = ProfilePostFragmentDirections.actionProfilePostToEditPost(
                postId = postId,
                breed = post?.breedName ?: "",
                description = post?.description ?: "",
                imageUrl = post?.image ?: ""
            )
            findNavController().navigate(action)
        }

        return view
    }
}