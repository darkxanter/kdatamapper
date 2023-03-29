package example.data

import com.github.darkxanter.kdatamapper.annotation.DataMapper

@DataMapper(
    toClasses = [ArticleTitleDto::class, ArticleWithCommentsDto::class],
    fromClasses = [ArticleCreateDto::class, ArticleUpdateDto::class],
)
data class ArticleEntity(
    val id: Long,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
)

data class ArticleCreateDto(
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
)

data class ArticleUpdateDto(
    val id: Long,
    val title: String,
    val content: String,
    val tags: List<String>,
)

@DataMapper(
    toClasses = [ArticleWithCommentsDto::class],
)
data class ArticleTitleDto(
    val id: Long,
    val title: String,
)

@DataMapper(
    toClasses = [ArticleTitleDto::class],
)
data class ArticleWithCommentsDto(
    val id: Long,
    val title: String,
    val bookmarkCount: Int,
    val comments: List<String> = emptyList(),
)
