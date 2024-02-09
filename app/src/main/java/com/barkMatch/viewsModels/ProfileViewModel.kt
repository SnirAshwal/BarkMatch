package com.barkMatch.viewsModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.barkMatch.models.Post
import com.barkMatch.models.User

class ProfileViewModel : ViewModel() {
    var posts: LiveData<MutableList<Post>>? = null
    var user: LiveData<User>? = null
}