package com.bay

import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import com.bay.agent.PuzzleDuelAgent
import kotlinx.coroutines.runBlocking
import io.github.cdimascio.dotenv.dotenv

fun main() = runBlocking {

    val env = dotenv()
    val openAiKey = env["OPENAI_KEY"]
    val googleKey = env["GOOGLE_KEY"]

    val openAIClient = OpenAILLMClient(openAiKey)
    val googleClient = GoogleLLMClient(googleKey)

    val agent = PuzzleDuelAgent {
        client = openAIClient
        writeEnabled = true
        verificationClient = googleClient
    }

    while (true) {
        val input = readLine()

        if (input != null) {
            val result = agent.processWithVerification(input)

            println("Result: ${result.first}")
            println("Verification: ${result.second}")
        } else {
            break
        }
    }
}


