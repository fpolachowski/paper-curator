# Paper Curator

A simple backend for retrieving and analyzing papers from different sources.
The goal of this project is to provide a hub for quick access to new research articles from different publishers.
To ensure copy right this project does not provide direct access to the paper but short descriptions of the paper and direct links to the publisher itself.

## Components

The project is composed of different components for retrieving, processing, and analyzing the documents.
1. [Document loader](src/main/kotlin/de/fpolachowski/papercurator/etl/DocumentLoader.kt): for extracting relevant information from publishers 
2. [Document reader](src/main/kotlin/de/fpolachowski/papercurator/etl/PaperDocumentReader.kt): for processing the article itself from pdf/html/etc to text based on [spring document reader](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/reader/pdf/PagePdfDocumentReader.html)
3. Document transformer: standard implementation from [spring.ai](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/document/class-use/DocumentTransformer.html)
4. Document writer: a vector store implementation based on [spring.ai.DocumentWriter](https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/document/DocumentWriter.html)
5. Ollama Chat client using RAG Advisor to write short summaries of papers
6. GraphQL Controller for providing client access to the stored paper information

## Current publisher implementations

- [Arxiv](https://arxiv.org/) through their [API](https://info.arxiv.org/help/rss.html)

## Current covered article categories

- Computer Science (CS)

## Dependencies

- Spring for GraphQL
- Ollama Spring Boot (API for interacting with Ollama LLMs)
- Postgres + PgVector (Vector store implementation for postgres)
- PDF Document Reader (specific for pdf files)
- Tika Document Reader (for other files)