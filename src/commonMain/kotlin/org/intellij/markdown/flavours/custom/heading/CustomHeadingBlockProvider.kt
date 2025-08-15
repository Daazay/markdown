package org.intellij.markdown.flavours.custom.heading

import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.MarkerProcessor
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockProvider

typealias HeadingMarkerRange = IntRange
typealias HeadingContentRange = IntRange
typealias HeadingRange = Pair<HeadingMarkerRange, HeadingContentRange>

class CustomHeadingBlockProvider : MarkerBlockProvider<MarkerProcessor.StateInfo> {
    override fun createMarkerBlocks(pos: LookaheadText.Position, productionHolder: ProductionHolder, stateInfo: MarkerProcessor.StateInfo): List<MarkerBlock> {
        val range = CustomHeadingMarkerBlock.match(pos) ?: return emptyList()
        return listOf<MarkerBlock>(
            CustomHeadingMarkerBlock(
                constraints = stateInfo.currentConstraints,
                productionHolder = productionHolder,
                range = range
            )
        )
    }

    override fun interruptsParagraph(pos: LookaheadText.Position, constraints: MarkdownConstraints): Boolean {
        return CustomHeadingMarkerBlock.match(pos) != null
    }
}