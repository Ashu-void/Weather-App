package com.first.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import com.first.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate((layoutInflater))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(binding.root)
        fetchWeatherData("chandigarh")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"3f285df0e804498b99fdaeeeff9445fa", "metric")
        response.enqueue(object: Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null ){
                    val temperature = responseBody.main.temp.toString()
                    val minTemp = responseBody.main.temp_min
                    val maxTemp = responseBody.main.temp_max
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure


                    binding.temp.text= "$temperature °C"
                    binding.minTemp.text = "Min temp: $minTemp  °C"
                    binding.maxTemp.text = "Max temp: $maxTemp  °C"
                    binding.weatherCondition.text = "$condition"
                    binding.condition.text = "$condition"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"


//                    Log.d("TAG", "onResponse: $temperature")

                    changeImage(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImage(conditions:String){
        when(conditions){
            "Haze","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.haze_weather)
                binding.mainIcon.setAnimation(R.raw.haze_animation)
            }

            "Clear Sky", "Sunny", "Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_day)
                binding.mainIcon.setAnimation(R.raw.sunny_ani)
            }
            "Partly Clouds","Clouds", "Overcast"->{
                binding.root.setBackgroundResource(R.drawable.cloudy_weather)
                binding.mainIcon.setAnimation(R.raw.cloudy_ani)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rainy_weather)
                binding.mainIcon.setAnimation(R.raw.rainy_ani)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard"->{
            binding.root.setBackgroundResource(R.drawable.snowy_weather)
            binding.mainIcon.setAnimation(R.raw.snow_ani)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_day)
                binding.mainIcon.setAnimation(R.raw.sunny_ani)
            }

        }
        binding.mainIcon.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return  sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return  sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return  sdf.format((Date()))
    }
}