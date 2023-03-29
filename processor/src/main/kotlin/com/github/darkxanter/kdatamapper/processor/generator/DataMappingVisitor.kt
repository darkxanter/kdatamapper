package com.github.darkxanter.kdatamapper.processor.generator

import com.github.darkxanter.kdatamapper.annotation.DataMapper
import com.github.darkxanter.kdatamapper.processor.extensions.getArgumentValue
import com.github.darkxanter.kdatamapper.processor.extensions.getKSClassDeclarations
import com.github.darkxanter.kdatamapper.processor.extensions.panic
import com.github.darkxanter.kdatamapper.processor.generator.model.DataMappingDefinition
import com.github.darkxanter.kdatamapper.processor.helpers.addCodeBlock
import com.github.darkxanter.kdatamapper.processor.helpers.addFunction
import com.github.darkxanter.kdatamapper.processor.helpers.addParameter
import com.github.darkxanter.kdatamapper.processor.helpers.addReturn
import com.github.darkxanter.kdatamapper.processor.helpers.createFile
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

internal class DataMapperVisitor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        logger.info("visit", classDeclaration)

        val mappings = getMappings(classDeclaration)
        logger.info("mappings $mappings")

        writeFile(classDeclaration) {
            mappings.forEach {
                generateMapping(it)
            }
        }
    }

    private fun FileSpec.Builder.generateMapping(mapping: DataMappingDefinition) {
        val fromProperties = mapping.from.getDeclaredProperties().associateBy { it.simpleName.asString() }
        val toProperties = mapping.to.primaryConstructor?.parameters
            ?: logger.panic("Missing primary constructor for ${mapping.to.qualifiedName?.asString()}")
        val (present, missing) = toProperties.partition {
            val name = it.name ?: logger.panic("Parameter name is null for ${mapping.to.qualifiedName?.asString()}")
            fromProperties.containsKey(name.asString())
        }
        val (missingWithDefault, missingWithoutDefault) = missing.partition { it.hasDefault }

        logger.info("present $present missing $missing")
        logger.info("missingWithDefault $missingWithDefault missingWithoutDefault $missingWithoutDefault")

        if (!mapping.missingPropertiesAsArguments && missingWithoutDefault.isNotEmpty()) {
            logger.panic(
                "Missing $missing properties to map " +
                    "from ${mapping.from.qualifiedName?.asString()} to ${mapping.to.qualifiedName?.asString()}"
            )
        }

        addMappingFunction(
            from = mapping.from.toClassName(),
            to = mapping.to.toClassName(),
            properties = toProperties,
            arguments = missing,
        )

        if (missingWithDefault.isNotEmpty()) {
            addMappingFunction(
                from = mapping.from.toClassName(),
                to = mapping.to.toClassName(),
                properties = toProperties - missingWithDefault,
                arguments = missingWithoutDefault,
            )
        }
//        addFunction("to${toClassName.simpleName}") {
//            receiver(mapping.from.toClassName())
//
//            missing.forEach { property ->
//                val type = property.type.toTypeName()
//                addParameter(property.name!!.asString(), type) {
//                    if (type.isNullable) {
//                        defaultValue("%L", null)
//                    }
//                }
//            }
//            addReturn()
//            addCodeBlock {
//                add("%T(\n", toClassName)
//                indent()
//                toProperties.map { it.name!!.asString() }.forEach { property ->
//                    addStatement("$property = $property,")
//                }
//                unindent()
//                add(")\n")
//            }
//        }
    }

    private fun FileSpec.Builder.addMappingFunction(
        from: ClassName,
        to: ClassName,
        properties: Iterable<KSValueParameter>,
        arguments: Iterable<KSValueParameter>,
    ) {
        addFunction("to${to.simpleName}") {
            receiver(from)

            arguments.forEach { property ->
                val type = property.type.toTypeName()
                addParameter(property.name!!.asString(), type) {
                    if (type.isNullable) {
                        defaultValue("%L", null)
                    }
                }
            }
            addReturn()
            addCodeBlock {
                add("%T(\n", to)
                indent()
                properties.map { it.name!!.asString() }.forEach { property ->
                    addStatement("$property = $property,")
                }
                unindent()
                add(")\n")
            }
        }
    }


    private fun getMappings(hostClassDeclaration: KSClassDeclaration): List<DataMappingDefinition> {
        return hostClassDeclaration.annotations.filter {
            it.shortName.asString() == DataMapper::class.simpleName
        }.flatMap { ksAnnotation ->
            val fromClasses = ksAnnotation.getKSClassDeclarations(DataMapper::fromClasses.name)
            val toClasses = ksAnnotation.getKSClassDeclarations(DataMapper::toClasses.name)
            val missingPropertiesAsArguments = ksAnnotation.getArgumentValue(
                DataMapper::missingPropertiesAsArguments.name
            ) ?: false

            val from = fromClasses.map {
                DataMappingDefinition(
                    from = it,
                    to = hostClassDeclaration,
                    missingPropertiesAsArguments = missingPropertiesAsArguments,
                )
            }

            val to = toClasses.map {
                DataMappingDefinition(
                    from = hostClassDeclaration,
                    to = it,
                    missingPropertiesAsArguments = missingPropertiesAsArguments,
                )
            }
            from + to
        }.toList()
    }

    private inline fun writeFile(
        classDeclaration: KSClassDeclaration,
        crossinline builder: FileSpec.Builder.() -> Unit,
    ) {
        writeFile(classDeclaration, "${classDeclaration.simpleName.asString()}DataMappings", builder)
    }

    private inline fun writeFile(
        classDeclaration: KSClassDeclaration,
        fileName: String,
        crossinline builder: FileSpec.Builder.() -> Unit,
    ) {
        createFile(classDeclaration.packageName.asString(), fileName, builder).writeTo(
            codeGenerator,
            aggregating = false,
            originatingKSFiles = listOfNotNull(
                classDeclaration.containingFile
            )
        )
    }
}
