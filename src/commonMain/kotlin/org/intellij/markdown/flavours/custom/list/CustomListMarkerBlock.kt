package org.intellij.markdown.flavours.custom.list

import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.custom.CustomElementTypes
import org.intellij.markdown.flavours.custom.CustomTokenTypes
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

enum class ListType { ORDERED, UNORDERED }

private data class ListInfo(
    val level: Int,
    val type: ListType,
    val range: IntRange,
)

class CustomListMarkerBlock(
    pos: LookaheadText.Position,
    constraints: MarkdownConstraints,
    private val productionHolder: ProductionHolder,
    private val listType: ListType,
    initialItemRange: ListItemRange,
) : MarkerBlockImpl(constraints, productionHolder.mark()) {
    private val initialListLevel = when (listType) {
        ListType.UNORDERED -> getUnorderedItemLevel(initialItemRange.first)
        ListType.ORDERED -> getOrderedItemLevel(pos, initialItemRange.first)
    }
    private var currentListLevel = 0
    private val nestingListStack = Stack<ListInfo>()

    init {
        require(initialListLevel == currentListLevel) {
            "Cannot start list with level: $initialListLevel"
        }
        processItems(initialItemRange, listType)
    }

    override fun doProcessToken(pos: LookaheadText.Position, currentConstraints: MarkdownConstraints): MarkerBlock.ProcessingResult {
        val actualPos = pos.nextPosition(1)
        if (actualPos == null) {
            closeLists(0, -1)
            return MarkerBlock.ProcessingResult.CANCEL
        }

        val matched = match(actualPos) ?: run {
            closeLists(0, -1)
            return MarkerBlock.ProcessingResult.DEFAULT
        }

        val (nextListType, itemRange) = matched

        val nextLevel = if (nextListType == ListType.ORDERED) {
            getOrderedItemLevel(actualPos, itemRange.first)
        } else {
            getUnorderedItemLevel(itemRange.first)
        }

        when {
            nextLevel == currentListLevel -> {
                if (listType != nextListType) {
                    closeLists(0, -1)
                    return MarkerBlock.ProcessingResult.DEFAULT
                }
                if (nestingListStack.isNotEmpty()) {
                    val levelInfo = nestingListStack.pop()
                    nestingListStack.push(levelInfo.copy(range = levelInfo.range.first..itemRange.second.last))
                }
            }
            nextLevel > currentListLevel -> {
                assert(nextLevel == currentListLevel + 1) {
                    "The difference between item levels cannot be greater than 1"
                }
                openList(itemRange, nextListType)
            }
            nextLevel < currentListLevel -> {
                closeLists(nextLevel, itemRange.second.last)
            }
        }

        processItems(itemRange, nextListType)
        return MarkerBlock.ProcessingResult.CANCEL
    }

    override fun isInterestingOffset(pos: LookaheadText.Position): Boolean = true

    override fun getDefaultAction(): MarkerBlock.ClosingAction {
        if (nestingListStack.isNotEmpty()) {
            closeLists(0, -1)
        }
        return MarkerBlock.ClosingAction.DONE
    }

    override fun calcNextInterestingOffset(pos: LookaheadText.Position): Int = pos.nextLineOrEofOffset

    override fun getDefaultNodeType(): IElementType = when (listType) {
        ListType.UNORDERED -> CustomElementTypes.ULIST
        ListType.ORDERED -> CustomElementTypes.OLIST
    }

    override fun allowsSubBlocks(): Boolean = false

    private fun processItems(itemRange: ListItemRange, listType: ListType) {
        val (markerRange, contentRange) = itemRange

        val (itemType, markerType, contentType) = when(listType) {
            ListType.ORDERED -> Triple(CustomElementTypes.OLIST_ITEM, CustomTokenTypes.OL_ITEM_MARKER, CustomTokenTypes.OL_ITEM_CONTENT)
            ListType.UNORDERED -> Triple(CustomElementTypes.ULIST_ITEM, CustomTokenTypes.UL_ITEM_MARKER, CustomTokenTypes.UL_ITEM_CONTENT)
        }

        productionHolder.addProduction(listOf(
            SequentialParser.Node(
                markerRange.first..contentRange.last,
                itemType
            ),
            SequentialParser.Node(
                markerRange.first..markerRange.last,
                markerType
            ),
            SequentialParser.Node(
                contentRange.first..contentRange.last,
                contentType
            ),
        ))
    }

    private fun openList(itemRange: ListItemRange, nextListType: ListType) {
        currentListLevel++
        val (markerRange, contentRange) = itemRange
        nestingListStack.push(ListInfo(
            level = currentListLevel,
            type = nextListType,
            range = (markerRange.first..contentRange.last),
        ))
    }

    private fun closeLists(toLevel: Int, currentEnd: Int) {
        val nodes = buildList<SequentialParser.Node> {
            var end: Int? = null
            while (nestingListStack.isNotEmpty() && nestingListStack.peek().level != toLevel) {
                val (_, type, range) = nestingListStack.pop()
                if (end == null) {
                    end = range.last
                }
                add(SequentialParser.Node(
                    range.first..end,
                    if (type == ListType.ORDERED) CustomElementTypes.OLIST_NESTED
                    else CustomElementTypes.ULIST_NESTED,
                ))
            }
        }
        if (nestingListStack.isNotEmpty()) {
            val info = nestingListStack.pop()
            nestingListStack.push(info.copy(range = (info.range.first..currentEnd)))
        }
        currentListLevel = toLevel
        productionHolder.addProduction(nodes)
    }

    private fun getOrderedItemLevel(pos: LookaheadText.Position, markerRange: ListItemMarkerRange): Int {
        val dotsCount = pos.originalText.substring(markerRange.first, markerRange.last)
            .takeLastWhile { it == '.' }
            .length
        val level = dotsCount - MIN_DOT_COUNT
        require(level in 0..MAX_NESTING_LEVEL) {
            "Invalid ordered list item dots count: $dotsCount"
        }
        return level
    }

    private fun getUnorderedItemLevel(markerRange: ListItemMarkerRange): Int {
        val dashCount = markerRange.last - markerRange.first
        val level = dashCount - MIN_DASH_COUNT
        require(level in 0..MAX_NESTING_LEVEL) {
            "Invalid marker dash count: $dashCount (must be between $MIN_DASH_COUNT and $MAX_DASH_COUNT)"
        }
        return level
    }

    companion object {
        private const val MIN_DASH_COUNT = 2
        private const val MAX_DASH_COUNT = 4
        private const val MIN_DOT_COUNT = 2
        private const val MAX_DOT_COUNT = 4
        private const val MAX_NESTING_LEVEL = 2

        fun match(pos: LookaheadText.Position): Pair<ListType, ListItemRange>? {
            var range = matchUnorderedListItem(pos)
            if (range != null) {
                return ListType.UNORDERED to range
            }
            range = matchOrderedListItem(pos) ?: return null
            return ListType.ORDERED to range
        }

        fun matchOrderedListItem(pos: LookaheadText.Position): ListItemRange? {
            val line = pos.currentLine

            // Skip leading whitespace
            var offset = line.takeWhile { it.isWhitespace() }.length

            val numStart = offset
            while (offset < line.length && line[offset].isDigit()) {
                offset++
            }

            if (offset == numStart) {
                return null
            }

            // Check if number is invalid
            line.substring(numStart, offset).toIntOrNull()
                ?: return null

            // Count consecutive dot characters
            var dotCount = 0
            while (offset < line.length && line[offset] == '.' && dotCount <= MAX_DOT_COUNT) {
                offset++
                dotCount++
            }

            // Validate dot count (must be between MIN_DOT_COUNT and MAX_DOT_COUNT)
            if (dotCount < MIN_DOT_COUNT || dotCount > MAX_DOT_COUNT) {
                return null
            }

            // Must have at least one whitespace character after dashes
            if (offset >= line.length || !line[offset].isWhitespace()) {
                return null
            }

            val markerRange = (pos.offset + numStart..pos.offset + offset)
            val contentRange = (pos.offset + offset..pos.offset + line.length)

            return markerRange to contentRange
        }

        fun matchUnorderedListItem(pos: LookaheadText.Position): ListItemRange? {
            val line = pos.currentLine

            // Skip leading whitespace
            var offset = line.takeWhile { it.isWhitespace() }.length

            // Count consecutive dash characters
            var dashCount = 0
            while (offset < line.length && line[offset] == '-' && dashCount <= 4) {
                offset++
                dashCount++
            }

            // Validate dash count (must be between MIN_DASH_COUNT and MAX_DASH_COUNT)
            if (dashCount !in MIN_DASH_COUNT..MAX_DASH_COUNT) {
                return null
            }

            // Must have at least one whitespace character after dashes
            if (offset >= line.length || !line[offset].isWhitespace()) {
                return null
            }

            val markerRange = (pos.offset + offset - dashCount..pos.offset + offset)
            val contentRange = (pos.offset + offset..pos.offset + line.length)

            return markerRange to contentRange
        }
    }
}