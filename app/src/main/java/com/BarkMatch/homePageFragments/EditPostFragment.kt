package com.BarkMatch.homePageFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.BarkMatch.api.ApiService
import com.BarkMatch.api.DogInfo
import com.BarkMatch.api.RetrofitClient
import com.BarkMatch.databinding.FragmentEditPostBinding
import com.BarkMatch.models.Model
import com.BarkMatch.utils.ImagesUtils
import com.BarkMatch.utils.SnackbarUtils
import com.BarkMatch.utils.Validations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPostFragment : Fragment() {

    private var apiService: ApiService? = null
    private var btnEditPostBack: ImageButton? = null
    private var ivEditPostImg: ImageView? = null
    private var etEditPostDescription: EditText? = null
    private var spinnerEditPostBreed: Spinner? = null
    private var btnEditPost: Button? = null
    private var pbEditPost: ProgressBar? = null
    private var dogsInfo: List<DogInfo>? = null

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        val view = binding.root

        val args: EditPostFragmentArgs by navArgs()
        val postId = args.postId
        val breed = args.breed
        val description = args.description
        val imageUrl = args.imageUrl
        var breedIndex = 0

        pbEditPost = binding.pbEditPost

        btnEditPostBack = binding.btnEditPostBack
        btnEditPostBack?.setOnClickListener {
            findNavController().popBackStack()
        }

        ivEditPostImg = binding.ivEditPostImg
        etEditPostDescription = binding.etEditPostDescription
        spinnerEditPostBreed = binding.spinnerEditPostBreed

        apiService = RetrofitClient.getClient().create(ApiService::class.java)
        if (apiService != null) {
            val call: Call<List<DogInfo>> = apiService!!.getBreeds()
            call.enqueue(object : Callback<List<DogInfo>> {
                override fun onResponse(
                    call: Call<List<DogInfo>>,
                    response: Response<List<DogInfo>>
                ) {
                    if (response.isSuccessful) {
                        dogsInfo = response.body()
                        if (dogsInfo != null) {
                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                dogsInfo!!.map { it.name }
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerEditPostBreed?.adapter = adapter

                            dogsInfo!!.forEachIndexed { index, dogInfo ->
                                if (dogInfo.name == breed) {
                                    breedIndex = index
                                    return@forEachIndexed
                                }
                            }

                            spinnerEditPostBreed?.setSelection(breedIndex)
                        }
                    } else {
                        Log.e("API Error", "Failed to get posts. Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<DogInfo>>, t: Throwable) {
                    Log.e("API Error", "Network error: ${t.message}")
                }
            })
        }

        etEditPostDescription?.setText(description)
        ImagesUtils.loadImage(imageUrl, ivEditPostImg!!)

        btnEditPost = binding.btnEditPost
        btnEditPost?.setOnClickListener {
            // Validations
            if (Validations.isFieldEmpty(etEditPostDescription?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "Description is required")
                return@setOnClickListener
            }

            if (Validations.isFieldEmpty(spinnerEditPostBreed?.selectedItem.toString())) {
                SnackbarUtils.showSnackbar(view, "Breed is required")
                return@setOnClickListener
            }

            val breedId =
                dogsInfo?.filter { info -> info.name.equals(spinnerEditPostBreed?.selectedItem.toString()) }
                    ?.get(0)?.id

            pbEditPost?.visibility = View.VISIBLE

            Model.instance.editPost(
                postId,
                spinnerEditPostBreed?.selectedItem.toString(),
                breedId ?: 0,
                etEditPostDescription?.text.toString()
            ) { isSuccess ->
                if (isSuccess) {
                    // Going back to post profile
                    findNavController().popBackStack()
                } else {
                    SnackbarUtils.showSnackbar(view, "Failed to update post")
                    pbEditPost?.visibility = View.GONE
                }
            }

        }

        return view
    }
}