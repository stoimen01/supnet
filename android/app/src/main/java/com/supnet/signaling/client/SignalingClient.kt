package com.supnet.signaling.client

import com.supnet.signaling.rooms.RoomsEffect
import com.supnet.signaling.rooms.RoomsEvent
import io.reactivex.Observable

interface SignalingClient {
    fun getEvents(): Observable<RoomsEvent.SignalingEvent>
    fun handleEffect(intent: RoomsEffect.SignalingEffect)
}