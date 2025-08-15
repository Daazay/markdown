package org.intellij.markdown.flavours.custom.heading

import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.custom.CustomElementTypes
import org.intellij.markdown.flavours.custom.CustomTokenTypes
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockImpl
import org.intellij.markdown.parser.sequentialparsers.SequentialParser

class CustomHeadingMarkerBlock(
    constraints: MarkdownConstraints,
    productionHolder: ProductionHolder,
    range: HeadingRange,
) : MarkerBlockImpl(constraints, productionHolder.mark()) {
    private val nodeType =  run {
        val hashCount = range.first.last - range.first.first
        CustomElementTypes.heading(hashCount - 1)
    }

    init {
        val currPos = productionHolder.currentPosition
        productionHolder.addProduction(
            listOf(
                SequentialParser.Node(
                    currPos + range.first.first..currPos + range.first.last,
                    CustomTokenTypes.HEADING_MARKER
                ),
                SequentialParser.Node(
                    currPos + range.second.first..currPos + range.second.last,
                    CustomTokenTypes.HEADING_CONTENT
                )
            )
        )
    }

    override fun doProcessToken(pos: LookaheadText.Position, currentConstraints: MarkdownConstraints): MarkerBlock.ProcessingResult {
        return if (pos.offsetInCurrentLine == -1) {
            MarkerBlock.ProcessingResult(
                MarkerBlock.ClosingAction.DROP,
                MarkerBlock.ClosingAction.DONE,
                MarkerBlock.EventAction.PROPAGATE
            )
        } else {
            MarkerBlock.ProcessingResult.CANCEL
        }
    }

    override fun isInterestingOffset(pos: LookaheadText.Position): Boolean = true

    override fun getDefaultAction(): MarkerBlock.ClosingAction = MarkerBlock.ClosingAction.DONE

    override fun calcNextInterestingOffset(pos: LookaheadText.Position): Int = pos.nextLineOrEofOffset

    override fun getDefaultNodeType(): IElementType = nodeType

    override fun allowsSubBlocks(): Boolean = false

    companion object {
        private const val MIN_HASHES_COUNT = 2
        private const val MAX_HASHES_COUNT = 7

        fun match(pos: LookaheadText.Position): HeadingRange? {
            val line = pos.currentLine

            // Skip leading whitespace
            var offset = line.takeWhile { it.isWhitespace() }.length

            // Count consecutive hash characters
            var hashCount = 0
            while (offset < line.length && line[offset] == '#' && hashCount <= 7) {
                offset++
                hashCount++
            }

            // Validate hash count (must be between MIN_HASHES and MAX_HASHES)
            if (hashCount !in MIN_HASHES_COUNT .. MAX_HASHES_COUNT) {
                return null
            }


            // Must have at least one whitespace character after hashes
            if (offset >= line.length || !line[offset].isWhitespace()) {
                return null
            }

            val markerRange = offset - hashCount .. offset
            val contentRange = offset..line.length

            return markerRange to contentRange
        }
    }
}