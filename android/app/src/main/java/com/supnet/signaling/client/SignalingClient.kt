package com.supnet.signaling.client

import com.supnet.signaling.rooms.RoomsEffect.SignalingEffect
import com.supnet.signaling.rooms.RoomsEvent.SignalingEvent
import io.reactivex.Observable

interface SignalingClient {

    fun getEvents(): Observable<SignalingEvent>

    fun handleEffect(intent: SignalingEffect)

}