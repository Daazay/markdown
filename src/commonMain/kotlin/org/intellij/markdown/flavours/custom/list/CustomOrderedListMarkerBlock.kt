package org.intellij.markdown.flavours.custom.list

import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.custom.CustomElementTypes
import org.intellij.markdown.flavours.custom.CustomTokenTypes
import org.intellij.markdown.flavours.custom.list.CustomUnorderedListMarkerBlock.Companion
import org.intellij.markdown.lexer.Compat.assert
import org.intellij.markdown.lexer.Stack
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockImpl
import org.intellij.markdown.parser.sequentialparsers.SequentialParser

typealias ListItemMarkerRange = IntRange
typealias ListItemContentRange = IntRange
typealias ListItemRange = Pair<ListItemMarkerRange, ListItemContentRange>

class CustomOrderedListMarkerBlock(
    pos: LookaheadText.Position,
    constraints: MarkdownConstraints,
    private val productionHolder: ProductionHolder,
    private val initialItemRange: ListItemRange,
) : MarkerBlockImpl(constraints, productionHolder.mark()) {
    private val initialListLevel = getItemLevel(pos, initialItemRange.first)
    private var currentListLevel = 0
    private val nestingListStack = Stack<Pair<Int, IntRange>>()

    init {
        require(initialListLevel == currentListLevel) { "Cannot start list with level: $initialListLevel" }
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

        val nextLevel = getItemLevel(pos, itemRange.first)

        when {
            nextLevel > currentListLevel -> {
                assert(nextLevel == currentListLevel + 1) { "The difference between items cannot be greater than 1 if we open new list" }
                openList(itemRange)
            }
            nextLevel < currentListLevel -> {
                closeLists(nextLevel, itemRange.second.last)
            }
            nextLevel == currentListLevel -> {
                if (nestingListStack.isNotEmpty()) {
                    val (level, range) = nestingListStack.pop()
                    nestingListStack.push(level to (range.first..itemRange.second.last))
                }
            }
        }

        processItems(itemRange)
        return MarkerBlock.ProcessingResult.CANCEL
    }

    override fun isInterestingOffset(pos: LookaheadText.Position): Boolean = true

    override fun getDefaultAction(): MarkerBlock.ClosingAction = MarkerBlock.ClosingAction.DONE

    override fun calcNextInterestingOffset(pos: LookaheadText.Position): Int = pos.nextLineOrEofOffset

    override fun getDefaultNodeType(): IElementType = CustomElementTypes.OLIST

    override fun allowsSubBlocks(): Boolean = false

    private fun processItems(itemRange: ListItemRange) {
        val (markerRange, contentRange) = itemRange
        productionHolder.addProduction(listOf(
            SequentialParser.Node(markerRange.first..contentRange.last, CustomElementTypes.OLIST_ITEM),
            SequentialParser.Node(markerRange.first..markerRange.last, CustomTokenTypes.OL_ITEM_MARKER),
            SequentialParser.Node(contentRange.first..contentRange.last, CustomTokenTypes.OL_ITEM_CONTENT),
        ))
    }

    private fun openList(itemRange: ListItemRange) {
        currentListLevel++
        val (markerRange, contentRange) = itemRange
        nestingListStack.push(currentListLevel to (markerRange.first..contentRange.last))
    }

    private fun closeLists(toLevel: Int, currentEnd: Int) {
        val nodes = buildList<SequentialParser.Node> {
            var end: Int? = null
            while (nestingListStack.isNotEmpty() && nestingListStack.peek().first != toLevel) {
                val (_, range) = nestingListStack.pop()
                if (end == null) {
                    end = range.last
                }
                add(SequentialParser.Node(range.first..end, CustomElementTypes.OLIST_NESTED))
            }
        }
        if (nestingListStack.isNotEmpty()) {
            val (level, range) = nestingListStack.pop()
            nestingListStack.push(level to (range.first..currentEnd))
        }
        currentListLevel = toLevel
        productionHolder.addProduction(nodes)
    }

    private fun getItemLevel(pos: LookaheadText.Position, markerRange: ListItemMarkerRange): Int {
        val dotsCount = pos.originalText.substring(markerRange.first, markerRange.last).takeLastWhile { it == '.' }.length
        val level = dotsCount - 2
        require(level in 0..2) { "Invalid unordered list item dots count: $dotsCount" }
        return level
    }

    companion object {
        fun match(pos: LookaheadText.Position): ListItemRange? {
            val line = pos.currentLine
            var offset = line.takeWhile { it.isWhitespace() }.length

            val numStart = offset
            while (offset < line.length && line[offset].isDigit()) {
                offset++
            }
            if (offset == numStart) return null
            val number = line.substring(numStart, offset).toIntOrNull()
                ?: return null

            var dotCount = 0
            while (offset < line.length && line[offset] == '.' && dotCount < 4) {
                offset++
                dotCount++
            }

            if (dotCount < 2 || dotCount > 4) return null
            if (offset >= line.length || !line[offset].isWhitespace()) return null

            val markerRange = (pos.offset + numStart..pos.offset + offset)
            val contentRange = (pos.offset + offset..pos.offset + line.length)

            return markerRange to contentRange
        }
    }
}