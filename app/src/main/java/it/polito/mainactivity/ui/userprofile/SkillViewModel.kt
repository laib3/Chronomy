package it.polito.mainactivity.ui.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SkillViewModel(private val skillTitle:String): ViewModel() {

    private var _title = MutableLiveData<String>().apply { value = skillTitle }
    private val _description =  MutableLiveData<String>().apply{ value = "" }
    private val _active = MutableLiveData<Boolean>().apply{ value = false }

    val title: LiveData<String> = _title
    val description: LiveData<String> = _description
    val active: LiveData<Boolean> = _active

    fun setDescription(d:String){
        _description.value = d
    }

    fun setActive(v:Boolean){
        _active.value = v
    }

}