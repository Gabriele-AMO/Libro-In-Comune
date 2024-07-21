package com.example.progetto.Preferiti

import androidx.lifecycle.ViewModel
import com.example.progetto.LibriInPrestito.ItemsVMLibriInPrestito

class MyViewModelPreferiti: ViewModel()  {
// Definisci qui le variabili e i metodi che desideri mantenere durante i cambiamenti di configurazione


var dataList: ArrayList<ItemsVMPreferiti> = ArrayList()

fun setData(data: ArrayList<ItemsVMPreferiti>) {
    dataList = data
}

fun getData(): ArrayList<ItemsVMPreferiti> {
    return dataList
}

}
