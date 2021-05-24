package wegrzyn.kwak.trunning.gui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import wegrzyn.kwak.trunning.R

class MainActivity : AppCompatActivity() {

    private val itemsList = ArrayList<MainItem>()
    private lateinit var customAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        customAdapter = CustomAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter
        prepareItems()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val intent = Intent(this, ActivityActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menu?: return false

        menu.add(R.string.button_activity_settings).also {

            it.setIcon(R.drawable.ic_baseline_settings_24)
            it.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            it.setOnMenuItemClickListener {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return@setOnMenuItemClickListener true
            }

        }

        return true
    }

    private fun prepareItems() {

        val mainItem1 = MainItem(1, "Trening z Mareczkiem", "Krakuff", "12 mar 19:30", 600, "12 km")

        val mainItem2 = MainItem(2, "XDD", "Warszafka", "12 mar 19:45", 600, "9 km")

        for (i in 0..10) {
            itemsList.add(mainItem1)
            itemsList.add(mainItem2)
        }
        customAdapter.notifyDataSetChanged()
    }
}
