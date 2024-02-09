package com.barkMatch.viewsModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.barkMatch.models.Post

class FeedViewModel : ViewModel() {
    var posts: LiveData<MutableList<Post>>? = null
}