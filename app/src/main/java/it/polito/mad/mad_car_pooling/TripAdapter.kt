package it.polito.mad.mad_car_pooling
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView


class TripAdapter(private val data: List<Trip>, private val context_: Context): RecyclerView.Adapter<TripAdapter.TripViewHolder> (){

    class TripViewHolder(v: View, context_: Context): RecyclerView.ViewHolder(v){
        private val departure_location: TextView = v.findViewById(R.id.departure)
        private val destination: TextView = v.findViewById(R.id.destination)
        private val departure_time: TextView = v.findViewById(R.id.departure_time)
        private val item_button: LinearLayout = v.findViewById(R.id.item_button)
        private var next_fragment : Fragment? = (context_ as FragmentActivity).supportFragmentManager.findFragmentByTag("TripDetailsFragment")
        private val context = context_

        //passare poi un oggetto Trip
        fun bind(u: Trip){
            destination.text = u.arrivalLocation
            departure_location.text = u.departureLocation
            departure_time.text = u.departureDateTime
            item_button.setOnClickListener {
                (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.trip_list_fragment, next_fragment!!).commit()
            }
        }
    }

    override fun getItemCount(): Int {

        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trip_list, parent, false)
        return TripViewHolder(v, context_)
    }

    override fun onBindViewHolder(holder: TripAdapter.TripViewHolder, position: Int) {
        val u = data[position]
        holder.bind(u)
    }





}