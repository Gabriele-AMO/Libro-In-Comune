package com.example.progetto.LibriInPrestito

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    // Definisci qui le variabili e i metodi che desideri mantenere durante i cambiamenti di configurazione


    var dataList: ArrayList<ItemsVMLibriInPrestito> = ArrayList()

    fun setData(data: ArrayList<ItemsVMLibriInPrestito>) {
        dataList = data
    }

    fun getData(): ArrayList<ItemsVMLibriInPrestito> {
        return dataList
    }

}
