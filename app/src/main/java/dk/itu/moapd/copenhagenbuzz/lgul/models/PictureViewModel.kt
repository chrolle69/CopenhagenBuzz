package dk.itu.moapd.copenhagenbuzz.lgul.models

import androidx.camera.core.CameraSelector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class PictureViewModel : ViewModel() {


    private var _selector = MutableLiveData<CameraSelector>()


    val selector: LiveData<CameraSelector>
        get() = _selector


    fun onCameraSelectorChanged(selector: CameraSelector) {
        this._selector.value = selector
    }

}