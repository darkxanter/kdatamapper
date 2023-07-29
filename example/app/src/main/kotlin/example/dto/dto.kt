package example.dto

import com.github.darkxanter.kdatamapper.annotation.DataMapper
import lib.ArticleEntity

@DataMapper(
    toClasses = [ArticleEntity::class],
)
data class ArticleCreateDto(
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
)

@DataMapper(
    toClasses = [ArticleEntity::class],
)
data class ArticleUpdateDto(
    val id: Long,
    val title: String,
    val content: String,
    val tags: List<String>,
)

@DataMapper(
    fromClasses = [ArticleEntity::class],
    toClasses = [ArticleWithCommentsDto::class],
)
data class ArticleTitleDto(
    val id: Long,
    val title: String,
)

@DataMapper(
    fromClasses = [ArticleEntity::class],
    toClasses = [ArticleTitleDto::class],
)
data class ArticleWithCommentsDto(
    val id: Long,
    val title: String,
    val bookmarkCount: Int,
    val comments: List<String> = emptyList(),
)
