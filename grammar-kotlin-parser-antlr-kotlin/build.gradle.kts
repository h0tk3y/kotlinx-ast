import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":grammar-kotlin-parser-common"))
                api(project(":parser-antlr-kotlin"))
            }
            kotlin.srcDirs("src/commonAntlr/kotlin")
        }

        val jvmTest by getting {
            dependencies {
                api(project(":grammar-kotlin-parser-test"))
            }
        }
    }
}

tasks.register<com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask>("generateGrammarSource") {
    antlrClasspath = configurations.detachedConfiguration(
        project.dependencies.create("org.antlr:antlr4:${Versions.antlrUsedByAntlrKotlin}"),
        project.dependencies.create("${Versions.antlrKotlinGroup}:antlr-kotlin-target:${Versions.antlrKotlin}")
    )
    maxHeapSize = "64m"
    packageName = "kotlinx.ast.grammar.kotlin.target.antlr.kotlin.generated"
    arguments = listOf("-no-visitor", "-no-listener")
    source = project.objects
        .sourceDirectorySet("commonAntlr", "commonAntlr")
        .srcDir("../grammar-kotlin-parser-common/src/commonAntlr/antlr").apply {
            include("*.g4")
        }
    outputDirectory = File("src/commonAntlr/kotlin")
}

tasks.withType(Test::class.java).all {
    useJUnitPlatform {}
    maxHeapSize = "4g"
    testLogging {
        showStandardStreams = true
        events = TestLogEvent.values().toSet()
    }
}
