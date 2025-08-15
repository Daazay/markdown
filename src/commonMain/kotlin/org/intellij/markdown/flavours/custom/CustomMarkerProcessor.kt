package org.intellij.markdown.flavours.custom

import org.intellij.markdown.flavours.custom.heading.CustomHeadingBlockProvider
import org.intellij.markdown.flavours.custom.hline.CustomHLineBlockProvider
import org.intellij.markdown.flavours.custom.list.CustomListBlockProvider
import org.intellij.markdown.flavours.custom.paragraph.CustomParagraphBlockProvider
import org.intellij.markdown.parser.LookaheadText
import org.intellij.markdown.parser.MarkerProcessor
import org.intellij.markdown.parser.MarkerProcessorFactory
import org.intellij.markdown.parser.ProductionHolder
import org.intellij.markdown.parser.constraints.CommonMarkdownConstraints
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import org.intellij.markdown.parser.markerblocks.MarkerBlockProvider

class CustomMarkerProcessor(
    productionHolder: ProductionHolder,
    constraintsBase: MarkdownConstraints,
) : MarkerProcessor<MarkerProcessor.StateInfo>(productionHolder, constraintsBase) {
    override var stateInfo: StateInfo = StateInfo(startConstraints, startConstraints, markersStack)

    private val blockProviders: List<MarkerBlockProvider<StateInfo>> = listOf(
        CustomHeadingBlockProvider(),
        CustomListBlockProvider(),
        CustomHLineBlockProvider(),
    )

    override fun getMarkerBlockProviders(): List<MarkerBlockProvider<StateInfo>> {
        return blockProviders + CustomParagraphBlockProvider(blockProviders)
    }

    override fun updateStateInfo(pos: LookaheadText.Position) = Unit

    override fun populateConstraintsTokens(pos: LookaheadText.Position, constraints: MarkdownConstraints, productionHolder: ProductionHolder)  = Unit

    object Factory: MarkerProcessorFactory {
        override fun createMarkerProcessor(productionHolder: ProductionHolder): MarkerProcessor<*> {
            return CustomMarkerProcessor(productionHolder, CommonMarkdownConstraints.BASE)
        }
    }
}