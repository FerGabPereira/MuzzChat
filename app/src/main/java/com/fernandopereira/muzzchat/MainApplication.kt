package com.fernandopereira.muzzchat

import android.app.Application
import com.fernandopereira.muzzchat.data.local.DatabaseSeeder
import com.fernandopereira.muzzchat.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }

        CoroutineScope(Dispatchers.IO).launch {
            get<DatabaseSeeder>().seedIfEmpty()
        }
    }
}
