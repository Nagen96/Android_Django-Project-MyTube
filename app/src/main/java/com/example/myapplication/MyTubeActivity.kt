package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.activity_my_tube.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyTubeActivity : AppCompatActivity() {

    lateinit var glide: RequestManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tube)


        (application as MasterApplication).service.getYoutubeList()
            .enqueue(object : Callback<ArrayList<Youtube>> {
                override fun onFailure(call: Call<ArrayList<Youtube>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<ArrayList<Youtube>>,
                    response: Response<ArrayList<Youtube>>
                ) {
                    if (response.isSuccessful) {
                        glide = Glide.with(this@MyTubeActivity)
                        val youtubeList = response.body()
                        val adapter = MytubeAdapter(
                            youtubeList!!,
                            LayoutInflater.from(this@MyTubeActivity),
                            glide,
                            this@MyTubeActivity
                        )
                        youtube_list_recycler.adapter = adapter
                    }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        val logoutInflater: MenuInflater = menuInflater
        if ((application as MasterApplication).checkIsLogin()) {
            logoutInflater.inflate(R.menu.logout_menu, menu)
        } else {
            inflater.inflate(R.menu.actionbar_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signin_menu -> {
                val intent = Intent(this, SignInActivity::class.java)
                this.startActivity(intent)
            }
            R.id.logout_menu -> {
                val tokenSP = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
                val tokenEditor = tokenSP.edit()
                tokenEditor.putString("login_sp", "null")
                tokenEditor.commit()
                (application as MasterApplication).createRetrofit()
                finish()
                startActivity(Intent(this@MyTubeActivity, MyTubeActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item);
    }

//    #Old Version.
//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//
//        val singinMenu = menu?.findItem(R.id.signin_menu)
//        val logoutMenu = menu?.findItem(R.id.logout_menu)
//
//        if ((application as MasterApplication).checkIsLogin()) {
//            singinMenu?.isEnabled = false
//            logoutMenu?.isEnabled = true
//        } else {
//            singinMenu?.isEnabled = true
//            logoutMenu?.isEnabled = false
//        }
//        return true
//    }

    class MytubeAdapter(
        var youtubeList: ArrayList<Youtube>,
        val inflater: LayoutInflater,
        val glide: RequestManager,
        val activity: Activity
    ) : RecyclerView.Adapter<MytubeAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title: TextView
            val thumbnail: ImageView
            val content: TextView

            init {
                title = itemView.findViewById(R.id.youtube_title)
                thumbnail = itemView.findViewById(R.id.youtube_thumbnail)
                content = itemView.findViewById(R.id.youtube_content)

                itemView.setOnClickListener {
                    val position: Int = adapterPosition
                    val intent = Intent(activity, MytubeDetailActivity::class.java)
                    intent.putExtra("video_url", youtubeList.get(position).video)
                    intent.putExtra("video_id", youtubeList.get(position).videoid)
                    activity.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.youtube_item_view, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return youtubeList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.setText(youtubeList.get(position).title)
            holder.content.setText(youtubeList.get(position).content)
            glide.load(youtubeList.get(position).thumbnail).into(holder.thumbnail)
        }
    }
}


