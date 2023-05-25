package com.thecover.lib.schemeutils.idispatchers

import android.content.Context

/**
 * @author:likewen
 * @date:2022/9/22
 * desc:
 */
interface Dispatchers {
   fun dispatch(context: Context?, module:String?, action: String?, params: String?): Boolean
}
