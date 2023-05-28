package kotlinx.ast.grammar.kotlin.common

import kotlinx.ast.common.AstParserType

enum class KotlinGrammarParserType : AstParserType {
    token,
    kotlinFile,
    kotlinScript,
    identifier,
    importList,
    simpleIdentifier,
    `annotation`,
    typeArguments,
    simpleUserType,
    userType,
}
