package com.github.darkxanter.kdatamapper.annotation

import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
public annotation class DataMapper(
    /**
     * Mapping `ThisClass.toToClass()`
     * */
    val toClasses: Array<KClass<*>> = [],
    /**
     * Mapping `FromClass.toThisClass()`
     * */
    val fromClasses: Array<KClass<*>> = [],
    /**
     * Missing properties will be added as arguments to a mapping function
     *
     * If `false` it will throw an error for missing properties
     * */
    val missingPropertiesAsArguments: Boolean = true,
)
