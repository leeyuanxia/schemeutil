package com.example.myapplication

import android.app.Application
import cn.thecover.lib.schemeutils.SUInit

/**
 * @author:likewen
 * @date:2023/2/9
 *
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SUInit.init(this)
    }
}
