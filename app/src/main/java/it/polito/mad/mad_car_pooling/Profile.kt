package it.polito.mad.mad_car_pooling

data class Profile(
        var fullName: String,
        var nickName: String,
        var location: String,
        var email: String,
        var birth: String,
        var imagePath: String
){
    private var id_: Int = id
    val index : Int get() = id_

    companion object {
        @JvmStatic  private var id: Int = 0
    }

    init {
        val id1 = id++
        id1
    }
}