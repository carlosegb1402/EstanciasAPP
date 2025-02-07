package com.example.estanciasapp.DB

class Fallas {
    var idfal: Int = 0
    var eqpfal:Int=0
    var fecfal:String=""
    var estfal:Int=0
    var obsfal:String=""
    var labfal:Int=1

    constructor(eqpfal: Int,fecfal:String,estfal: Int, obsfal: String, labfal: Int) {
        this.eqpfal = eqpfal
        this.fecfal = fecfal
        this.estfal = estfal
        this.obsfal = obsfal
        this.labfal = labfal
    }

}


