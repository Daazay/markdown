package org.intellij.markdown.flavours.custom.paragraph

import org.intellij.markdown.IElementType
import org.intellij.markdown.flavours.custom.CustomElementTypes
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.constraints.applyToNextLineAndAddModifiers
import org.intellij.markdown.parser.constraints.getCharsEaten
import org.intellij.markdown.parser.constraints.upstreamWith
import org.intellij.markdown.parser.markerblocks.MarkdownParserUtil
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockImpl

class CustomParagraphMarkerBlock(
    constraints: MarkdownConstraints,
    productionHolder: ProductionHolder,
    val interruptsParagraph: (pos: LookaheadText.Position, constraints: MarkdownConstraints) -> Boolean,
) : MarkerBlockImpl(constraints, productionHolder.mark()) {
    companion object {
        private const val MIN_EOLS_FOR_PARAGRAPH_END = 2
    }

    override fun doProcessToken(pos: LookaheadText.Position, currentConstraints: MarkdownConstraints): MarkerBlock.ProcessingResult {
        // If we're still on the same line, continue processing
        if (pos.offsetInCurrentLine != -1) {
            return MarkerBlock.ProcessingResult.CANCEL
        }

        // Check if we have enough consecutive empty lines to end the paragraph
        if (MarkdownParserUtil.calcNumberOfConsequentEols(pos, constraints) >= MIN_EOLS_FOR_PARAGRAPH_END) {
            return MarkerBlock.ProcessingResult.DEFAULT
        }

        // Apply constraints to the next line and check if we can merge paragraphs
        val nextLineConstraints = constraints.applyToNextLineAndAddModifiers(pos)
        if (!nextLineConstraints.upstreamWith(constraints)) {
            return MarkerBlock.ProcessingResult.DEFAULT
        }

        // Check if the next line would interrupt paragraph parsing
        val posToCheck = pos.nextPosition(1 + nextLineConstraints.getCharsEaten(pos.currentLine))
        if (posToCheck == null || interruptsParagraph(posToCheck, nextLineConstraints)) {
            return MarkerBlock.ProcessingResult.DEFAULT
        }

        return MarkerBlock.ProcessingResult.CANCEL
    }

    override fun isInterestingOffset(pos: LookaheadText.Position): Boolean = true

    override fun getDefaultAction(): MarkerBlock.ClosingAction = MarkerBlock.ClosingAction.DONE

    override fun calcNextInterestingOffset(pos: LookaheadText.Position): Int = pos.nextLineOrEofOffset

    override fun getDefaultNodeType(): IElementType = CustomElementTypes.PARAGRAPH

    override fun allowsSubBlocks(): Boolean = false
}