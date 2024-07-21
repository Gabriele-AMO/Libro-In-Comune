package com.example.progetto.RicercaLibri

import androidx.lifecycle.ViewModel

class MyViewModelRicercaLibro: ViewModel() {
    // Definisci qui le variabili e i metodi che desideri mantenere durante i cambiamenti di configurazione


    var dataList: ArrayList<ItemsVMRicercaLibro> = ArrayList()

    fun setData(data: ArrayList<ItemsVMRicercaLibro>) {
        dataList = data
    }

    fun getData(): ArrayList<ItemsVMRicercaLibro> {
        return dataList
    }
}