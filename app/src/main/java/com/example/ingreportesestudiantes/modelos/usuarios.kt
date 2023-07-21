package com.example.ingreportesestudiantes.modelos

class usuarios(matricula: String, nombre: String, apellido_Paterno: String,apellido_Materno: String, foto: String, currentDateTimeString: String,correo :String) {
    var matricula: String = matricula
    var nombre: String = nombre
    var apellido_Paterno: String = apellido_Paterno
    var apellido_Materno: String = apellido_Materno
    var foto: String = foto
    var fechaHora: String = currentDateTimeString
    var correo: String = correo
    constructor() : this("","","","","", "","")

}