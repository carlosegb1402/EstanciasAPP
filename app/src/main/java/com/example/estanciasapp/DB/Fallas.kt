package com.example.estanciasapp.DB

 class Fallas {

     var idfal:Int=0
    var eqpfal:Int=0
    var fecfal:String=""
    var estfal:Int=0
    var obsfal:String=""

    constructor(eqpfal: Int,fecfal:String,estfal: Int, obsfal: String) {
        this.eqpfal = eqpfal
        this.fecfal = fecfal
        this.estfal = estfal
        this.obsfal = obsfal
    }

     constructor(idfal: Int, eqpfal: Int, fecfal: String, estfal: Int, obsfal: String) {
         this.idfal = idfal
         this.eqpfal = eqpfal
         this.fecfal = fecfal
         this.estfal = estfal
         this.obsfal = obsfal
     }

 }


