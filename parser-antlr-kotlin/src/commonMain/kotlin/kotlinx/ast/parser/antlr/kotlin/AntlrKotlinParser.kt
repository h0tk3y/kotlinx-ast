package kotlinx.ast.parser.antlr.kotlin

import kotlinx.ast.common.AstParser
import kotlinx.ast.common.AstParserType
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.Issue
import kotlinx.ast.common.ast.Ast
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.Lexer
import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.TokenStream
import org.antlr.v4.kotlinruntime.tree.ParseTree

abstract class AntlrKotlinParser<P : Parser, Type : AstParserType>(
    private val extractor: AntlrKotlinParserExtractor<P, Type>,
    private val lexerFactory: (CharStream) -> Lexer,
    private val parserFactory: (TokenStream) -> P
) : AstParser<P, ParseTree, Type> {

    override fun parse(source: AstSource, type: Type): Ast =
        parse(source, type, mutableListOf())

    override fun parse(source: AstSource, type: Type, issues: MutableList<Issue>): Ast {
        return antlrKotlinParser(source, extractor, type, lexerFactory, parserFactory, issues)
    }

    override fun parse(source: AstSource, types: List<Type>): List<Ast> =
        parse(source, types, mutableListOf())

    override fun parse(source: AstSource, types: List<Type>, issues: MutableList<Issue>): List<Ast> {
        return antlrKotlinParser(source, extractor, types, lexerFactory, parserFactory, issues)
    }
}
