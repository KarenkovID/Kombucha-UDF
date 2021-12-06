package com.ikarenkov.teamaker

interface Feature<Msg : Any, Model : Any, Eff : Any> : Cancelable {

    val currentState: Model

    fun accept(msg: Msg)

    fun listenState(listener: (model: Model) -> Unit): Cancelable

    fun listenEffect(listener: (eff: Eff) -> Unit): Cancelable

}
