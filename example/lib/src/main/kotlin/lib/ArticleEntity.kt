package lib

data class ArticleEntity(
    val id: Long,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
)
