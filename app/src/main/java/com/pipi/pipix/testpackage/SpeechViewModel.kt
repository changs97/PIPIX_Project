package com.pipi.pipix.testpackage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpeechViewModel: ViewModel() {
    val currentCountVisible: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    val currentImageVisible: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    val currentClickable: MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>()
    }

    val currentProgress: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }
}