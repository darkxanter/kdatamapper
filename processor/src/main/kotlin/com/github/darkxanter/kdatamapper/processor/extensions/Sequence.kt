package com.github.darkxanter.kdatamapper.processor.extensions

internal fun <T : Any> Sequence<T>.isEmpty(): Boolean = !iterator().hasNext()
