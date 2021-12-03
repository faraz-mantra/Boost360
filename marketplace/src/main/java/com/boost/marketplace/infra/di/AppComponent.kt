package com.boost.marketplace.infra.di

import com.boost.marketplace.MarketplaceApplication
import com.boost.marketplace.infra.viewmodel.ViewModelModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, ViewModelModule::class, AndroidInjectionModule::class])
interface AppComponent {

//    application class
    fun inject(application: MarketplaceApplication)

}