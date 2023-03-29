package com.github.darkxanter.kdatamapper.processor.extensions

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType

internal inline fun <reified T : Any> KSAnnotation.getArgumentValue(paramName: String): T? {
    return arguments.find { it.name?.asString() == paramName }?.value as? T
}

internal fun KSAnnotation.getKSClassDeclarations(paramName: String): List<KSClassDeclaration> {
    return arguments.find { it.name?.asString() == paramName }?.value?.let { value ->
        if (value is List<*>) {
            value.filterIsInstance<KSType>()
        } else {
            error("value for parameter '$paramName' is not list")
        }
    }?.map {
        it.declaration
    }?.filterIsInstance<KSClassDeclaration>()
        ?: emptyList()
}
