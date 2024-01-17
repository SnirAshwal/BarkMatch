package com.BarkMatch

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.BarkMatch.api.ApiService
import com.BarkMatch.api.DogImageInfo
import com.BarkMatch.api.DogInfo
import com.BarkMatch.api.RetrofitClient
import com.BarkMatch.databinding.ActivityBreedInfoBinding
import com.BarkMatch.databinding.FragmentFeedBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.CornerTreatment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BreedInfoActivity : AppCompatActivity() {

    private var btnBreedInfoBack: ImageView? = null
    private var apiService: ApiService? = null
    private var ivBreedImage: ShapeableImageView? = null
    private var tvBreedName: TextView? = null
    private var tvBreedGroup: TextView? = null
    private var tvBreedTemperament: TextView? = null
    private var tvBreedBredFor: TextView? = null
    private var tvBreedOrigin: TextView? = null
    private var tvBreedLifSpan: TextView? = null
    private var tvBreedHeight: TextView? = null
    private var tvBreedWeight: TextView? = null
    private var progressBar: ProgressBar? = null

    private lateinit var _binding: ActivityBreedInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBreedInfoBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        progressBar = _binding.pbDogInfo
        progressBar?.visibility = View.VISIBLE

        ivBreedImage = _binding.ivBreedImage
        tvBreedName = _binding.tvBreedName
        tvBreedGroup = _binding.tvBreedGroup
        tvBreedTemperament = _binding.tvBreedTemperament
        tvBreedBredFor = _binding.tvBreedBredFor
        tvBreedOrigin = _binding.tvBreedOrigin
        tvBreedLifSpan = _binding.tvBreedLifSpan
        tvBreedHeight = _binding.tvBreedHeight
        tvBreedWeight = _binding.tvBreedWeight

        val extras = intent.extras
        if (extras != null) {
            val breedId = extras.getString("breedId")
            if (breedId != null) {
                apiService = RetrofitClient.getClient().create(ApiService::class.java)
                if (apiService != null) {
                    val call: Call<DogInfo> = apiService!!.getBreedInfo(breedId.toInt())
                    call.enqueue(object : Callback<DogInfo> {
                        @SuppressLint("SetTextI18n")
                        override fun onResponse(
                            call: Call<DogInfo>,
                            response: Response<DogInfo>
                        ) {
                            if (response.isSuccessful) {
                                val dogsInfo: DogInfo? = response.body()
                                if (dogsInfo != null) {
                                    tvBreedName?.text =
                                        tvBreedName?.text.toString() + " " + dogsInfo.name
                                    tvBreedGroup?.text =
                                        tvBreedGroup?.text.toString() + " " + (dogsInfo.breedGroup
                                            ?: " -")
                                    tvBreedTemperament?.text =
                                        tvBreedTemperament?.text.toString() + " " + (dogsInfo.temperament
                                            ?: " -")
                                    tvBreedBredFor?.text =
                                        tvBreedBredFor?.text.toString() + " " + (dogsInfo.bredFor
                                            ?: " -")
                                    tvBreedOrigin?.text =
                                        tvBreedOrigin?.text.toString() + " " + (dogsInfo.origin
                                            ?: " -")
                                    tvBreedLifSpan?.text =
                                        tvBreedLifSpan?.text.toString() + " " + (dogsInfo.lifeSpan
                                            ?: " -")
                                    tvBreedHeight?.text =
                                        tvBreedHeight?.text.toString() + " " + dogsInfo.height?.metric + " cm / " + dogsInfo.height?.imperial + " in"
                                    tvBreedWeight?.text =
                                        tvBreedWeight?.text.toString() + " " + dogsInfo.weight?.metric + " kg / " + dogsInfo.weight?.imperial + " lbs"

                                    if (dogsInfo.referenceImageId != null)
                                        getBreedImageInfo(dogsInfo.referenceImageId)

                                    progressBar?.visibility = View.GONE
                                }
                            } else {
                                Log.e(
                                    "API Error",
                                    "Failed to get dog info. Code: ${response.code()}"
                                )
                            }
                        }

                        override fun onFailure(call: Call<DogInfo>, t: Throwable) {
                            Log.e("API Error", "Network error: ${t.message}")
                        }
                    })
                }
            }
        }

        btnBreedInfoBack = _binding.btnBreedInfoBack
        btnBreedInfoBack?.setOnClickListener {
            finish()
        }
    }

    fun getBreedImageInfo(imageId: String) {
        val call: Call<DogImageInfo> = apiService!!.getBreedImageInfo(imageId)
        call.enqueue(object : Callback<DogImageInfo> {
            override fun onResponse(
                call: Call<DogImageInfo>,
                response: Response<DogImageInfo>
            ) {
                if (response.isSuccessful) {
                    val dogImageInfo: DogImageInfo? = response.body()
                    if (dogImageInfo != null) {
                        Picasso.get()
                            .load(dogImageInfo.url)
                            .transform(RoundedCornersTransformation(50,0)) // Make the image corners round
                            .fit()
                            .centerCrop()
                            .into(ivBreedImage)
                    }

                } else {
                    Log.e("API Error", "Failed to get dog image info. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DogImageInfo>, t: Throwable) {
                Log.e("API Error", "Network error: ${t.message}")
            }
        })
    }
}