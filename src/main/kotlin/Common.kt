package com.wix

import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

fun log(message: String) {
    println("[${LocalDateTime.now()}][${Thread.currentThread().name}] $message")
}

fun assertMainThread() {
    if (Thread.currentThread().name != "custom-main") {
        throw IllegalStateException("This should be executed on the main thread. Current thread: ${Thread.currentThread().name}")
    }
}

fun assertDbThread() {
    if (Thread.currentThread().name != "dataBaseThread") {
        throw IllegalStateException("This should be executed on the db thread Current thread: ${Thread.currentThread().name}")
    }
}

fun assertNetworkThread() {
    if (Thread.currentThread().name != "networkThread") {
        throw IllegalStateException("This should be executed on the network thread Current thread: ${Thread.currentThread().name}Current thread: ${Thread.currentThread().name}")
    }
}


fun setProgressVisibility(isVisible: Boolean) {
    assertMainThread()
    log("Progress visibility is $isVisible")
}

fun printData(data: Int) {
    assertMainThread()
    log("Data is $data")
}

suspend fun addDataToDb(result: Int): Int {
    log("Adding data to db result: $result")
    assertDbThread()
    delay(2000)
    return result * 10
}


private val atomicInteger = AtomicInteger(0)

suspend fun executeNetworkRequest(): Int {
    log("Executing network request")
    assertNetworkThread()
    delay(1000)
    val result = atomicInteger.incrementAndGet()
    log("Network request executed. Result: $result")
    return result
}

suspend fun executeNetworkRequestWithError(): Int {
    log("Executing network request")
    assertNetworkThread()
    delay(1000)
    val result = atomicInteger.incrementAndGet()
    if (result % 3 == 0){
        throw IllegalStateException("Network request failed $result")
    }
    log("Network request executed. Result: $result")
    return result
}