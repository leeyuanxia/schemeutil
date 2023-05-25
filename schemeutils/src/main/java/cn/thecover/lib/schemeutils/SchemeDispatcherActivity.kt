package cn.thecover.lib.schemeutils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.thecover.lib.schemeutils.utils.SchemaUtil

class SchemeDispatcherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheme_dispatcher)
        intent?.data?.let {
            SchemaUtil.invokeSchemeFromOutside(applicationContext, it)
        }
        finish()
    }
}