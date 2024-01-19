package com.BarkMatch.homePageFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.BarkMatch.BreedInfoActivity
import com.BarkMatch.R
import com.BarkMatch.databinding.FragmentProfilePostBinding
import com.BarkMatch.models.Model
import com.BarkMatch.utils.DialogUtils
import com.BarkMatch.utils.ImagesUtils

class ProfilePostFragment : Fragment() {

    private var btnProfilePostBack: ImageView? = null
    private var tvPostDescription: TextView? = null
    private var ivPostImage: ImageView? = null
    private var imPostBreedInfo: ImageView? = null
    private var tvPostBreed: TextView? = null
    private var tvPostUsername: TextView? = null
    private var imPostContactInfo: ImageView? = null

    private var _binding: FragmentProfilePostBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePostBinding.inflate(inflater, container, false)
        val view = binding.root

        btnProfilePostBack = binding.btnProfilePostBack
        btnProfilePostBack?.setOnClickListener {
            findNavController().navigate(R.id.ProfileFragment)
        }

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
                    requireContext(), """
                        Name: $fullName
                        
                        Phone number: $phoneNumber
                    """.trimIndent()
                )
            }
        }

        containerForPostLayout.addView(postLayout)

        return view
    }
}