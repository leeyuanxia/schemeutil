package com.example.myapplication

import android.content.Context
import cn.thecover.lib.schemeutils.lib_annotation.Action
import cn.thecover.lib.schemeutils.lib_annotation.Module
import com.thecover.lib.schemeutils.idispatchers.Callback

/**
 * @author:likewen
 * @date:2023/2/9
 *
 */
@Module("news")
class TestDispatcher {
    @Action("getName")
    fun getName(context: Context,params: String?, callback: Callback?) {

        callback?.call("我在测试")
    }
}
