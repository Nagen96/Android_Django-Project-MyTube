package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.accessibility.AccessibilityManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_mytube_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MytubeDetailActivity : AppCompatActivity() {

    lateinit var commentEditText: EditText
    lateinit var sendBtn: Button
    lateinit var videoid: String
    lateinit var deleteid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mytube_detail)

        val url = intent.getStringExtra("video_url")
        videoid = intent.getStringExtra("video_id")
        Log.d("video_id", videoid)
        video_view.setVideoPath(url)
        video_view.start()
        video_view.requestFocus()
        val mediaController = MediaController(this@MytubeDetailActivity)
        mediaController?.setAnchorView(video_view)
        video_view.setMediaController(mediaController)

        initView(this@MytubeDetailActivity)

        youtube_comment_recycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                val position = rv.getChildAdapterPosition(child!!)
                Log.d("posss", "" + position)
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }
        })

        sendBtn.setOnClickListener {
            var comment = commentEditText.text.toString()
            if ((application as MasterApplication).checkIsLogin()) {
                val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
                val token = sp.getString("login_sp", "null")

                (application as MasterApplication).service.uploadComment(token!!, comment, videoid)
                    .enqueue(object : Callback<Comment> {
                        override fun onResponse(
                            call: Call<Comment>,
                            response: Response<Comment>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@MytubeDetailActivity,
                                    "덧글작성 완료.",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                updateComment(videoid)
                            } else {
                                Toast.makeText(
                                    this@MytubeDetailActivity,
                                    "덧글작성 실패.",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }

                        }

                        override fun onFailure(call: Call<Comment>, t: Throwable) {
                            Toast.makeText(
                                this@MytubeDetailActivity,
                                "덧글작성 실패.",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    })
            } else {
                Toast.makeText(
                    this@MytubeDetailActivity,
                    "로그인이 필요합니다.",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            commentEditText.text.clear()
        }
        updateComment(videoid)
    }

    fun initView(activity: Activity) {
        commentEditText = activity.findViewById(R.id.comment_edit_text)
        sendBtn = activity.findViewById(R.id.send_btn)
    }

    fun updateComment(videoid: String) {
        (application as MasterApplication).service.getComment(videoid).enqueue(
            object : Callback<ArrayList<Comment>> {
                override fun onResponse(call: Call<ArrayList<Comment>>, response: Response<ArrayList<Comment>>) {
                    if (response.isSuccessful) {
                        val commentList = response.body()
                        val adapter = CommentAdapter(
                            commentList!!,
                            LayoutInflater.from(this@MytubeDetailActivity)
                        )
                        youtube_comment_recycler.adapter = adapter
                        youtube_comment_recycler.layoutManager = LinearLayoutManager(this@MytubeDetailActivity)
                    }
                }

                override fun onFailure(call: Call<ArrayList<Comment>>, t: Throwable) {

                }
            }
        )
    }

    class CommentAdapter(
        var commentList: ArrayList<Comment>,
        val inflater: LayoutInflater
    ) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

        lateinit var commentid: String

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val commentId: TextView
            val commentText: TextView
            val deleteBtn: Button

            init {
                commentId = itemView.findViewById(R.id.youtube_comment_id)
                commentText = itemView.findViewById(R.id.youtube_comment_text)
                deleteBtn = itemView.findViewById(R.id.youtube_comment_delete)

                deleteBtn.setOnClickListener {
                    val value = commentList.get(position).commentid.toString()
                    Log.d("iddd", "" + value)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.youtube_comment_view, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return commentList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.commentId.setText(commentList.get(position).token)
            holder.commentText.setText(commentList.get(position).comment)
            commentid = commentList.get(position).commentid.toString()
        }


    }

    fun deleteComment(commentid: String) {
        if ((application as MasterApplication).checkIsLogin()) {
            val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            val token = sp.getString("login_sp", "null")

            (application as MasterApplication).service.deleteComment(token!!, videoid, commentid)
                .enqueue(object : Callback<Comment> {
                    override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                        if (response.isSuccessful) {

                        }
                    }

                    override fun onFailure(call: Call<Comment>, t: Throwable) {

                    }
                })
        }
    }
}

