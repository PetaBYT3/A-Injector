package com.a.injector.domain.model

sealed interface ObservableProcess<out P, out C> {
    data class OnProcess<out P>(val data: P): ObservableProcess<P, Nothing>
    data class OnCompleted<out C>(val data: C): ObservableProcess<Nothing, C>
}