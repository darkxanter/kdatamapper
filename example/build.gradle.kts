val exposedVersion: String by project
val sqliteVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    application
}

dependencies {
    implementation(project(":core"))
    ksp(project(":processor"))
}

sourceSets.configureEach {
    kotlin.srcDir("$buildDir/generated/ksp/$name/kotlin/")
}

application {
    mainClass.set("example.AppKt")
}
tasks {
    jar {
        manifest {
            attributes("Main-Class" to application.mainClass.get())
        }
    }
}
