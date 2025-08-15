package org.intellij.markdown.flavours.custom.hline

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockImpl

class CustomHLineMarkerBlock(
    constraints: MarkdownConstraints,
    productionHolder: ProductionHolder,
) : MarkerBlockImpl(constraints, productionHolder.mark()) {
    override fun doProcessToken(pos: LookaheadText.Position, currentConstraints: MarkdownConstraints): MarkerBlock.ProcessingResult = MarkerBlock.ProcessingResult.DEFAULT

    override fun isInterestingOffset(pos: LookaheadText.Position): Boolean  = pos.offsetInCurrentLine == -1

    override fun getDefaultAction(): MarkerBlock.ClosingAction = MarkerBlock.ClosingAction.DONE

    override fun calcNextInterestingOffset(pos: LookaheadText.Position): Int = pos.nextLineOrEofOffset

    override fun getDefaultNodeType(): IElementType = MarkdownTokenTypes.HORIZONTAL_RULE

    override fun allowsSubBlocks(): Boolean = false

    companion object {
        private const val REQUIRED_DASH_COUNT = 5

        fun match(pos: LookaheadText.Position): Boolean {
            val line = pos.currentLine

            // Skip leading whitespace
            var offset = line.takeWhile { it.isWhitespace() }.length

            // Count consecutive dash characters
            var dashCount = 0
            while (offset < line.length && line[offset] == '-' && dashCount < REQUIRED_DASH_COUNT) {
                offset++
                dashCount++
            }

            // Must have exactly the required number of dashes
            if (dashCount != REQUIRED_DASH_COUNT) {
                return false
            }

            // The rest of the line must be blank (only whitespace or empty)
            if (line.substring(offset).isNotBlank()) {
                return false
            }

            return true
        }
    }
}