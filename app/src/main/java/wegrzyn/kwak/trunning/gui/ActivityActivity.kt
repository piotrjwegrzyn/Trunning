package wegrzyn.kwak.trunning.gui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import wegrzyn.kwak.trunning.R
import java.util.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

class ActivityActivity : AppCompatActivity() {

    private val DEFAULT_UPDATE_INTERVAL = 2
    private val FASTEST_UPDATE_INTERVAL = 1
    private val MAX_ACCEPTABLE_ACCURACY = 21

    private var isRunning = false
    private var hour = 0
    private var min = 0
    private var sec = 0


    private val dane = Dane.getInstance()

    private var locationRequest: LocationRequest = LocationRequest.create()
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val databaseHelper = dane.databaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity)

        findViewById<FloatingActionButton>(R.id.fab2).setOnClickListener {
            notUpdating()
            finish()
        }

        locationRequest.setInterval((1000 * DEFAULT_UPDATE_INTERVAL).toLong())
        locationRequest.setFastestInterval((1000 * FASTEST_UPDATE_INTERVAL).toLong())
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location.accuracy < MAX_ACCEPTABLE_ACCURACY) {
                    dane.addPoint(location)
                }
            }
        }
        updating()
    }
        private fun updating() {
            time()
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }

        fun notUpdating() {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            dane.track_distance = countDistance()
            time()
            val random = Random()
            val x = random.nextInt(100000)
            databaseHelper.createTrackTable("Track$x")
            dane.clearLocations()
        } // END of notUpdating

        private fun countDistance(): Int {
            var deltaLatitude = 0.0
            var deltaLongtitude = 0.0
            var actualLatitude = dane.firstPoint.latitude
            var actualLongtitude = dane.firstPoint.longitude
            for (location in dane.points) {
                deltaLatitude += location.latitude - actualLatitude
                deltaLongtitude += location.longitude - actualLongtitude
                actualLatitude = location.latitude
                actualLongtitude = location.longitude
            }
            val deltaLatitudeInMeters = deltaLatitude * 111320
            val deltaLongtitudeInMeters =
                deltaLongtitude * 40075000 * cos(dane.firstPoint.latitude) / 360
            val distance =
                sqrt(deltaLatitudeInMeters.pow(2.0) + deltaLongtitudeInMeters.pow(2.0))
            return distance.toInt()
        } // END of countDistance


        private fun time() {
            if (isRunning) { // Save time for track if tracking was already running
                val calendar = Calendar.getInstance()
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val min = calendar[Calendar.MINUTE]
                val sec = calendar[Calendar.SECOND]
                dane.track_time = 3600 * (hour - this.hour) + 60 * (min - this.min) + sec - this.sec
                isRunning = false
            } else { // Save time for tracking start
                val calendar = Calendar.getInstance()
                hour = calendar[Calendar.HOUR_OF_DAY]
                min = calendar[Calendar.MINUTE]
                sec = calendar[Calendar.SECOND]
                isRunning = true
            }
        } // END of time

}