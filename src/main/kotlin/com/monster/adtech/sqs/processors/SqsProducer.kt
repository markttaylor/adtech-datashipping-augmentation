/*
This code is from here: https://jivimberg.io/blog/2019/02/23/sqs-consumer-using-kotlin-coroutines/
No copyright
*/
package com.monster.adtech.sqs.processors

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

fun main() = runBlocking {
    val SQS_URL = "https://sqs.ap-southeast-2.amazonaws.com/525207659638/Mark_testonly_1587428967054"
    val sqs = SqsClient.builder()
        .region(Region.AP_SOUTHEAST_2)
        .build()

    var id = 0

    while (true) {
        id++

        val sendMsgRequest = SendMessageRequest.builder()
            .queueUrl(SQS_URL)
            .messageBody("hello world $id")
            .build()

        sqs.sendMessage(sendMsgRequest)
        println("Message sent with id: $id")
        delay((1000L..5000L).random())
    }
}