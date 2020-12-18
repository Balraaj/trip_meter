package com.forall.tripmeter.di.component

import com.forall.tripmeter.di.ServiceScope
import com.forall.tripmeter.repository.ServiceRepository
import dagger.Component

@ServiceScope
@Component(dependencies = [ApplicationComponent::class])
interface ServiceComponent {

    fun getServiceRepository(): ServiceRepository

    @Component.Factory
    interface Factory{
        fun create(applicationComponent: ApplicationComponent): ServiceComponent
    }
}