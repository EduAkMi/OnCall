package com.oncall.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oncall.repository.INotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val notificationRepository: INotificationRepository
) : ViewModel() {
    private val dispatcher = Dispatchers.IO
    private val _onGetIsRunning = MutableLiveData<Boolean>()
    internal val onGetIsRunning: LiveData<Boolean> = _onGetIsRunning

    init {
        viewModelScope.launch(dispatcher) {
            notificationRepository.isRunningFlow.collect { isRunning ->
                _onGetIsRunning.postValue(isRunning)
            }
        }
    }

    internal fun logIsRunning() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("AAA", "IsRunning: " + notificationRepository.getIsRunning().toString())
        }
    }
}
