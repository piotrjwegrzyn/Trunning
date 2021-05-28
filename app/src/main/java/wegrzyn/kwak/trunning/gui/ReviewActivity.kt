package wegrzyn.kwak.trunning.gui

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import wegrzyn.kwak.trunning.R

class ReviewActivity : AppCompatActivity() {

    private lateinit var tv_distance : TextView
    private lateinit var tv_date : TextView
    private lateinit var tv_time : TextView
    private lateinit var tv_location : TextView
    private lateinit var tv_trackName : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        val data = Data.getInstance()
        val databaseHelper = data.databaseHelper

        tv_distance = findViewById(R.id.tv_distance)
        tv_date = findViewById(R.id.tv_date)
        tv_time = findViewById(R.id.tv_time)
        tv_location = findViewById(R.id.tv_location)
        tv_trackName = findViewById(R.id.tv_trackName)
        val whichAmI = intent.getIntExtra("TRACK_ID", 0)

        val cursor : Cursor = databaseHelper.cursorToTracksTable
        if (cursor.count != 0){
            cursor.move(whichAmI)
            tv_trackName.text = cursor.getString(1)
            tv_location.text =  cursor.getString(2)
            tv_date.text = cursor.getString(3)
            tv_time.text = cursor.getInt(4).toString()
            tv_distance.text = cursor.getString(5)
        }

        findViewById<ExtendedFloatingActionButton>(R.id.fab3).setOnClickListener {
            if (whichAmI != 0) {
                databaseHelper.readTrackFromDatabase("Track" + (whichAmI))
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
    } // END of onCreate
}