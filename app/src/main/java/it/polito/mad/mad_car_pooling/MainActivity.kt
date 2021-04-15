package it.polito.mad.mad_car_pooling

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.tripListReciclerView)

        val list: List<String> = listOf("Stringa1", "Stringa2", "Stringa3", "Stringa1", "Stringa2", "Stringa3","Stringa1", "Stringa2", "Stringa3" , "Stringa1", "Stringa2", "Stringa3")

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TripAdapter(list)
    }

}