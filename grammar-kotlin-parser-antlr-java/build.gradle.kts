import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm")
    antlr
    java
    `maven-publish`
}

kotlin {
    target {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType(Test::class.java).all {
    useJUnitPlatform {}
    maxHeapSize = "4g"
    testLogging {
        showStandardStreams = true
        events = TestLogEvent.values().toSet()
    }
}

dependencies {
    antlr("org.antlr:antlr4:${Versions.antlrJava}")
    api(project(":parser-antlr-java"))
    api(project(":grammar-kotlin-parser-common"))
    testImplementation(project(":grammar-kotlin-parser-test"))
}

tasks.generateGrammarSource {
    arguments = arguments + listOf(
        "-no-listener",
        "-no-visitor",
        "-package",
        "kotlinx.ast.grammar.kotlin.target.antlr.java.generated"
    )
    source = project.objects
        .sourceDirectorySet("antlr", "antlr")
        .srcDir("$projectDir/../grammar-kotlin-parser-common/src/commonAntlr/antlr").apply {
            include("*.g4")
        }
    outputDirectory = File("$projectDir/src/main/java/kotlinx/ast/grammar/kotlin/target/antlr/java/generated")
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.compileTestKotlin {
    dependsOn(tasks.generateTestGrammarSource)
}