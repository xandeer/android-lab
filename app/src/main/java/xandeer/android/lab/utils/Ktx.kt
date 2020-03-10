package xandeer.android.lab.utils

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*

// ViewModel {
fun <T> AppCompatActivity.observe(data: LiveData<T>, fn: (it: T) -> Unit) {
  data.observe(this, Observer { it?.let(fn) })
}

fun <T> Context.observe(data: LiveData<T>, fn: (it: T) -> Unit) =
  (this as AppCompatActivity).observe(data, fn)

fun <T> View.observe(data: LiveData<T>, fn: (it: T) -> Unit) =
  context.observe(data, fn)

fun <T> MutableLiveData<T>.notify() {
  value = value
}

fun <T : ViewModel> Context.getVm(clazz: Class<T>) =
  ViewModelProviders.of(this as AppCompatActivity).get(clazz)
// ViewModel }