package wegrzyn.kwak.trunning.gui

import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import wegrzyn.kwak.trunning.R


internal class CustomAdapter(private var itemsList: List<MainItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        var whichAmI: Int = 0
        var dateTextView: TextView = view.findViewById(R.id.item_date)
        var titleTextView: TextView = view.findViewById(R.id.item_title)
        var statsTextView: TextView = view.findViewById(R.id.item_stats)
        var locationTextView: TextView = view.findViewById(R.id.item_location)

        private val dane = Data.getInstance()
        private val databaseHelper = dane.databaseHelper

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            val intent = Intent(v?.context, ReviewActivity::class.java)
            intent.putExtra("TRACK_ID", this.whichAmI)
            v?.context?.startActivity(intent)

            //databaseHelper.readTrackFromDatabase(databaseHelper.getTrackNameFromId(whichAmI))
            //v?.context?.startActivity(Intent(v.context, MapsActivity::class.java))
            
            // stary main ->v?.context?.startActivity(Intent(v.context, MainActivity::class.java))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemsList.size) 1 else 0
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.main_item, parent, false)
            MyViewHolder(itemView)
        } else {
            object : RecyclerView.ViewHolder(View(parent.context).apply {
                val height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    80.0f,
                    context.resources.displayMetrics
                ).toInt()
                layoutParams= LinearLayout.LayoutParams(0, height)
            }){}
        }
    }

    override fun onBindViewHolder(holder_: RecyclerView.ViewHolder, position: Int) {
        if (position == itemsList.size) return
        val holder = holder_ as MyViewHolder

        val item = itemsList[position]

        holder.whichAmI = item.track_id
        holder.dateTextView.text = item.track_date
        holder.locationTextView.text = item.track_location
        holder.statsTextView.text = getStatsText(item)
        holder.titleTextView.text = item.track_name

    }
    override fun getItemCount() = itemsList.size + 1

    private fun getStatsText(item: MainItem): String {

        val time = if(item.track_time > 90) {
            (item.track_time/60).toString() + " min"
        } else {
            (item.track_time).toString() + " sec"
        }

        val distance = if(Integer.parseInt(item.track_distance) > 1000) {
            (Integer.parseInt(item.track_distance)/1000).toString() + " km"
        } else {
            item.track_distance + " m"
        }

        return "$distance ꞏ $time"

    }
}