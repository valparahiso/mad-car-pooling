package it.polito.mad.mad_car_pooling

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

data class Stop(
        var locationName: String,
        var stopDateTime: String,
        var saved: Boolean,
        var deleted: Boolean
) : Parcelable{ //inherited for saving the state in the fragment
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )
    init {
            Log.d("POLITOMAD_Stop", "Location Name: " + this.locationName)
            Log.d("POLITOMAD_Stop", "Date Time: " + this.stopDateTime)
        }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(locationName)
        parcel.writeString(stopDateTime)
        parcel.writeByte(if (saved) 1 else 0)
        parcel.writeByte(if (deleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Stop> {
        override fun createFromParcel(parcel: Parcel): Stop {
            return Stop(parcel)
        }

        override fun newArray(size: Int): Array<Stop?> {
            return arrayOfNulls(size)
        }
    }
}
