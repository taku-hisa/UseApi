package com.example.useapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SimpleAdapter
import androidx.lifecycle.lifecycleScope
import com.example.useapi.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cityList : MutableList<MutableMap<String, String>> = mutableListOf()
        cityList.add( mutableMapOf("name" to "名古屋", "id" to "230010") )
        cityList.add( mutableMapOf("name" to "豊橋", "id" to "230020") )
        cityList.add( mutableMapOf("name" to "津", "id" to "240010") )
        cityList.add( mutableMapOf("name" to "尾鷲", "id" to "240020") )
        cityList.add( mutableMapOf("name" to "岐阜", "id" to "210010") )
        cityList.add( mutableMapOf("name" to "高山", "id" to "210020") )
        cityList.add( mutableMapOf("name" to "静岡", "id" to "220010") )
        cityList.add( mutableMapOf("name" to "浜松", "id" to "220040") )
        val adapter = SimpleAdapter(applicationContext,
            cityList,
            android.R.layout.simple_list_item_1,
            arrayOf("name"),
            intArrayOf(android.R.id.text1))
        binding.listView.adapter = adapter

        binding.listView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as Map<String,String>
            val cityName = item["name"]
            val cityId = item["id"]

            lifecycleScope.launch {
                if(cityId!=null) {
                    val response = getRequest(cityId)
                    binding.textView.setText(response?.title + response?.link)
                }
            }
        }
    }
}

val api = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://weather.tsukumijima.net")
        .build().create(ApiInterface::class.java)

data class Weather(
    val title: String = "",
    val link: String = ""
)

interface ApiInterface {
    //何か文字を入れないとエラーになる
    @GET("/api/forecast")
    suspend fun fetchWeather(
        //?city=${id}というクエリが生成される
        @Query("city")
        id:String
    ): Weather
}

suspend fun getRequest(id:String): Weather? {
    try {
        val response: Weather = api.fetchWeather(id)
        return response
    } catch (e: Exception) {
        return null
    }
}