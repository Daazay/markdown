package org.intellij.markdown.flavours.custom.delimiters

import org.intellij.markdown.IElementType
import org.intellij.markdown.parser.sequentialparsers.DelimiterParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.TokensCache
import org.intellij.markdown.parser.sequentialparsers.impl.EmphStrongDelimiterParser

class CustomDelimiterParser(
    private val tokenType: IElementType,
    private val elementType: IElementType,
    private val marker: Char,
) : DelimiterParser() {
    companion object {
        private const val MIN_DELIMITER_FOR_IGNORE = 3

        private fun Info.isOpeningDelimiter(tokenType: IElementType): Boolean {
            return this.tokenType == tokenType && this.closerIndex != -1
        }
    }

    override fun process(tokens: TokensCache, iterator: TokensCache.Iterator, delimiters: MutableList<Info>, result: SequentialParser.ParsingResultBuilder) {
        var index = delimiters.lastIndex

        while (index >= 0) {
            // Find opening
            if (!delimiters[index].isOpeningDelimiter(tokenType)) {
                index--
                continue
            }

            var openerIndex = index
            var closerIndex = delimiters[index].closerIndex

            // Attempt to widen the matched delimiters
            var delimitersMatched = 1
            while (EmphStrongDelimiterParser.areAdjacentSameMarkers(delimiters, openerIndex, closerIndex)) {
                openerIndex--
                closerIndex++
                delimitersMatched++
            }

            // If 3 or more delimiters are matched, ignore
            if (delimitersMatched < MIN_DELIMITER_FOR_IGNORE) {
                val opener = delimiters[openerIndex]
                val closer = delimiters[closerIndex]

                result.withNode(SequentialParser.Node(
                    opener.position..closer.position + 1,
                    elementType
                ))
            }

            // Update index
            index = openerIndex - 1
        }
    }

    override fun scan(tokens: TokensCache, iterator: TokensCache.Iterator, delimiters: MutableList<Info>): Int {
        if (iterator.type != tokenType) {
            return 0
        }

        // Count consecutive delimiter tokens
        var stepsToAdvance = 1
        var rightIterator = iterator

        repeat(maxAdvance - 1) {
            if (rightIterator.rawLookup(1) != tokenType) {
                return@repeat
            }
            rightIterator = rightIterator.advance()
            stepsToAdvance++
        }

        // Determine if the delimiter sequence can open and/or close
        val (canOpen, canClose) = canOpenClose(tokens, iterator, rightIterator, canSplitText = false)

        repeat(stepsToAdvance) { index ->
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
}