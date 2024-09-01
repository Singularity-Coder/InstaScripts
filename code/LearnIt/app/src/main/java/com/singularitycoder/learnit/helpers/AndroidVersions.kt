package com.singularitycoder.learnit.helpers

import android.os.Build

object AndroidVersions {

    @JvmStatic
    fun isTiramisu() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}
