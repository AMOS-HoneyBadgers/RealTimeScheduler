package contracts

org.springframework.cloud.contract.spec.Contract.make {
    // Label by means of which the output message can be triggered
    label 'task-push'
    // input to the contract
    input {
        // the contract will be triggered by a method
        triggeredBy('testsendTasksToTasksQueue()')
    }
    // output message of the contract
    outputMessage {
        // destination to which the output message will be sent
        sentTo 'tasks.exchange'
        headers {
            header('contentType': 'application/json')
            header('amqp_receivedRoutingKey' : 'tasks.routingkey')
        }
        // the body of the output message
        body("id12312")
    }
}