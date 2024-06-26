package example

import example.dto.ArticleCreateDto
import example.dto.ArticleUpdateDto
import example.dto.toArticleEntity
import example.dto.toArticleTitleDto
import example.dto.toArticleWithCommentsDto

fun main() {
    val articleCreateDto = ArticleCreateDto(
        title = "Some Title",
        content = "This is content",
        tags = listOf("test", "dto")
    )

    val articleUpdateDto = ArticleUpdateDto(
        id = 10,
        title = "Some Title # 10",
        content = "Some content",
        tags = listOf("test", "dto", "update")
    )

    printDivider()

    println(articleCreateDto)
    println(articleUpdateDto)
    val articleEntity = articleCreateDto.toArticleEntity(1)

    val updatedArticleEntity = articleUpdateDto.toArticleEntity()

    println(articleEntity)
    println(updatedArticleEntity)

    printDivider()

    val articleTitleDto = articleEntity.toArticleTitleDto()
    val articleWithCommentsDto = articleEntity.toArticleWithCommentsDto(10)
    val articleWithCommentsDto2 = articleEntity.toArticleWithCommentsDto(25, listOf("Test!"))

    println(articleTitleDto)
    println(articleWithCommentsDto)
    println(articleWithCommentsDto2)

    printDivider()
    println(articleWithCommentsDto.toArticleTitleDto())
}

fun printDivider() = println("=".repeat(20))
