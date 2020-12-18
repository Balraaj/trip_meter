package com.forall.tripmeter.di

import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
annotation class ActivityScope


@Scope
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
annotation class FragmentScope

@Scope
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
annotation class ViewModelScope

@Scope
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
annotation class ServiceScope
