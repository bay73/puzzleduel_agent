package com.bay.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.ToolRegistry.Builder
import ai.koog.agents.core.tools.ToolRegistry.Companion.invoke
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import com.bay.agent.tools.HttpClient
import com.bay.agent.tools.JsoupHttpClient
import com.bay.agent.tools.PuzzleDuelReadToolSet
import com.bay.agent.tools.PuzzleDuelWriteToolSet
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import kotlin.collections.forEach

class PuzzleDuelAgent(
    client: LLMClient,
    verificationClient: LLMClient,
    httpClient: HttpClient,
    writeEnabled: Boolean,
) {

    private val readTools = PuzzleDuelReadToolSet(httpClient)
    private val writeTools = PuzzleDuelWriteToolSet()

    private val agent = AIAgent(
        executor = SingleLLMPromptExecutor(client),
        systemPrompt = "You are an agent who helps to analyse results of puzzle competitions. " +
                "You goal is to answer different questions about competition results and provide aggregated data.",

        llmModel = chooseModel(client),
        toolRegistry = ToolRegistry {
            tools(readTools.asTools())
            if (writeEnabled) {
                tools(writeTools.asTools())
            }
        }
    )

    private val verificationAgent = AIAgent(
        executor = SingleLLMPromptExecutor(verificationClient),
        systemPrompt = "Your goal is to verify correctness of an answer. Aou are provided a question and an answer and" +
                " should check if the given answer makes sense and formatted correctly. " +
                "Return just a single number from 1 to 10, where 1 means that the answer is totally wrong and 10 means that the answer is very good..",
        llmModel = chooseModel(verificationClient)
    )
    suspend fun process(input: String): String = agent.run(input)

    suspend fun processWithVerification(input: String): Pair<String, String> {
        val result = process(input)
        val verificationResult = verificationAgent.run("QUESTION: $input \nANSWER: $result")
        return result to verificationResult
    }

    // TODO: find better way to choose model - check if Koog has possibility of autoselect model available through an executor.
    private fun chooseModel(llmClient: LLMClient): LLModel =
        when (llmClient) {
            is OpenAILLMClient -> OpenAIModels.Chat.GPT4o
            is GoogleLLMClient -> GoogleModels.Gemini2_5Flash
            else -> error("Unsupported LLM client: $llmClient")
        }

    class Builder internal constructor() {
        lateinit var client: LLMClient
        var httpClient: HttpClient = JsoupHttpClient()
        var verificationClient: LLMClient? = null
        var writeEnabled: Boolean = false

        internal fun build(): PuzzleDuelAgent {
            return PuzzleDuelAgent(client, verificationClient ?: client, httpClient, writeEnabled)
        }
    }

    companion object {
        operator fun invoke(init: Builder.() -> Unit): PuzzleDuelAgent = Builder().apply(init).build()
    }
}