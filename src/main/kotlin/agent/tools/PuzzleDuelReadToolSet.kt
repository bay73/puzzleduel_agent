package com.bay.agent.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Tools for retrieving information from Puzzle Duel web site")
class PuzzleDuelReadToolSet(private val httpClient: HttpClient) : ToolSet {
    @Tool("list_of_competitions")
    @LLMDescription("Extracts list of all competitions")
    fun getCompetitionsList(): String {
        val document = httpClient.get("https://www.puzzleduel.club/contest")
        val data = document.select("#contest-list").html()
        return data
    }


    @Tool("full_competition_results")
    @LLMDescription("Extracts full competition results")
    fun getFullCompetitionResults(
        @LLMDescription("Competition identifier")
        competitionIdentifier: String,
    ): String {
        val document = httpClient.get("https://www.puzzleduel.club/contest/$competitionIdentifier/results")
        val data = document.select("#contest-results").html()
        return data
    }

    @Tool("competition_description")
    @LLMDescription("Extracts full competition description")
    fun getCompetitionDescription(
        @LLMDescription("Competition identifier")
        competitionIdentifier: String,
    ): String {
        val document = httpClient.get("https://www.puzzleduel.club/contest/$competitionIdentifier")
        val data = document.select("#contest-description").html()
        return data
    }

    @Tool("competition_puzzles")
    @LLMDescription("Extracts list competition puzzles")
    fun getCompetitionPuzzles(
        @LLMDescription("Competition identifier")
        competitionIdentifier: String,
    ): String {
        val document = httpClient.get("https://www.puzzleduel.club/contest/$competitionIdentifier")
        val data = document.select("#contest-puzzles").html()
        return data
    }
}