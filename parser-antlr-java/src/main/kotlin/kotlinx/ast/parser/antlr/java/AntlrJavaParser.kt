package kotlinx.ast.parser.antlr.java

import kotlinx.ast.common.AstParser
import kotlinx.ast.common.AstParserType
import kotlinx.ast.common.AstSource
import kotlinx.ast.common.Issue
import kotlinx.ast.common.ast.Ast
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.tree.ParseTree

abstract class AntlrJavaParser<P : Parser, Type : AstParserType>(
    private val extractor: AntlrJavaParserExtractor<P, Type>,
    private val lexerFactory: (CharStream) -> Lexer,
    private val parserFactory: (TokenStream) -> P
) : AstParser<P, ParseTree, Type> {

    override fun parse(source: AstSource, type: Type): Ast {
        return parse(source, type, mutableListOf())
    }

    override fun parse(source: AstSource, type: Type, issues: MutableList<Issue>): Ast {
        return antlrJavaParser(source, extractor, type, lexerFactory, parserFactory, issues)
    }

    override fun parse(source: AstSource, types: List<Type>): List<Ast> {
        return parse(source, types, mutableListOf())
    }

    override fun parse(source: AstSource, types: List<Type>, issues: MutableList<Issue>): List<Ast> {
        return antlrJavaParser(source, extractor, types, lexerFactory, parserFactory, issues)
    }
}
