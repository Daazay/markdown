package org.intellij.markdown.flavours.custom.list

import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.MarkerProcessor
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockProvider

class CustomListBlockProvider : MarkerBlockProvider<MarkerProcessor.StateInfo> {
    override fun createMarkerBlocks(pos: LookaheadText.Position, productionHolder: ProductionHolder, stateInfo: MarkerProcessor.StateInfo): List<MarkerBlock> {
        val matched = CustomListMarkerBlock.match(pos) ?: return emptyList()
        val (type, range) = matched

        return listOf<MarkerBlock>(
            CustomListMarkerBlock(
                pos = pos,
                constraints = stateInfo.currentConstraints,
                productionHolder = productionHolder,
                listType = type,
                initialItemRange = range,
            )
        )
    }

    override fun interruptsParagraph(pos: LookaheadText.Position, constraints: MarkdownConstraints): Boolean {
        return CustomListMarkerBlock.match(pos) != null
    }
}