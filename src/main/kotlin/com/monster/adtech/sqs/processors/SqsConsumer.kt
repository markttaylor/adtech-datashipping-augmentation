/*
This code is from here: https://jivimberg.io/blog/2019/02/23/sqs-consumer-using-kotlin-coroutines/
No copyright
*/
package com.monster.adtech.sqs.processors

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.future.await
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import kotlin.coroutines.CoroutineContext

class SqsSampleConsumerChannels(private val sqs: SqsAsyncClient) : CoroutineScope {
    private val SQS_URL = "https://sqs.ap-southeast-2.amazonaws.com/525207659638/Mark_testonly_1587428967054"
    private val supervisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + supervisorJob

    fun start() = launch {
        val messageChannel = Channel<Message>()
        repeat(N_WORKERS) { launchWorker(messageChannel) }
        launchMsgReceiver(messageChannel)
    }

    fun stop() {
        supervisorJob.cancel()
    }

    private fun CoroutineScope.launchMsgReceiver(channel: SendChannel<Message>) = launch {
        repeatUntilCancelled {
            val receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(SQS_URL)
                .waitTimeSeconds(20)
                .maxNumberOfMessages(10)
                .build()

            val messages = sqs.receiveMessage(receiveRequest).await().messages()
            println("${Thread.currentThread().name} Retrieved ${messages.size} messages")

            messages.forEach {
                channel.send(it)
            }
        }
    }

    private fun CoroutineScope.launchWorker(channel: ReceiveChannel<Message>) = launch {
        repeatUntilCancelled {
            for (msg in channel) {
                try {
                    processMsg(msg)
                    deleteMessage(msg)
                } catch (ex: Exception) {
                    println("${Thread.currentThread().name} exception trying to process message ${msg.body()}")
                    ex.printStackTrace()
                    changeVisibility(msg)
                }
            }
        }
    }

    private suspend fun processMsg(message: Message) {
        println("${Thread.currentThread().name} Started processing message: ${message.body()}")
        delay((1000L..2000L).random())
        println("${Thread.currentThread().name} Finished processing of message: ${message.body()}")
    }

    private suspend fun deleteMessage(message: Message) {
        sqs.deleteMessage { req ->
            req.queueUrl(SQS_URL)
            req.receiptHandle(message.receiptHandle())
        }.await()
        println("${Thread.currentThread().name} Message deleted: ${message.body()}")
    }

    private suspend fun changeVisibility(message: Message) {
        sqs.changeMessageVisibility { req ->
            req.queueUrl(SQS_URL)
            req.receiptHandle(message.receiptHandle())
            req.visibilityTimeout(10)
        }.await()
        println("${Thread.currentThread().name} Changed visibility of message: ${message.body()}")
    }
}

fun main() = runBlocking {
    println("${Thread.currentThread().name} Starting program")
    val sqs = SqsAsyncClient.builder()
        .region(Region.AP_SOUTHEAST_2)
        .build()
    val consumer = SqsSampleConsumerChannels(sqs)
    consumer.start()
    delay(30000)
    consumer.stop()
}

private const val N_WORKERS = 4