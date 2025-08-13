package org.intellij.markdown.flavours.custom.list

import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.custom.CustomElementTypes
import org.intellij.markdown.flavours.custom.CustomTokenTypes
import org.intellij.markdown.lexer.Compat.assert
import org.intellij.markdown.lexer.Stack
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.constraints.applyToNextLineAndAddModifiers
import org.intellij.markdown.parser.constraints.getCharsEaten
import org.intellij.markdown.parser.constraints.upstreamWith
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockImpl
import org.intellij.markdown.parser.sequentialparsers.SequentialParser

typealias UListMarkerRange = IntRange
typealias UListContentRange = IntRange
typealias UListItemRange = Pair<UListMarkerRange, UListContentRange>

class CustomUnorderedListMarkerBlock(
    constraints: MarkdownConstraints,
    private val productionHolder: ProductionHolder,
    private val initialItemRange: UListItemRange,
) : MarkerBlockImpl(constraints, productionHolder.mark()) {
    private val initialListLevel = getItemLevel(initialItemRange.first)
    private var currentListLevel = 0
    // [level, start, end]
    private val nestingListStack = Stack<Triple<Int, Int, Int>>()

    init {
        openLists(initialListLevel, initialItemRange)
        processItems(initialItemRange)
    }

    override fun doProcessToken(pos: LookaheadText.Position, currentConstraints: MarkdownConstraints): MarkerBlock.ProcessingResult {
        val actualPos = pos.nextPosition(1)
        if (actualPos == null) {
            closeLists(0, -1)
            return MarkerBlock.ProcessingResult.CANCEL
        }

        val itemRange = match(actualPos) ?: run {
            closeLists(0, -1)
            return MarkerBlock.ProcessingResult.DEFAULT
        }

        val nextLevel = getItemLevel(itemRange.first)

        when {
            nextLevel > currentListLevel -> openLists(nextLevel, itemRange)
            nextLevel < currentListLevel -> closeLists(nextLevel, itemRange.second.last)
            nextLevel == currentListLevel -> {
                if (nestingListStack.isNotEmpty()) {
                    val (level, start, oldEnd) = nestingListStack.pop()
                    nestingListStack.push(Triple(level, start, itemRange.second.last))
                }
            }
        }

        processItems(itemRange)
        return MarkerBlock.ProcessingResult.CANCEL
    }

    override fun isInterestingOffset(pos: LookaheadText.Position): Boolean = true

    override fun getDefaultAction(): MarkerBlock.ClosingAction = MarkerBlock.ClosingAction.DONE

    override fun calcNextInterestingOffset(pos: LookaheadText.Position): Int = pos.nextLineOrEofOffset

    override fun getDefaultNodeType(): IElementType = CustomElementTypes.ULIST

    override fun allowsSubBlocks(): Boolean = false

    private fun processItems(itemRange: UListItemRange) {
        val (markerRange, contentRange) = itemRange
        productionHolder.addProduction(
             listOf(
                SequentialParser.Node(markerRange.first..contentRange.last, CustomElementTypes.ULIST_ITEM),
                SequentialParser.Node(markerRange.first..markerRange.last, CustomTokenTypes.UL_ITEM_MARKER),
                SequentialParser.Node(contentRange.first..contentRange.last, CustomTokenTypes.UL_ITEM_CONTENT),
            )
        )
    }

    private fun openLists(toLevel: Int, itemRange: UListItemRange) {
        val (markerRange, contentRange) = itemRange
        while (currentListLevel != toLevel) {
            currentListLevel++
            nestingListStack.push(Triple(currentListLevel, markerRange.first, contentRange.last))
        }
    }

    private fun closeLists(toLevel: Int, currentEnd: Int) {
        val nodes = buildList<SequentialParser.Node> {
            var end: Int? = null
            while (nestingListStack.isNotEmpty() && nestingListStack.peek().first != toLevel) {
                val (_, start, end2) = nestingListStack.pop()
                if (end == null) {
                    end = end2
                }
                add(SequentialParser.Node(start..end, CustomElementTypes.ULIST_NESTED))
            }
        }
        if (nestingListStack.isNotEmpty()) {
            val (level, start, oldEnd) = nestingListStack.pop()
            nestingListStack.push(Triple(level, start, currentEnd))
        }
        currentListLevel = toLevel
        productionHolder.addProduction(nodes)
    }

    companion object {
        fun match(pos: LookaheadText.Position): UListItemRange? {
            val line = pos.currentLine

            // trim leading whitespaces
            var offset = line.takeWhile { it.isWhitespace() }.length

            // count dashes
            var dashesCount = 0
            while (offset < line.length && line[offset] == '-' && dashesCount <= 4) {
                offset++
                dashesCount++
            }

            // unordered list levels starts with 2 dashes and goes up only to 4
            if (dashesCount < 2 || dashesCount > 4) {
                return null
            }

            // check for trailing whitespace. Must be at least one
            if (offset >= line.length || !line[offset].isWhitespace()) {
                return null
            }

            val markerRange = (pos.offset + offset - dashesCount..pos.offset + offset)
            val contentRange = (pos.offset + offset..pos.offset + line.length)

            return Pair(markerRange, contentRange)
        }

        fun getItemLevel(markerRange: UListMarkerRange): Int {
            val dashes = markerRange.last - markerRange.first
            val level = dashes - 2
            require(level in 0..2) { "Invalid marker dash count: $dashes" }
            return level
        }
    }
}