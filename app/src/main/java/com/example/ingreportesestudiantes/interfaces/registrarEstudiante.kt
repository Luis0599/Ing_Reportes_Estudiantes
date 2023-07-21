package com.example.ingreportesestudiantes.interfaces
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST
import retrofit.Callback
import retrofit.client.Response
interface registrarEstudiante {
    @FormUrlEncoded
    @POST("/registrarEstudiante.php")
    fun registraEstudiante(
        @Field("idEstudiantes")  idEstudiantes: String,
        @Field("nombre") nombre: String,
        @Field("apellidoP") apellidoP: String,
        @Field("apellidoM") apellidoM: String,
        @Field("matricula") matricula: String,
        callback: Callback<Response?>
    )
}