package kotlinx.ast.parser.antlr.java

import kotlinx.ast.common.Point
import kotlinx.ast.common.Position
import kotlinx.ast.common.Source
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.TerminalNode

val Token.startPoint: Point
    get() = Point(this.line, this.charPositionInLine)

val Token.endPoint: Point
    get() = if (this.type == Token.EOF) startPoint else startPoint + this.text

fun Token.toPosition(considerPosition: Boolean = true, source: Source? = null): Position? =
    if (considerPosition) Position(this.startPoint, this.endPoint, source) else null

val ParserRuleContext.position: Position
    get() = Position(start.startPoint, stop.endPoint)

/**
 * Returns the position of the receiver parser rule context.
 * @param considerPosition if it's false, this method returns null.
 */
fun ParserRuleContext.toPosition(considerPosition: Boolean = true, source: Source? = null): Position? {
    return if (considerPosition && start != null && stop != null) {
        val position = position
        if (source == null) position else Position(position.start, position.end, source)
    } else {
        null
    }
}

fun TerminalNode.toPosition(considerPosition: Boolean = true, source: Source? = null): Position? =
    this.symbol.toPosition(considerPosition, source)