package com.bay.agent.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Tools for modifying Puzzle Duel web site data")
class PuzzleDuelWriteToolSet : ToolSet {
    @Tool("publish_announcement")
    @LLMDescription("Publishes announcement at facebook page")
    fun publishAnnouncement(
        @LLMDescription("Announcement content")
        announcementContent: String,
        @LLMDescription("Announcement date")
        announcementDate: String,
    ) {
        // Fake implementation instead of real publishing at the website and on Facebook
        println("Announcement at $announcementDate:\n$announcementContent")
    }
}