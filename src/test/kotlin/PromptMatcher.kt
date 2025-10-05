import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.message.Message
import io.mockk.MockKMatcherScope

fun MockKMatcherScope.withPromptMessages(vararg messageMatcher: Message.() -> Boolean): Prompt = match({
    it.messages.size == messageMatcher.size
            && it.messages.foldIndexed(true) { index, previous, message -> previous && messageMatcher[index](message) }
})
