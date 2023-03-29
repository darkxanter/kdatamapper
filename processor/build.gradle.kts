val kspVersion: String by project
val kotlinpoetVersion: String by project

plugins {
    id("com.github.darkxanter.library-convention")
}

description = "KDataMapper symbol processor"

dependencies {
    implementation(project(":core"))
    implementation("com.squareup:kotlinpoet-ksp:$kotlinpoetVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
}

tasks.jar {
    archiveBaseName.set("kdatamapper-${archiveBaseName.get()}")
}
