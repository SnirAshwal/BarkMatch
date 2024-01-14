package com.BarkMatch.homePageFragments

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class CreatePostFragment : Fragment() {

    private var postImageFileName: String? = null
    private var apiService: ApiService? = null
    private var imageView: ImageView? = null
    private var etDescription: EditText? = null
    private var spinnerBreed: Spinner? = null
    private var createPostBtn: Button? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageView?.setImageURI(uri)
            postImageFileName = uri?.let {
                getFileName(requireContext().contentResolver, it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        imageView = view.findViewById(R.id.ivCreatePostImg);
        imageView?.setOnClickListener {
            openFileChooser()
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
                        val dogsInfo: List<DogInfo>? = response.body()
                        if (dogsInfo != null) {
                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                dogsInfo.map { it.name }
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
            // TODO: create post logic

//            val bitmap: Bitmap =
//                BitmapFactory.decodeResource(resources, R.id.ivCreatePostImg)
//            val imageFile =
//                postImageFileName?.let { it1 ->
//                    File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//                        it1
//                    )
//                }
//
//            if (imageFile != null) {
//                saveImageToFile(imageFile, bitmap)
//            }

            val imageFile = ""
            val post = imageFile?.let { it1 ->
                Post(
                    etDescription?.text.toString(),
                    1,
                    false,
                    10,
                    "Pincher",
                    "it1.absolutePath"
                )
            }

            if (post != null) {
                Model.instance.addPost(post) {
                    Navigation.findNavController(it).popBackStack(R.id.FeedFragment, false)
                }
            }
        }

        return view
    }

    private fun openFileChooser() {
        pickImageLauncher.launch("image/*")
    }

    private fun saveImageToFile(file: File, bitmap: Bitmap) {
        try {
            // Create a FileOutputStream to write the bitmap to the file
            val outputStream = FileOutputStream(file)

            // Use the compress method to save the bitmap to the FileOutputStream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            // Flush and close the stream
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("Range")
    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
        var fileName: String? = null

        // Query the content resolver to get the file name
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayName =
                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                fileName = if (displayName != null) {
                    displayName
                } else {
                    null // Handle the case where DISPLAY_NAME is null
                }
            }
        }

        return fileName
    }
}