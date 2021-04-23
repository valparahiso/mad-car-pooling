package it.polito.mad.mad_car_pooling
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.mad_car_pooling.ui.trip_list.TripListFragment


class TripAdapter(private val data: List<Trip>, private val fragment_: TripListFragment): RecyclerView.Adapter<TripAdapter.TripViewHolder> (){

    class TripViewHolder(v: View, fragment_: TripListFragment): RecyclerView.ViewHolder(v){
        private val departureLocation: TextView = v.findViewById(R.id.departure)
        private val arrivalLocation: TextView = v.findViewById(R.id.destination)
        private val departureDateTime: TextView = v.findViewById(R.id.departure_time)
        private val duration: TextView = v.findViewById(R.id.duration)
        private val itemButton: LinearLayout = v.findViewById(R.id.item_button)
        private val fragment = fragment_

        //passare poi un oggetto Trip
        fun bind(u: Trip){
            arrivalLocation.text = u.arrivalLocation
            departureLocation.text = u.departureLocation
            departureDateTime.text = u.departureDateTime
            duration.text = u.duration
            itemButton.setOnClickListener {
                
                fragment.updateTrip(u)
                fragment.findNavController().navigate(R.id.action_nav_list_to_details_trip_fragment2)
            }
        }
    }

    override fun getItemCount(): Int {

        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trip_list, parent, false)
        return TripViewHolder(v, fragment_)
    }

    override fun onBindViewHolder(holder: TripAdapter.TripViewHolder, position: Int) {
        val u = data[position]
        holder.bind(u)
    }





}