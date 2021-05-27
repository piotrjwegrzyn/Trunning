package wegrzyn.kwak.trunning.gui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import wegrzyn.kwak.trunning.R
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_FINE_LOCATION_CODE = 1
    private val INTERNET_PERMISSION_GRANTED_CODE = 2

    private lateinit var data : Data
    private lateinit var databaseHelper : DatabaseHelper

    private val itemsList = ArrayList<MainItem>()
    private lateinit var customAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ------------------------------------------ PERMISSIONS FOR LOCATION AND INTERNET -------------------------------------------------
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_FINE_LOCATION_CODE
                )
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestPermissions(
                    arrayOf(Manifest.permission.INTERNET),
                    INTERNET_PERMISSION_GRANTED_CODE
                )
            }
        }
        data = Data.getInstance()
        databaseHelper = data.databaseHelper
        // -----------------------------------------------------------------------------------------------------------------------------------
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
    } // END of onCreate

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
        val cursor : Cursor = databaseHelper.cursorToTracksTable
        if (cursor.count != 0){
            while (cursor.moveToNext()) {
                val temp = MainItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5)
                )
                itemsList.add(temp)
            }
        }
        customAdapter.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_FINE_LOCATION_CODE, INTERNET_PERMISSION_GRANTED_CODE -> if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish()
            }
        }
    }
}
