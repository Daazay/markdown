package org.intellij.markdown.flavours.custom.list

import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.MarkerProcessor
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockProvider

class CustomOrderedListBlockProvider : MarkerBlockProvider<MarkerProcessor.StateInfo> {
    override fun createMarkerBlocks(pos: LookaheadText.Position, productionHolder: ProductionHolder, stateInfo: MarkerProcessor.StateInfo): List<MarkerBlock> {
        val range = CustomOrderedListMarkerBlock.match(pos) ?: return emptyList()
        return listOf<MarkerBlock>(
            CustomOrderedListMarkerBlock(
                pos = pos,
                constraints = stateInfo.currentConstraints,
                productionHolder = productionHolder,
                initialItemRange = range,
            )
        )
    }

    override fun interruptsParagraph(pos: LookaheadText.Position, constraints: MarkdownConstraints): Boolean {
        return CustomOrderedListMarkerBlock.match(pos) != null
    }
}