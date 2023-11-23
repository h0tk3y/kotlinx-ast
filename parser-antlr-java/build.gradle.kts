plugins {
    kotlin("jvm")
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

dependencies {
    api(project(":common"))
    api("org.antlr:antlr4:${Versions.antlrJava}")
}
