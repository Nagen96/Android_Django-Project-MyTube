package com.example.myapplication

import java.io.Serializable

class Comment(
    var token: String? = null,
    var comment: String? = null,
    var videoid: String? = null
) : Serializable