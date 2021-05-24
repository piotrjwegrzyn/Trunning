package wegrzyn.kwak.trunning.gui

import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
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

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            v?.context?.startActivity(Intent(v.context, MainActivity::class.java))
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
        holder.statsTextView.text = (item.track_time / 60).toString() + " min ꞏ " + item.track_distance
        holder.titleTextView.text = item.track_name

    }
    override fun getItemCount() = itemsList.size + 1
}