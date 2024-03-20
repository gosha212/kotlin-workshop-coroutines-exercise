package com.wix

import kotlinx.coroutines.*
import java.util.concurrent.Executors

private val networkDispatcher = Executors.newFixedThreadPool(3) {
    Thread(it, "networkThread").apply { isDaemon = true }
}.asCoroutineDispatcher()

private val dataBaseDispatcher = Executors.newSingleThreadExecutor {
    Thread(it, "dataBaseThread").apply { isDaemon = true }
}.asCoroutineDispatcher()

private val uiThread = Executors.newSingleThreadExecutor {
    Thread(it, "custom-main").apply { isDaemon = true }
}.asCoroutineDispatcher()

fun main() {
    // 1. Set progress visibility to true on ui thread (setProgressVisibility)

    // 2. Execute 50 network request on network pool (executeNetworkRequest)

    // 3. Cancel all network request after 500 ms

    // 4. Execute 50 failing network request on network pool (executeNetworkRequestWithError)

    // 5. Sum the successful responses and insert the result to database on database thread (addDataToDb).

    // 6. Set progress visibility to false on ui thread (setProgressVisibility)

    // 7. Print the data that was returned from the addToDb function on the ui thread (printData)
}