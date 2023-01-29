package com.github.darkxanter.kdatamapper.processor.extensions

import com.google.devtools.ksp.symbol.KSType

internal fun KSType.isMatched(vararg reference: String): Boolean {
    return reference.contains(declaration.qualifiedName?.asString())
}

internal val KSType.simpleName: String get() = declaration.simpleName.asString()

internal fun KSType.getFirstArgumentType(): KSType {
    val argument = arguments.firstOrNull() ?: error("Type $this has no arguments")
    val argumentType = argument.type?.resolve() ?: error("Argument $argument has no type")
    if (isMarkedNullable) {
        return argumentType.makeNullable()
    }
    return argumentType
}

internal fun KSType.unwrapEntityId(): KSType {
    return if (isMatched("org.jetbrains.exposed.dao.id.EntityID")) {
        getFirstArgumentType()
    } else {
        this
    }
}
