package kotlinx.ast.parser.antlr.java

import kotlinx.ast.common.AstChannel
import kotlinx.ast.common.AstParserType
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.*
import kotlinx.ast.common.impl.AstList
import kotlinx.ast.common.impl.flatten
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

private class AntlrJavaParserImpl(
    private val stream: CommonTokenStream,
    private val parserNames: Array<String>,
    private val tokenNames: Array<String?>,
    private val channels: Array<AstChannel>
) {
    private val consumedHiddenTokens = mutableSetOf<Token>()
    private val defaultChannel = channels.first()

    private fun toAstTerminal(token: Token): AstTerminal {
        val text = token.text ?: throw RuntimeException()
        val info = AstInfo(
            id = token.tokenIndex,
            start = AstInfoPosition(
                index = token.startIndex,
                line = token.line,
                row = token.charPositionInLine + 1,
            ),
            stop = AstInfoPosition(
                index = token.stopIndex + 1,
                line = token.line,
                row = token.charPositionInLine + 1 + text.length,
            ),
        )
        val name = when (token.type) {
            -1 ->
                "EOF"
            in tokenNames.indices ->
                tokenNames[token.type]
            else ->
                null
        } ?: "<Invalid>"
        val channel = channels[token.channel]
        return DefaultAstTerminal(name, text, channel).withAstInfo(info)
    }

    private fun hiddenTokens(node: ParseTree, start: Boolean): List<AstTerminal> {
        return when (node) {
            is ParserRuleContext -> {
                val token = if (start) {
                    node.start
                } else {
                    node.stop
                }
                hiddenTokens(token, start)
            }
            is TerminalNode ->
                hiddenTokens(node.symbol, start)
            else ->
                emptyList()
        }
    }

    private fun hiddenTokens(token: Token?, left: Boolean): List<AstTerminal> {
        val index = token?.tokenIndex
        val list = when {
            index == null ->
                null
            left ->
                stream.getHiddenTokensToLeft(index)
            else ->
                stream.getHiddenTokensToRight(index)
        } ?: emptyList()

        val result = list.filterNot(consumedHiddenTokens::contains)
        result.forEach {
            consumedHiddenTokens.add(it)
        }

        return result.map(::toAstTerminal)
    }

    fun parse(node: ParseTree): AstList {
        val start = hiddenTokens(node, start = true)
        val stop = hiddenTokens(node, start = false)
        val ast = when (node) {
            is ParserRuleContext -> {
                val name = parserNames[node.ruleIndex]
                val children = (node.children ?: emptyList<ParseTree>()).flatMap { children ->
                    parse(children).flatten().map { ast ->
                        ast.flatten(defaultChannel)
                    }.flatten()
                }
                val info = children
                    .filterIsInstance<AstWithAstInfo>()
                    .mapNotNull(AstWithAstInfo::info)
                    .fold(emptyAstInfo) { left, right ->
                        left + right
                    }
                DefaultAstNode(name, children).withAstInfo(info)
            }
            is TerminalNode -> {
                toAstTerminal(node.symbol ?: throw RuntimeException())
            }
            else ->
                throw RuntimeException()
        }
        return AstList(start, ast, stop)
    }
}

fun <P : Parser, Type : AstParserType> antlrJavaParser(
    source: AstSource,
    extractor: AntlrJavaParserExtractor<P, Type>,
    type: Type,
    lexerFactory: (CharStream) -> Lexer,
    parserFactory: (TokenStream) -> P
): Ast {
    val result = antlrJavaParser(source, extractor, listOf(type), lexerFactory, parserFactory)
    if (result.size != 1) {
        throw RuntimeException("expected exactly one ast!")
    }
    return result.first()
}

fun <P : Parser, Type : AstParserType> antlrJavaParser(
    source: AstSource,
    extractor: AntlrJavaParserExtractor<P, Type>,
    types: List<Type>,
    lexerFactory: (CharStream) -> Lexer,
    parserFactory: (TokenStream) -> P
): List<Ast> {
    val listener = AntlrJavaErrorListener(source)
    val input = source.toAntlrJavaCharStream()
    val lexer = lexerFactory(input)
//    lexer.removeErrorListeners()
//    lexer.addErrorListener(listener)
    val stream = CommonTokenStream(lexer)
    val parser = parserFactory(stream)
//    parser.removeErrorListeners()
//    parser.addErrorListener(listener)
    val ruleNames = parser.ruleNames ?: emptyArray()
    val channelNames = lexer.channelNames ?: emptyArray()
    val channels = channelNames.withIndex().map { (i, channel) ->
        AstChannel(i, channel)
    }.toTypedArray()
    val vocabulary = lexer.vocabulary
    val tokenNames = Array(vocabulary.maxTokenType + 1) {
        vocabulary.getSymbolicName(it)
    }
    val astParser = AntlrJavaParserImpl(stream, ruleNames, tokenNames, channels)
    val result = types.mapNotNull { type ->
        if (type.toString() == "token") {
            val token = stream.LT(1)
            if (token == null) {
                null
            } else {
                if (token.type != -1) {
                    stream.consume()
                }
                DefaultAstTerminal(tokenNames[token.type] ?: "", token.text ?: "", channels[token.channel])
            }
        } else {
            astParser.parse(extractor.extract(parser, type)).join()
        }
    }
    if (stream.LA(1) != -1) {
        throw RuntimeException("trailing data while parsing types \"${types.joinToString("\", \"")}\"")
    }
    return result
}
