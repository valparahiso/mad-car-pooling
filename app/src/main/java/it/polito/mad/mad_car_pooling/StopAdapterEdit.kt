package it.polito.mad.mad_car_pooling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.mad_car_pooling.ui.trip_edit.TripEditFragment


class StopAdapterEdit(var data: MutableList<Stop>, private val fragment_: TripEditFragment) :
    RecyclerView.Adapter<StopAdapterEdit.StopEditViewHolder>() {
    var holderHashMap: HashMap<Int, RecyclerView.ViewHolder> = HashMap()

    class StopEditViewHolder(v: View, fragment_: TripEditFragment) : RecyclerView.ViewHolder(v) {
        private val location: TextView = v.findViewById(R.id.departure_stop_edit)
        private val dateTime: TextView = v.findViewById(R.id.date_time_stop_edit)

        //passare poi un oggetto Trip
        fun bind(u: Stop) {

            val params: ViewGroup.LayoutParams = this.itemView.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            this.itemView.layoutParams = params

            location.text = u.locationName
            dateTime.text = u.stopDateTime
            
        }
    }

    override fun onViewDetachedFromWindow(holder: StopEditViewHolder) {
        holderHashMap[holder.adapterPosition] = holder
        super.onViewDetachedFromWindow(holder)
    }

    override fun onViewAttachedToWindow(holder: StopEditViewHolder) {
        holderHashMap.remove(holder.adapterPosition)
        holder.itemView.findViewById<ImageView>(R.id.delete_stop_edit).setOnClickListener {

            data[holder.adapterPosition].deleted = true

            val params: ViewGroup.LayoutParams = holder.itemView.layoutParams
            params.height = 0
            holder.itemView.layoutParams = params

        }
        super.onViewAttachedToWindow(holder)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopEditViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_stop_list_edit,
            parent,
            false
        )
        return StopEditViewHolder(v, fragment_)
    }

    override fun onBindViewHolder(holder: StopEditViewHolder, position: Int) {
        val u = data[position]
        holder.bind(u)
    }

}