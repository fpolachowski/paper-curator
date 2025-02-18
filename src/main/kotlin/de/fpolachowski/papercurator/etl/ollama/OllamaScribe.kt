package de.fpolachowski.papercurator.etl.ollama

import de.fpolachowski.papercurator.model.Document
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.ChatClient.AdvisorSpec
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class OllamaScribe(
    private val chatModel: OllamaChatModel,
    private val vectorStore: VectorStore
) {
    private val chatClient = ChatClient.builder(chatModel)
        .defaultAdvisors(QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().topK(6).build()))
        .build()

    fun generateContextPrompt(query: String, template : String) : Prompt {
        val promptTemplate = PromptTemplate(template)
        val prompt = promptTemplate.create(mapOf("query" to query))
        return prompt
    }

    fun ragByDocument(document : Document, userQuestion : String, template: String): String? {
        val query = generateContextPrompt(userQuestion, template)
        return chatClient.prompt(query)
            .advisors { a: AdvisorSpec -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "fileName == ${document.title}") } //TODO (fix me)
            .call()
            .content()
    }

}