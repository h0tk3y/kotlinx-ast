package kotlinx.ast.parser.antlr.java

import kotlinx.ast.common.AstSource
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import org.antlr.v4.runtime.misc.ParseCancellationException
import java.util.*

data class AntlrJavaErrorListener(val filename: String) : BaseErrorListener() {

    constructor(source: AstSource) : this(source.description)

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        throw ParseCancellationException("$filename:$line:$charPositionInLine: $msg")
    }

    override fun reportAmbiguity(
        recognizer: Parser?,
        dfa: DFA?,
        startIndex: Int,
        stopIndex: Int,
        exact: Boolean,
        ambigAlts: BitSet?,
        configs: ATNConfigSet?
    ) {
        super.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs)
    }

    override fun reportAttemptingFullContext(
        recognizer: Parser?,
        dfa: DFA?,
        startIndex: Int,
        stopIndex: Int,
        conflictingAlts: BitSet?,
        configs: ATNConfigSet?
    ) {
        super.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs)
    }

    override fun reportContextSensitivity(
        recognizer: Parser?,
        dfa: DFA?,
        startIndex: Int,
        stopIndex: Int,
        prediction: Int,
        configs: ATNConfigSet?
    ) {
        super.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs)
    }
}
