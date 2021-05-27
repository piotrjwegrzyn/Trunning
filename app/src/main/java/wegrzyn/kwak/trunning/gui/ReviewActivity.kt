package wegrzyn.kwak.trunning.gui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import wegrzyn.kwak.trunning.R

class ReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        val dane = Data.getInstance()
        val databaseHelper = dane.databaseHelper

        val whichAmI = intent.getIntExtra("TRACK_ID", 0)

        findViewById<ExtendedFloatingActionButton>(R.id.fab3).setOnClickListener {
            if (whichAmI != 0) {
                databaseHelper.readTrackFromDatabase(databaseHelper.getTrackNameFromId(whichAmI))
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }

    }
}