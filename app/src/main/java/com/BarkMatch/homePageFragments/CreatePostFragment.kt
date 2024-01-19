package com.BarkMatch.homePageFragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.BarkMatch.R
import com.BarkMatch.api.ApiService
import com.BarkMatch.api.DogInfo
import com.BarkMatch.api.RetrofitClient
import com.BarkMatch.models.Model
import com.BarkMatch.models.Post
import com.BarkMatch.utils.ImagesUtils
import com.BarkMatch.utils.SnackbarUtils
import com.BarkMatch.utils.Validations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePostFragment : Fragment() {

    private var apiService: ApiService? = null
    private var imageView: ImageView? = null
    private var etDescription: EditText? = null
    private var spinnerBreed: Spinner? = null
    private var createPostBtn: Button? = null
    private var selectedImageUri: Uri? = null
    private var dogsInfo: List<DogInfo>? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageView?.let { ImagesUtils.loadImage(uri, it) }
            selectedImageUri = uri
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        imageView = view.findViewById(R.id.ivCreatePostImg);
        imageView?.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        etDescription = view.findViewById(R.id.etCreatePostDescription)

        spinnerBreed = view.findViewById(R.id.spinnerCreatePostBreed)
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
                            spinnerBreed?.adapter = adapter
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

        createPostBtn = view.findViewById(R.id.btnCreatePost)
        createPostBtn?.setOnClickListener {
            // Validation
            if (selectedImageUri == null) {
                SnackbarUtils.showSnackbar(view, "Image is required")
                return@setOnClickListener
            }

            if (Validations.isFieldEmpty(etDescription?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "Description is required")
                return@setOnClickListener
            }

            if (Validations.isFieldEmpty(spinnerBreed?.selectedItem.toString())) {
                SnackbarUtils.showSnackbar(view, "Breed is required")
                return@setOnClickListener
            }

            // Creating the post
            val breedId =
                dogsInfo?.filter { info -> info.name.equals(spinnerBreed?.selectedItem.toString()) }
                    ?.get(0)?.id

            val post = Post(
                etDescription?.text.toString(),
                breedId ?: 0,
                spinnerBreed?.selectedItem.toString()
            )

            selectedImageUri?.let { imageUri ->
                Model.instance.createPost(post, imageUri) {
                    Navigation.findNavController(it).popBackStack(R.id.FeedFragment, false)
                }
            }
        }

        return view
    }
}