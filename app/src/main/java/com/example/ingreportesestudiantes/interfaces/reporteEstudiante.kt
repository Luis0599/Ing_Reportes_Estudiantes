package com.example.ingreportesestudiantes.interfaces

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface reporteEstudiante {
    @FormUrlEncoded
    @POST("/reporteEstudiante.php")
    fun EnviarReporteEstudiante(
        @Field("nombreOficio")  nombreOficio: String,
        @Field("palabrasClaves") palabrasClaves: String,
        @Field("idEstudiantes") idEstudiantes: String,
        @Field("idTecnicos") idTecnicos: String,
        @Field("nombreCompleto") nombreCompleto: String,
        callback: Callback<Response?>
    )
}