plugins {
    id("com.github.darkxanter.library-convention")
}

description = "KDataMapper core package"

tasks.jar {
    archiveBaseName.set("kdatamapper-${archiveBaseName.get()}")
}
