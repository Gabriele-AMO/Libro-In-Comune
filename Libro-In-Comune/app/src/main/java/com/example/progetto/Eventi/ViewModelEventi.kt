package com.example.progetto.Eventi

import androidx.lifecycle.ViewModel
import com.example.progetto.LibriInPrestito.ItemsVMLibriInPrestito

class ViewModelEventi : ViewModel(){


    var dataList: ArrayList<ItemsVMEventi> = ArrayList()

    fun setData(data: ArrayList<ItemsVMEventi>) {
        dataList = data
    }

    fun getData(): ArrayList<ItemsVMEventi> {
        return dataList
    }
}