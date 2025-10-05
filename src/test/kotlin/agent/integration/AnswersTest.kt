package agent.integration

import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import com.bay.agent.PuzzleDuelAgent
import io.github.cdimascio.dotenv.dotenv
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class AnswersTest {
    private val env = dotenv()

    @Test
    fun presetQueriesTest() = runTest {
        val answers = mapOf(
            "Who is the winner of Tap into Tapa contest?" to "Freddie Hand",
            "How many puzzles contained DoubleShow 6?" to "11",
            "Which puzzle types presented in Skyline Magic contest?" to "Skyscrapers",
            "Which contest was in September 2025?" to "Ring-Ring Rally",
            "Who authored Tetromino Canvas Clash contest?" to "Anurag Sahay",
            )

        val agent = PuzzleDuelAgent {
            client = OpenAILLMClient(env["OPENAI_KEY"])
        }

        answers.forEach { (question, answer) ->
            val result = agent.process(question)
            println("$question: \n$result")
            result shouldContain answer
        }
    }
}