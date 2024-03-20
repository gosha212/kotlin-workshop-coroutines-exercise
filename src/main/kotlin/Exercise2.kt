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
    runBlocking(uiThread) {
        // 1. Set progress visibility to true on ui thread (setProgressVisibility)
        setProgressVisibility(true)

        val coroutineScope = CoroutineScope(networkDispatcher)

        // 2. Execute 50 network request on network pool (executeNetworkRequest)
        repeat(50) {
            coroutineScope.launch {
                executeNetworkRequest()
            }
        }

        // 3. Cancel all network request after 500 ms
        delay(500)
        coroutineScope.cancel()

        // 4. Execute 50 failing network request on network pool (executeNetworkRequestWithError)
        val deferredList = supervisorScope {
            (1..50).map {
                async(networkDispatcher) {
                    executeNetworkRequestWithError()
                }
            }
        }

        val successfulResults: List<Int> = deferredList.map {
            try {
                it.await()
            } catch (e: Exception) {
                log("Error: ${e.message}")
                0

            }
        }

        // 5. Sum the successful responses and insert the result to database on database thread (addDataToDb).
        val result = withContext(dataBaseDispatcher) {
            addDataToDb(successfulResults.sum())
        }
        // 6. Set progress visibility to false on ui thread (setProgressVisibility)
        setProgressVisibility(false)
        // 7. Print the data that was returned from the addToDb function on the ui thread (printData)
        printData(result)
    }
}