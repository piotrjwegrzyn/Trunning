package wegrzyn.kwak.trunning.gui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import wegrzyn.kwak.trunning.R
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt


class ActivityActivity : AppCompatActivity() {

    private val DEFAULT_UPDATE_INTERVAL = 4
    private val FASTEST_UPDATE_INTERVAL = 3
    private val MAX_ACCEPTABLE_ACCURACY = 25

    private var isRunning = false
    private var hour = 0
    private var min = 0
    private var sec = 0


    private val data = Data.getInstance()

    private lateinit var tv_Accuracy : TextView
    private lateinit var tv_Speed : TextView
    private lateinit var tv_Long : TextView
    private lateinit var tv_Lat : TextView
    private lateinit var et_trackName : EditText

    private var locationRequest: LocationRequest = LocationRequest.create()
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val databaseHelper = data.databaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity)

        tv_Accuracy = findViewById(R.id.tv_Accuracy)
        tv_Speed = findViewById(R.id.tv_Speed)
        tv_Long = findViewById(R.id.tv_Long)
        tv_Lat = findViewById(R.id.tv_Lat)
        et_trackName = findViewById(R.id.et_trackName)

        findViewById<FloatingActionButton>(R.id.fab2).setOnClickListener {
            finishActivity()
            finish()
        }

        locationRequest.interval = (1000 * DEFAULT_UPDATE_INTERVAL).toLong()
        locationRequest.fastestInterval = (1000 * FASTEST_UPDATE_INTERVAL).toLong()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location.accuracy < MAX_ACCEPTABLE_ACCURACY) {
                    data.addPoint(location)
                    updateTexts(location)
                }
            }
        }
        updating()


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }  // END of onCreate

    private fun updating() {
        time()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun notUpdating() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        data.track_distance = countDistance()
        time()
        val x = databaseHelper.lastTrackId + 1
        databaseHelper.createTrackTable("Track$x")
        data.clearLocations()
    }

    private fun countDistance(): Int {
        var deltaLatitude = 0.0
        var deltaLongitude = 0.0
        var actualLatitude = data.firstPoint.latitude
        var actualLongitude = data.firstPoint.longitude
        for (location in data.points) {
            deltaLatitude += abs(location.latitude - actualLatitude)
            deltaLongitude += abs(location.longitude - actualLongitude)
            actualLatitude = location.latitude
            actualLongitude = location.longitude
        }
        val deltaLatitudeInMeters = deltaLatitude * 111320
        val deltaLongitudeInMeters =
            deltaLongitude * 40075000 * cos(data.firstPoint.latitude) / 360
        val distance =
            sqrt(deltaLatitudeInMeters.pow(2.0) + deltaLongitudeInMeters.pow(2.0))
        return distance.toInt()
    }

    private fun time() {
        if (isRunning) { // Save time for track if tracking was already running
            val calendar = Calendar.getInstance()
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val min = calendar[Calendar.MINUTE]
            val sec = calendar[Calendar.SECOND]
            data.track_time = 3600 * (hour - this.hour) + 60 * (min - this.min) + sec - this.sec
            isRunning = false
        } else { // Save time for tracking start
            val calendar = Calendar.getInstance()
            hour = calendar[Calendar.HOUR_OF_DAY]
            min = calendar[Calendar.MINUTE]
            sec = calendar[Calendar.SECOND]
            isRunning = true
        }
    }

    private fun updateTexts(location: Location){
        tv_Lat.text = DecimalFormat("#.000").format(location.latitude).toString()
        tv_Long.text = DecimalFormat("#.000").format(location.longitude).toString()
        tv_Accuracy.text = (location.accuracy).toString()
        val a = if (location.hasSpeed()) (location.speed).toString() else "0"
        tv_Speed.text = getSpeed(a.toDouble())
    }
    private fun sendAddBroadcast() {
        val i = Intent()
        i.action = "change"
        i.putExtra("change", true)
        LocalBroadcastManager.getInstance(this).sendBroadcast(i)
    }

    private fun getSpeed(speed: Double) : String {
        return DecimalFormat("#.0").format(speed).toString() + " m/s"
    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finishActivity()
        finish()
        return true
    }

    private fun finishActivity() {
        data.track_name = et_trackName.text.toString()
        notUpdating()
        this.sendAddBroadcast()
    }

    override fun onBackPressed() {
        // to prevent strange behavior
    }

}
