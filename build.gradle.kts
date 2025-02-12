buildscript {
    dependencies {
        // add the antlr-kotlin-gradle-plugin to the classpath
        classpath("${Versions.antlrKotlinGroup}:antlr-kotlin-gradle-plugin:${Versions.antlrKotlin}")
    }
}

plugins {
    base
    kotlin("multiplatform") version Versions.kotlin apply false
}

// a small hack: the variable must be named like the property
// jitpack will pass -Pversion=..., so `val version` is required here.
val version: String by project
// we create an alias here...
val versionProperty = version
// do the same for group
val group: String by project
val groupProperty = if (group.endsWith("kotlinx.ast")) {
    group
} else {
    // just another jitpack hack
    "$group.ast"
}

allprojects {
    // ... because `version` is another var here.
    // when version is hardcoded here, jitpack can not overwrite it.
    // the default version can now be changed in gradle.properties
    version = versionProperty
    group = groupProperty

    repositories {
        mavenLocal()
        mavenCentral()
        // used to download antlr-kotlin runtime
        maven("https://jitpack.io")
    }
}

tasks.withType<Wrapper> {
    gradleVersion = Versions.gradle
    distributionType = Wrapper.DistributionType.ALL
}

// Make the root project archives configuration depend on every subproject
val allProjectDeps = configurations.create("allProjectDeps") {
    isCanBeConsumed = false
    isCanBeResolved = false
}
subprojects.forEach {
    dependencies.add(allProjectDeps.name, it)
}
configurations.getByName("archives") {
    extendsFrom(allProjectDeps)
}
