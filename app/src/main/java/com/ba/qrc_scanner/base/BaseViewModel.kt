package com.ba.qrc_scanner.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ba.qrc_scanner.utils.getVersionName
import com.ba.qrc_scanner.utils.isNetworkConnected


abstract class BaseViewModel(application: Application) : AndroidViewModel(
    application
) {


    protected open val isNetworkConnected: Boolean
        get() {
            return isNetworkConnected(getApplication())
        }

     open val versionName: String?
        get() {
            return getVersionName(getApplication())
        }
}
