package de.fpolachowski.papercurator.etl

import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.stereotype.Service

@Service
class OllamaScribe(
    private val chatModel: OllamaChatModel
) {

    fun generateContextPrompt(query: String, context : String, template : String) : Prompt {
        val promptTemplate = PromptTemplate(template)
        val prompt = promptTemplate.create(mapOf("context" to context, "query" to query))
        return prompt
    }

    fun generateWithContext(prompt : Prompt): ChatResponse? {
        return this.chatModel.call(prompt)
    }
}