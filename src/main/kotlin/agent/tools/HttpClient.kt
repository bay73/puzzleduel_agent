package com.bay.agent.tools

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

interface HttpClient {
    fun get(url: String): Document
}

class JsoupHttpClient : HttpClient {
    override fun get(url: String): Document =
        Jsoup.connect(url).get()
}