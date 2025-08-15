package org.intellij.markdown.flavours.custom.paragraph

import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.MarkerProcessor
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlock
import org.intellij.markdown.parser.markerblocks.MarkerBlockProvider

class CustomParagraphBlockProvider(
    private val blockProviders: List<MarkerBlockProvider<MarkerProcessor.StateInfo>>,
) : MarkerBlockProvider<MarkerProcessor.StateInfo> {
    override fun createMarkerBlocks(pos: LookaheadText.Position, productionHolder: ProductionHolder, stateInfo: MarkerProcessor.StateInfo): List<MarkerBlock> {
        if (pos.currentLine.isBlank()) {
            return emptyList()
        }
        return listOf(
            CustomParagraphMarkerBlock(
                constraints = stateInfo.currentConstraints,
                productionHolder = productionHolder,
                interruptsParagraph = checkIfInterruptsParagraph
            )
        )
    }

    override fun interruptsParagraph(pos: LookaheadText.Position, constraints: MarkdownConstraints): Boolean
            = false

    private val checkIfInterruptsParagraph: (pos: LookaheadText.Position, constraints: MarkdownConstraints) -> Boolean = { pos, constraints ->
        blockProviders.any { provider ->
            provider.interruptsParagraph(pos, constraints)
        }
    }
}