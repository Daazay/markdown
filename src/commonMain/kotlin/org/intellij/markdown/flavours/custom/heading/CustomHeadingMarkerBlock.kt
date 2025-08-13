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
    private val markerRange: HeadingMarkerRange,
    private val contentRange: HeadingContentRange,
) : MarkerBlockImpl(constraints, productionHolder.mark()) {
    private val nodeType = when (markerRange.last - markerRange.first) {
        2 -> CustomElementTypes.HEADING_1
        3 -> CustomElementTypes.HEADING_2
        4 -> CustomElementTypes.HEADING_3
        5 -> CustomElementTypes.HEADING_4
        6 -> CustomElementTypes.HEADING_5
        7 -> CustomElementTypes.HEADING_6
        else -> throw IllegalArgumentException("Invalid heading marker range: $markerRange")
    }

    init {
        val currPos = productionHolder.currentPosition
        val nodes: List<SequentialParser.Node> = buildList {
            add(SequentialParser.Node(currPos + markerRange.first..currPos + markerRange.last, CustomTokenTypes.HEADING_MARKER))
            add(SequentialParser.Node(currPos + contentRange.first..currPos + contentRange.last, CustomTokenTypes.HEADING_CONTENT))
        }
        productionHolder.addProduction(nodes)
    }

    override fun doProcessToken(pos: LookaheadText.Position, currentConstraints: MarkdownConstraints): MarkerBlock.ProcessingResult {
        if (pos.offsetInCurrentLine == -1) {
            return MarkerBlock.ProcessingResult(MarkerBlock.ClosingAction.DROP, MarkerBlock.ClosingAction.DONE, MarkerBlock.EventAction.PROPAGATE)
        }
        return MarkerBlock.ProcessingResult.CANCEL
    }

    override fun isInterestingOffset(pos: LookaheadText.Position): Boolean = true

    override fun getDefaultAction(): MarkerBlock.ClosingAction = MarkerBlock.ClosingAction.DONE

    override fun calcNextInterestingOffset(pos: LookaheadText.Position): Int = pos.nextLineOrEofOffset

    override fun getDefaultNodeType(): IElementType = nodeType

    override fun allowsSubBlocks(): Boolean = false

    companion object {
        fun match(pos: LookaheadText.Position): HeadingRange? {
            val line = pos.currentLine

            // trim leading whitespaces
            var offset = line.takeWhile { it.isWhitespace() }.length

            // count hashes
            var hashesCount = 0
            while (offset < line.length && line[offset] == '#' && hashesCount <= 7) {
                offset++
                hashesCount++
            }

            // heading levels starts with 2 hashes and goes up only to 7
            if (hashesCount < 2 || hashesCount > 7) {
                return null
            }

            // check for trailing whitespace. Must be at least one
            if (offset >= line.length || !line[offset].isWhitespace()) {
                return null
            }

            val markerRange = offset - hashesCount .. offset
            val contentRange = offset..line.length

            return Pair(markerRange, contentRange)
        }
    }
}