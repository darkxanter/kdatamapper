# KDataMapper

[![Maven Central](https://img.shields.io/maven-central/v/io.github.darkxanter/kdatamapper-core)](https://search.maven.org/artifact/io.github.darkxanter/kdatamapper-core)

The Kotlin Data Mapper (KDataMapper) is a Kotlin Symbol Processing plugin that can generate extension functions to map fields of one class to the primary constructor of another class.

## How To Use

Let's say we have the following entity:

```kotlin
data class ArticleEntity(
    val id: Long,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
)
```

Now we want to have DTOs for create and update entity:

```kotlin
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
```

To create mapping functions, you need to add the `@KDataMapper` annotation and there are several options for this:

- Add annotation to the `ArticleEntity` and set `fromClasses = [ArticleCreateDto::class, ArticleUpdateDto::class]` in
  the `@KDataMapper` annotation

```kotlin
@DataMapper(
    fromClasses = [ArticleCreateDto::class, ArticleUpdateDto::class],
)
data class ArticleEntity(
    val id: Long,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
)
```

- Or add annotations to DTOs and set `toClasses = [ArticleEntity::class]` in each `@KDataMapper` annotation

```kotlin
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
```

When we build the project we'll have:

```kotlin
public fun ArticleCreateDto.toArticleEntity(id: Long) = ArticleEntity(
    id = id,
    title = title,
    content = content,
    tags = tags,
)

public fun ArticleUpdateDto.toArticleEntity() = ArticleEntity(
    id = id,
    title = title,
    content = content,
    tags = tags,
)
```

The `id` parameter has been added to the `ArticleCreateDto.toArticleEntity` function
because it's required in `ArticleEntity` and has no default value.

Next, we want get a DTO with additional field that are not in `ArticleEntity`:

```kotlin
@DataMapper(
    fromClasses = [ArticleEntity::class],
)
data class ArticleWithCommentsDto(
    val id: Long,
    val title: String,
    val bookmarkCount: Int,
    val comments: List<String> = emptyList(),
)
```

After build we get:

```kotlin
public fun ArticleTitleDto.toArticleWithCommentsDto(bookmarkCount: Int, comments: List<String>) =
    ArticleWithCommentsDto(
        id = id,
        title = title,
        bookmarkCount = bookmarkCount,
        comments = comments,
    )

public fun ArticleTitleDto.toArticleWithCommentsDto(bookmarkCount: Int) = ArticleWithCommentsDto(
    id = id,
    title = title,
    bookmarkCount = bookmarkCount,
)
```

Two functions are generated because the `comments` field have a default value.

You can find a complete project example in the `example` subdirectory.

## Setup

Add KSP plugin to your module's `build.gradle.kts`:

```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.7.22-1.0.8"
}
```

Add `Maven Central` to the repositories blocks in your project's `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
}
```

Add `KDataMapper` dependencies:

```kotlin
dependencies {
    implementation("io.github.darkxanter:kdatamapper-core:0.1.0")
    ksp("io.github.darkxanter:kdatamapper-processor:0.1.0")
}
```

To access generated code from KSP, you need to set up the source path into your module's `build.gradle.kts` file:

```kotlin
sourceSets.configureEach {
    kotlin.srcDir("$buildDir/generated/ksp/$name/kotlin/")
}
```

---

License
======

    Copyright 2023 Sergey Shumov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
