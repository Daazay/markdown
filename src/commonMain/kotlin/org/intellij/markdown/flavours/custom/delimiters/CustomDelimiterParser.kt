package org.intellij.markdown.flavours.custom.delimiters

import org.intellij.markdown.IElementType
import org.intellij.markdown.parser.sequentialparsers.DelimiterParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.TokensCache

class CustomDelimiterParser(
    private val tokenType: IElementType,
    private val elementType: IElementType,
    private val marker: Char,
) : DelimiterParser() {
    override fun process(tokens: TokensCache, iterator: TokensCache.Iterator, delimiters: MutableList<Info>, result: SequentialParser.ParsingResultBuilder) {
        var index = delimiters.size - 1

        while (index >= 0) {
            // Find opening
            if (!delimiters[index].isOpeningDelimiter(tokenType)) {
                index -= 1
                continue
            }
            var openerIndex = index
            var closerIndex = delimiters[index].closerIndex

            // Attempt to widen the matched delimiters
            var delimitersMatched = 1
            while (areAdjacentSameMarkers(delimiters, openerIndex, closerIndex)) {
                openerIndex -= 1
                closerIndex += 1
                delimitersMatched += 1
            }

            // If 3 or more delimiters are matched, ignore
            if (delimitersMatched < 3) {
                val opener = delimiters[openerIndex]
                val closer = delimiters[closerIndex]

                result.withNode(SequentialParser.Node(opener.position..closer.position + 1, elementType))
            }

            // Update index
            index = openerIndex - 1
        }
    }

    override fun scan(tokens: TokensCache, iterator: TokensCache.Iterator, delimiters: MutableList<Info>): Int {
        if (iterator.type != tokenType) {
            return 0
        }
        var stepsToAdvance = 1
        var rightIterator = iterator
        for (index in 0 until maxAdvance) {
            if (rightIterator.rawLookup(1) != tokenType) {
                break
            }
            rightIterator = rightIterator.advance()
            stepsToAdvance += 1
        }
        val (canOpen, canClose) = canOpenClose(tokens, iterator, rightIterator, canSplitText = false)
        for (index in 0 until stepsToAdvance) {
            val info = Info(
                tokenType = tokenType,
                position = iterator.index + index,
                length = 0,
                canOpen = canOpen,
                canClose = canClose,
                marker = marker,
            )
            delimiters.add(info)
        }
        return stepsToAdvance
    }

    companion object {
        private fun DelimiterParser.Info.isOpeningDelimiter(tokenType: IElementType): Boolean {
            return this.tokenType == tokenType && this.closerIndex != -1
        }

        fun areAdjacentSameMarkers(delimiters: List<Info>, openerIndex: Int, closerIndex: Int): Boolean {
            val opener = delimiters[openerIndex]
            val closer = delimiters[closerIndex]
            return openerIndex > 0 &&
                    delimiters[openerIndex - 1].closerIndex == opener.closerIndex + 1 &&
                    delimiters[openerIndex - 1].marker == opener.marker &&
                    delimiters[openerIndex - 1].position == opener.position - 1 &&
                    delimiters[opener.closerIndex + 1].position == closer.position + 1
        }
    }
}