@file:Suppress("unused", "TooManyFunctions")

package com.github.darkxanter.kdatamapper.processor.helpers

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import kotlin.reflect.KClass

// FileSpec helpers

internal inline fun createFile(
    packageName: String,
    fileName: String,
    crossinline builder: FileSpec.Builder.() -> Unit,
): FileSpec {
    return FileSpec.builder(packageName, fileName).apply {
        suppressWarnings()
        addFileComment("This file was generated by KDataMapper.\n")
        addFileComment("Do not modify this file.")
    }.apply(builder).build()
}

internal inline fun FileSpec.Builder.addInterface(
    className: ClassName,
    crossinline builder: TypeSpec.Builder.() -> Unit,
): FileSpec.Builder = addInterface(className.simpleName, builder)

internal inline fun FileSpec.Builder.addInterface(
    className: String,
    crossinline builder: TypeSpec.Builder.() -> Unit,
): FileSpec.Builder = addType(createInterface(className, builder))

internal inline fun FileSpec.Builder.addClass(
    className: ClassName,
    crossinline builder: TypeSpec.Builder.() -> Unit,
): FileSpec.Builder = addClass(className.simpleName, builder)

internal inline fun FileSpec.Builder.addClass(
    className: String,
    crossinline builder: TypeSpec.Builder.() -> Unit,
): FileSpec.Builder = addType(createClass(className, builder))

internal inline fun FileSpec.Builder.addFunction(
    name: String,
    crossinline builder: FunSpec.Builder.() -> Unit,
): FileSpec.Builder = addFunction(createFunction(name, builder))

internal inline fun FileSpec.Builder.addFunction(
    name: MemberName,
    crossinline builder: FunSpec.Builder.() -> Unit,
): FileSpec.Builder = addFunction(createFunction(name.simpleName, builder))

internal fun FileSpec.Builder.addImport(
    type: KSType,
) = addImport(type.declaration.packageName.asString(), type.declaration.simpleName.asString())

internal inline fun <reified Type : Any> FileSpec.Builder.addImport() {
    val typeName = Type::class.asTypeName()
    addImport(typeName.packageName, typeName.simpleName)
}

internal fun FileSpec.Builder.suppressWarnings() {
    addAnnotation(
        createAnnotation(Suppress::class) {
            useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)

            addMember("%S", "unused")
            addMember("%S", "RedundantVisibilityModifier")
            addMember("%S", "UnusedReceiverParameter")
            addMember("%S", "RedundantUnitReturnType")
            addMember("%S", "MemberVisibilityCanBePrivate")
            addMember("%S", "MatchingDeclarationName")
            addMember("%S", "FunctionParameterNaming")
        }
    )
}

// FunSpec helpers

internal inline fun createFunction(
    functionName: String,
    crossinline builder: FunSpec.Builder.() -> Unit,
): FunSpec = FunSpec.builder(functionName).apply(builder).build()

internal inline fun FunSpec.Builder.addCodeBlock(
    crossinline builder: CodeBlock.Builder.() -> Unit,
): FunSpec.Builder = addCode(buildCodeBlock(builder))

internal inline fun createConstructor(
    crossinline builder: FunSpec.Builder.() -> Unit,
): FunSpec = FunSpec.constructorBuilder().apply(builder).build()

internal inline fun createParameter(
    name: String,
    type: TypeName,
    crossinline builder: ParameterSpec.Builder.() -> Unit,
): ParameterSpec = ParameterSpec.builder(name, type).apply(builder).build()

internal fun FunSpec.Builder.addParameter(
    name: String,
    type: TypeName,
    builder: ParameterSpec.Builder.() -> Unit,
): FunSpec.Builder = addParameter(createParameter(name, type, builder))

internal fun FunSpec.Builder.addReturn() = addCode("return ")

// TypeSpec helpers

internal inline fun createInterface(
    className: String,
    crossinline builder: TypeSpec.Builder.() -> Unit,
): TypeSpec = TypeSpec.interfaceBuilder(className).apply(builder).build()

internal inline fun createClass(
    className: String,
    crossinline builder: TypeSpec.Builder.() -> Unit,
): TypeSpec = TypeSpec.classBuilder(className).apply(builder).build()

internal inline fun TypeSpec.Builder.addPrimaryConstructor(
    crossinline builder: FunSpec.Builder.() -> Unit,
): TypeSpec.Builder = primaryConstructor(createConstructor(builder))

internal fun TypeSpec.Builder.addProperty(
    name: String,
    type: TypeName,
    builder: PropertySpec.Builder.() -> Unit,
): TypeSpec.Builder = addProperty(createProperty(name, type, builder))

internal inline fun TypeSpec.Builder.addFunction(
    name: String,
    crossinline builder: FunSpec.Builder.() -> Unit,
): TypeSpec.Builder = addFunction(createFunction(name, builder))

// PropertySpec helpers

internal fun createProperty(
    name: String,
    type: TypeName,
    builder: PropertySpec.Builder.() -> Unit,
) = PropertySpec.builder(name, type).apply(builder).build()

// CodeBloc helpers

internal fun CodeBlock.Builder.addReturn() = add("return ")

internal fun CodeBlock.Builder.endControlFlow(returnStatement: String, addLineBreak: Boolean = true) {
    unindent()
    add("}$returnStatement")
    if (addLineBreak) add("\n")
}

internal fun CodeBlock.Builder.endControlFlow(addLineBreak: Boolean) {
    unindent()
    add("}")
    if (addLineBreak) add("\n")
}

// Annotation helpers

internal inline fun createAnnotation(
    type: ClassName,
    builder: AnnotationSpec.Builder.() -> Unit,
): AnnotationSpec {
    return AnnotationSpec
        .builder(type)
        .apply(builder)
        .build()
}

internal inline fun createAnnotation(
    type: KClass<out Annotation>,
    builder: AnnotationSpec.Builder.() -> Unit,
): AnnotationSpec {
    return AnnotationSpec
        .builder(type)
        .apply(builder)
        .build()
}
