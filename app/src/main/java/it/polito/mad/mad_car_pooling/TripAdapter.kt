package it.polito.mad.mad_car_pooling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TripAdapter(val data: List<String>): RecyclerView.Adapter<TripAdapter.TripViewHolder> (){

    class TripViewHolder(v: View): RecyclerView.ViewHolder(v){
        val destination: TextView = v.findViewById(R.id.destination)

        //passare poi un oggetto Trip
        fun bind(u: String){
            destination.text = u
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripAdapter.TripViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trip_list, parent, false)
        return TripViewHolder(v)
    }

    override fun onBindViewHolder(holder: TripAdapter.TripViewHolder, position: Int) {
        val u = data[position]
        holder.bind(u)
    }



}