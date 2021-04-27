package it.polito.mad.mad_car_pooling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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