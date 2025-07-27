package com.example.manoslocales.models

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class User(

    @get:PropertyName("nombre") @set:PropertyName("nombre")
    var nombre: String = "",

    @get:PropertyName("apellido") @set:PropertyName("apellido")
    var apellido: String = "",

    @get:PropertyName("email") @set:PropertyName("email")
    var email: String = "",

    @get:PropertyName("fechaNacimiento") @set:PropertyName("fechaNacimiento")
    var fechaNacimiento: String = "",

    @get:PropertyName("tipoDocumento") @set:PropertyName("tipoDocumento")
    var tipoDocumento: String = "",

    @get:PropertyName("numeroDocumento") @set:PropertyName("numeroDocumento")
    var numeroDocumento: String = "",

    @get:PropertyName("direccion") @set:PropertyName("direccion")
    var direccion: String = "",

    @get:PropertyName("codigoPostal") @set:PropertyName("codigoPostal")
    var codigoPostal: String = "",

    @get:PropertyName("esEmprendedor") @set:PropertyName("esEmprendedor")
    var esEmprendedor: Boolean = false,

    @get:PropertyName("nombreEmprendimiento") @set:PropertyName("nombreEmprendimiento")
    var nombreEmprendimiento: String? = null,

    @get:PropertyName("descripcionEmprendimiento") @set:PropertyName("descripcionEmprendimiento")
    var descripcionEmprendimiento: String? = null,

    @get:PropertyName("ubicacionEmprendimiento") @set:PropertyName("ubicacionEmprendimiento")
    var ubicacionEmprendimiento: String? = null,

    @get:PropertyName("telefono") @set:PropertyName("telefono")
    var telefono: String? = null
) {
    constructor() : this("", "", "", "", "", "", "", "", false, null, null, null, null)
}