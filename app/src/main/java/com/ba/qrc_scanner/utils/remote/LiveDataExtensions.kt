package com.ba.qrc_scanner.utils.remote

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.ba.qrc_scanner.enums.Status

fun <T> LiveData<Resource<T>>.observeResource(
    owner: LifecycleOwner,
    onLoading: () -> Unit,
    onSuccess: (T) -> Unit,
    onError: (String) -> Unit
) {
    observe(owner) { resource ->
        when (resource.status) {
            Status.LOADING -> onLoading()
            Status.SUCCESS -> resource.data?.let(onSuccess)
            Status.ERROR -> onError(resource.message ?: "Something went wrong")
        }
    }
}


fun <T> LiveData<Resource<T>>.observeResourceWithLoadingData(
    owner: LifecycleOwner,
    onLoading: (T?) -> Unit,
    onSuccess: (T) -> Unit,
    onError: (String, T?) -> Unit
) {
    observe(owner) { resource ->
        when (resource.status) {
            Status.LOADING -> onLoading(resource.data)
            Status.SUCCESS -> resource.data?.let(onSuccess)
            Status.ERROR -> onError(resource.message ?: "Something went wrong", resource.data)
        }
    }
}


fun <T> LiveData<Resource<T>>.observeResourceSimple(
    owner: LifecycleOwner,
    onData: (T?, Boolean) -> Unit, // data, isLoading
    onError: (String) -> Unit
) {
    observe(owner) { resource ->
        when (resource.status) {
            Status.LOADING -> onData(resource.data, true)
            Status.SUCCESS -> resource.data?.let { onData(it, false) }
            Status.ERROR -> onError(resource.message ?: "Something went wrong")
        }
    }
}