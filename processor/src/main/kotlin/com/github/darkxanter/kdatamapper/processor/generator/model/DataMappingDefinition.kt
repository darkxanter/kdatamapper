package com.github.darkxanter.kdatamapper.processor.generator.model

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal data class DataMappingDefinition(
    val from: KSClassDeclaration,
    val to: KSClassDeclaration,
    val missingPropertiesAsArguments: Boolean,
)
