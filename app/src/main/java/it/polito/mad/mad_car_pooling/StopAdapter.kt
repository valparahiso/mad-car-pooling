package it.polito.mad.mad_car_pooling
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.mad_car_pooling.ui.trip_details.TripDetailsFragment
import it.polito.mad.mad_car_pooling.ui.trip_list.TripListFragment


class StopAdapter(private val data: List<Stop>, private val fragment_: TripDetailsFragment): RecyclerView.Adapter<StopAdapter.StopViewHolder> (){

    class StopViewHolder(v: View, fragment_: TripDetailsFragment): RecyclerView.ViewHolder(v){
        private val location: TextView = v.findViewById(R.id.departure_stop)
        private val dateTime: TextView = v.findViewById(R.id.date_time_stop)

        private val fragment = fragment_

        //passare poi un oggetto Trip
        fun bind(u: Stop){
            location.text = u.locationName
            dateTime.text = u.stopDateTime

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_stop_list, parent, false)
        return StopViewHolder(v, fragment_)
    }

    override fun onBindViewHolder(holder: StopAdapter.StopViewHolder, position: Int) {
        val u = data[position]
        holder.bind(u)
    }





}