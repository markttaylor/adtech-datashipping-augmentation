

//snippet-sourcedescription:[SQSExample.java demonstrates how to create, list and delete queues.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[sqs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/24/2020]
//snippet-sourceauthor:[scmacdon-aws]
// snippet-start:[sqs.java2.sqs_example.complete]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monster.adtech.example;

import software.amazon.awssdk.regions.Region;
// snippet-start:[sqs.java2.sqs_example.import]
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
// snippet-end:[sqs.java2.sqs_example.import]

import java.util.List;

// snippet-start:[sqs.java2.sqs_example.main]
public class SQSExample {

    fun main(args : Array<String>) {
        val queueName: String  = "queue" + System.currentTimeMillis();

        val sqsClient: SqsClient = SqsClient.builder()
            .region(Region.AP_SOUTHEAST_2)
            .build();

        System.out.println("\nCreate Queue");
        // snippet-start:[sqs.java2.sqs_example.create_queue]
        val createQueueRequest: CreateQueueRequest = CreateQueueRequest.builder().queueName(queueName).build();
        sqsClient.createQueue(createQueueRequest);
        // snippet-end:[sqs.java2.sqs_example.create_queue]

        System.out.println("\nGet queue url");
        // snippet-start:[sqs.java2.sqs_example.get_queue]
        val getQueueUrlResponse: GetQueueUrlResponse =
            sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
        val queueUrl: String = getQueueUrlResponse.queueUrl();
        System.out.println(queueUrl);
        // snippet-end:[sqs.java2.sqs_example.get_queue]


        System.out.println("\nList Queues");
        // snippet-start:[sqs.java2.sqs_example.list_queues]
        val prefix: String = "que";
        val listQueuesRequest: ListQueuesRequest = ListQueuesRequest.builder().queueNamePrefix(prefix).build();
        val listQueuesResponse: ListQueuesResponse  = sqsClient.listQueues(listQueuesRequest);
        for (url: String in listQueuesResponse.queueUrls()) {
            System.out.println(url);
        }
        // snippet-end:[sqs.java2.sqs_example.list_queues]

        // List queues with filters
        val namePrefix: String = "queue";
        val filterListRequest: ListQueuesRequest = ListQueuesRequest.builder()
            .queueNamePrefix(namePrefix).build();

        val listQueuesFilteredResponse: ListQueuesResponse = sqsClient.listQueues(filterListRequest);
        System.out.println("Queue URLs with prefix: " + namePrefix);
        for (url: String in listQueuesFilteredResponse.queueUrls()) {
            System.out.println(url);
        }

        System.out.println("\nSend message");
        // snippet-start:[sqs.java2.sqs_example.send_message]
        sqsClient.sendMessage(SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody("Hello world!")
            .delaySeconds(10)
            .build());
        // snippet-end:[sqs.java2.sqs_example.send_message]

        System.out.println("\nSend multiple messages");
        // snippet-start:[sqs.java2.sqs_example.send__multiple_messages]
        val sendMessageBatchRequest: SendMessageBatchRequest = SendMessageBatchRequest.builder()
            .queueUrl(queueUrl)
            .entries(SendMessageBatchRequestEntry.builder().id("id1").messageBody("Hello from msg 1").build(),
                SendMessageBatchRequestEntry.builder().id("id2").messageBody("msg 2").delaySeconds(10).build())
            .build();
        sqsClient.sendMessageBatch(sendMessageBatchRequest);
        // snippet-end:[sqs.java2.sqs_example.send__multiple_messages]


        System.out.println("\nReceive messages");
        // snippet-start:[sqs.java2.sqs_example.retrieve_messages]
        val receiveMessageRequest: ReceiveMessageRequest = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(5)
            .build();
        val messages: kotlin.collections.List<Message> = sqsClient.receiveMessage(receiveMessageRequest).messages();
        // snippet-end:[sqs.java2.sqs_example.retrieve_messages]


        System.out.println("\nChange Message Visibility");
        for (message: Message in messages) {
            val req: ChangeMessageVisibilityRequest = ChangeMessageVisibilityRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .visibilityTimeout(100)
                .build();
            sqsClient.changeMessageVisibility(req);
        }

        System.out.println("\nDelete Messages");
        // snippet-start:[sqs.java2.sqs_example.delete_message]
        for (message: Message in messages) {
            val deleteMessageRequest: DeleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build();
            sqsClient.deleteMessage(deleteMessageRequest);
        }
        // snippet-end:[sqs.java2.sqs_example.delete_message]

        System.out.println("\nDelete Queue");
        // snippet-start:[sqs.java2.sqs_example.delete_queue]
        val deleteQueueRequest: DeleteQueueRequest = DeleteQueueRequest.builder().queueUrl(queueUrl).build();
        sqsClient.deleteQueue(deleteQueueRequest);
        // snippet-end:[sqs.java2.sqs_example.delete_queue]
    }
}
// snippet-end:[sqs.java2.sqs_example.main]
// snippet-end:[sqs.java2.sqs_example.complete]

