package com.example.avjindersinghsekhon.minimaltodo

import android.content.res.Resources



class PreferenceKeys constructor(resources : Resources){

    @JvmField
    val night_mode_pref_key : String =  resources.getString(R.string.night_mode_pref_key)

}