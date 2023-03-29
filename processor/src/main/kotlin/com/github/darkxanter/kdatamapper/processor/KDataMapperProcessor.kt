package com.github.darkxanter.kdatamapper.processor

import com.github.darkxanter.kdatamapper.annotation.DataMapper
import com.github.darkxanter.kdatamapper.processor.extensions.getSymbolsWithAnnotation
import com.github.darkxanter.kdatamapper.processor.extensions.panic
import com.github.darkxanter.kdatamapper.processor.generator.DataMapperVisitor
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate

public class KDataMapperProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("KDataMapper processing round start")
        return processDataMapper(resolver).also {
            logger.info("KDataMapper processing round end")
        }
    }

    override fun finish() {
        logger.info("KDataMapper processing finished")
    }


    private fun processDataMapper(resolver: Resolver): List<KSAnnotated> {
        val visitor = DataMapperVisitor(codeGenerator, logger)
        val resolvedSymbols = resolver.getSymbolsWithAnnotation<DataMapper>()
        return processSymbols(resolvedSymbols, visitor) { symbol ->
            when {
                symbol is KSClassDeclaration && symbol.classKind == ClassKind.CLASS && !symbol.isAbstract() -> {
                }

                else -> {
                    logger.panic("@MapperExtension can be applied only to non abstract class")
                }
            }
        }
    }

    private fun processSymbols(
        resolvedSymbols: Sequence<KSAnnotated>,
        visitor: KSVisitorVoid,
        test: (KSAnnotated) -> Unit,
    ): List<KSAnnotated> {
        resolvedSymbols.filter { it.validate() }.forEach { classDeclaration ->
            test(classDeclaration)
            classDeclaration.accept(visitor, Unit)
        }
        return resolvedSymbols.filterNot { it.validate() }.toList()
    }
}
