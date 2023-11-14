package kotlinx.ast.grammar.kotlin.test

import kotlinx.ast.test.pathOf
import kotlinx.ast.test.readTextOrNull
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

private val path = listOf(
    pathOf("../grammar-kotlin-parser-test/src/commonMain/resources/testdata"),
    pathOf("grammar-kotlin-parser-test/src/commonMain/resources/testdata")
).find { path ->
    Files.isDirectory(path)
}

data class TestData(
    val name: String,
    val kotlinFile: File,
    val kotlinContent: String,
    val rawAstFile: File,
    val rawAstContent: String?,
    val rawInfoFile: File,
    val rawInfoContent: String?,
    val summaryFile: File,
    val summaryContent: String?,
    val summaryInfoFile: File,
    val summaryInfoContent: String?,
)

private fun File.sourceFile(suffix: String): File {
    return File(parentFile, name.replace(".kt.txt", suffix))
}

internal fun testData(): List<TestData> {
    if (path == null) {
        return emptyList()
    }
    val testData = Files.list(path).toList().map(Path::toFile)
        .filter { file -> file.name.endsWith(".kt.txt") }
        .filter { file -> file.name.contains("Error.") } // TODO: remove
        .mapNotNull { kotlinFile ->
            val kotlinContent = kotlinFile.readTextOrNull()
            if (kotlinContent == null) {
                null
            } else {
                val rawAstFile = kotlinFile.sourceFile(".raw.ast.txt")
                val rawInfoFile = kotlinFile.sourceFile(".raw.info.txt")
                val summaryFile = kotlinFile.sourceFile(".summary.ast.txt")
                val summaryInfoFile = kotlinFile.sourceFile(".summary.info.txt")
                TestData(
                    name = kotlinFile.nameWithoutExtension,
                    kotlinFile = kotlinFile,
                    kotlinContent = kotlinContent,
                    rawAstFile = rawAstFile,
                    rawAstContent = rawAstFile.readTextOrNull(),
                    rawInfoFile = rawInfoFile,
                    rawInfoContent = rawInfoFile.readTextOrNull(),
                    summaryFile = summaryFile,
                    summaryContent = summaryFile.readTextOrNull(),
                    summaryInfoFile = summaryInfoFile,
                    summaryInfoContent = summaryInfoFile.readTextOrNull(),
                )
            }
        }
    return testData
}
