package com.example.ingreportesestudiantes.modelos

class Reporte(direccion: String, fechaHora: String, latitud: String, longitud: String,oficio: String, palabrasClaves: String,nombreDelTecnico: String,telefonoDelTecnico:String) {
    var direccion: String = direccion
    var fechaHora: String = fechaHora
    var latitud: String = latitud
    var longitud: String = longitud
    var oficio: String = oficio
    var palabrasClaves: String = palabrasClaves
    var nombreDelTecnico:String = nombreDelTecnico
    var telefonoDelTecnico :String = telefonoDelTecnico
    constructor() : this("","","","","", "","","")

}