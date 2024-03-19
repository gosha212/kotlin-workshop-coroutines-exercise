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

fun main() = runBlocking(uiThread) {
    // 1. Set progress visibility to true on ui thread (setProgressVisibility)
    setProgressVisibility(true)
    // 2. Execute 50 network request on network pool
    // (executeNetworkRequest)
    val networkResult: Int = withContext(networkDispatcher) {
        val results = (1..50).map {
            async {
                executeNetworkRequest()
            }
        }

        results.awaitAll().sum()
    }

    // 3. Sum the network responses and insert the result to database on database thread (addDataToDb).
    val dbResult = withContext(dataBaseDispatcher) {
        addDataToDb(networkResult)
    }

    // 4. Set progress visibility to false on ui thread (setProgressVisibility)
    setProgressVisibility(false)
    // 5. Print the data that was returned from the addToDb function on the ui thread (printData)
    printData(dbResult)
}





