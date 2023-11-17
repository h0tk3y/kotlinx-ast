package kotlinx.ast.common

import kotlinx.ast.common.ast.Ast

enum class IssueType {
    LEXICAL,
    SYNTACTIC,
    SEMANTIC,
    TRANSLATION
}

enum class IssueSeverity {
    ERROR,
    WARNING,
    INFO
}

data class Issue(
    val type: IssueType,
    val message: String,
    val severity: IssueSeverity = IssueSeverity.ERROR,
    val position: Position? = null,
    val ast: Ast? = null
) {

    companion object {
        fun lexical(
            message: String,
            severity: IssueSeverity = IssueSeverity.ERROR,
            position: Position? = null
        ): Issue = Issue(IssueType.LEXICAL, message, severity, position)
        fun syntactic(
            message: String,
            severity: IssueSeverity = IssueSeverity.ERROR,
            position: Position? = null,
            ast: Ast? = null
        ): Issue = Issue(IssueType.SYNTACTIC, message, severity, position, ast)
        fun semantic(
            message: String,
            severity: IssueSeverity = IssueSeverity.ERROR,
            position: Position? = null
        ): Issue = Issue(IssueType.SEMANTIC, message, severity, position)
        fun translation(
            message: String,
            severity: IssueSeverity = IssueSeverity.ERROR,
            position: Position? = null
        ): Issue = Issue(IssueType.TRANSLATION, message, severity, position)
    }
}