package it.polito.mad.mad_car_pooling
import android.content.Context
import android.system.Os.remove
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView


class TripAdapter(private val data: List<Trip>, private val context_: Context, private val fragment_: Fragment): RecyclerView.Adapter<TripAdapter.TripViewHolder> (){

    class TripViewHolder(v: View, context_: Context, fragment_: Fragment): RecyclerView.ViewHolder(v){
        private val departure_location: TextView = v.findViewById(R.id.departure)
        private val destination: TextView = v.findViewById(R.id.destination)
        private val departure_time: TextView = v.findViewById(R.id.departure_time)
        private val item_button: LinearLayout = v.findViewById(R.id.item_button)
        private var next_fragment : Fragment = TripDetailsFragment()
        private val context = context_
        private val fragment = fragment_

        //passare poi un oggetto Trip
        fun bind(u: Trip){
            destination.text = u.arrivalLocation
            departure_location.text = u.departureLocation
            departure_time.text = u.departureDateTime
            item_button.setOnClickListener {
                //(context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, next_fragment, "TripDetailsFragment").commit()
                fragment.findNavController().navigate(R.id.action_nav_list_to_details_trip_fragment2)
            }
        }
    }

    override fun getItemCount(): Int {

        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trip_list, parent, false)
        return TripViewHolder(v, context_, fragment_)
    }

    override fun onBindViewHolder(holder: TripAdapter.TripViewHolder, position: Int) {
        val u = data[position]
        holder.bind(u)
    }





}