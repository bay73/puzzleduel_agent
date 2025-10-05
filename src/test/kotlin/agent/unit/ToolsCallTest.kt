package agent.unit

import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.ResponseMetaInfo
import com.bay.agent.PuzzleDuelAgent
import com.bay.agent.tools.HttpClient
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import withPromptMessages

@ExtendWith(MockKExtension::class)
class ToolsCallTest {

    @MockK
    private lateinit var mockLlmClient: OpenAILLMClient

    @MockK
    private lateinit var mockHttpClient: HttpClient

    @Test
    fun testUseGetContestListTool() = runTest {
        coEvery {
            mockLlmClient.execute(
                withPromptMessages(
                    { role == Message.Role.System },
                    { role == Message.Role.User }
                ),
                any(),
                any(),
            )
        } returns listOf(
            Message.Tool.Call("fake_id", "list_of_competitions", "{}", ResponseMetaInfo(Clock.System.now())),
        )
        coEvery {
            mockLlmClient.execute(
                withPromptMessages(
                    { role == Message.Role.System },
                    { role == Message.Role.User },
                    { role == Message.Role.Tool && this is Message.Tool.Call },
                    { role == Message.Role.Tool && this is Message.Tool.Result }
                ),
                any(),
                any(),
            )
        } returns listOf(
            Message.Assistant("This is the first answer", ResponseMetaInfo(Clock.System.now())),
        )
        every { mockHttpClient.get(any()) } returns Jsoup.parse("<html><head><title>Fake</title></head></html>")

        val agent = PuzzleDuelAgent.Companion {
            client = mockLlmClient
            httpClient = mockHttpClient
        }

        val result = agent.process("Which contest was in July 2025?")

        result shouldBe "This is the first answer"

        coVerifySequence {
            mockLlmClient.execute(withArg { it.messages.size == 2 }, OpenAIModels.Chat.GPT4o, any())
            mockHttpClient.get("https://www.puzzleduel.club/contest")
            mockLlmClient.execute(withArg { it.messages.size == 4 }, OpenAIModels.Chat.GPT4o, any())
        }
    }

    @Test
    fun testUseGetContestResultsTool() = runTest {
        coEvery {
            mockLlmClient.execute(
                withPromptMessages(
                    { role == Message.Role.System },
                    { role == Message.Role.User }
                ),
                any(),
                any(),
            )
        } returns listOf(
            Message.Tool.Call(
                "fake_id",
                "full_competition_results",
                "{\"competitionIdentifier\":\"fake_contest_id\"}",
                ResponseMetaInfo(Clock.System.now())
            ),
        )
        coEvery {
            mockLlmClient.execute(
                withPromptMessages(
                    { role == Message.Role.System },
                    { role == Message.Role.User },
                    { role == Message.Role.Tool && this is Message.Tool.Call },
                    { role == Message.Role.Tool && this is Message.Tool.Result }
                ),
                any(),
                any(),
            )
        } returns listOf(
            Message.Assistant("This is the second answer", ResponseMetaInfo(Clock.System.now())),
        )
        every { mockHttpClient.get(any()) } returns Jsoup.parse("<html><head><title>Fake</title></head></html>")

        val agent = PuzzleDuelAgent.Companion {
            client = mockLlmClient
            httpClient = mockHttpClient
        }

        val result = agent.process("How won contest in July 2025?")

        result shouldBe "This is the second answer"

        coVerifySequence {
            mockLlmClient.execute(withArg { it.messages.size == 2 }, OpenAIModels.Chat.GPT4o, any())
            mockHttpClient.get("https://www.puzzleduel.club/contest/fake_contest_id/results")
            mockLlmClient.execute(withArg { it.messages.size == 4 }, OpenAIModels.Chat.GPT4o, any())
        }
    }
}