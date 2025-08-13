package org.intellij.markdown.flavours.custom

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.custom.delimiters.CustomDelimiterParser
import org.intellij.markdown.html.*
import org.intellij.markdown.lexer.MarkdownLexer
import org.intellij.markdown.lexer._MarkdownLexer
import org.intellij.markdown.parser.LinkMap
import org.intellij.markdown.parser.MarkerProcessorFactory
import org.intellij.markdown.parser.sequentialparsers.EmphasisLikeParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParserManager

class CustomFlavourDescriptor(
    private val margin: Int = 16,
) : MarkdownFlavourDescriptor {
    override val markerProcessorFactory: MarkerProcessorFactory = CustomMarkerProcessor.Factory

    private val sequentialParsers = listOf<SequentialParser>(
        EmphasisLikeParser(
            CustomDelimiterParser(CustomTokenTypes.STRONG_MARKER, CustomElementTypes.STRONG, '*'),
            CustomDelimiterParser(CustomTokenTypes.ITALIC_MARKER, CustomElementTypes.ITALIC, '_'),
            CustomDelimiterParser(CustomTokenTypes.STRIKETHROUGH_MARKER, CustomElementTypes.STRIKETHROUGH, '~'),
            CustomDelimiterParser(CustomTokenTypes.UNDERLINE_MARKER, CustomElementTypes.UNDERLINE, '+'),
        )
    )

    private val generatingProviders = mapOf<IElementType, GeneratingProvider> (
        MarkdownElementTypes.MARKDOWN_FILE to SimpleTagProvider("body"),
        CustomTokenTypes.WHITE_SPACE to object : GeneratingProvider {
            override fun processNode(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
                visitor.consumeHtml(" ")
            }
        },
        CustomElementTypes.PARAGRAPH to object : TrimmingInlineHolderProvider() {
            override fun openTag(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
                visitor.consumeTagOpen(node, "p", "style=\"margin:${margin}px 0\"")
            }
            override fun closeTag(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
                visitor.consumeTagClose("p")
            }
        },
        CustomTokenTypes.HEADING_CONTENT to TrimmingInlineHolderProvider(),
        CustomElementTypes.HEADING_1 to SimpleTagProvider("h1"),
        CustomElementTypes.HEADING_2 to SimpleTagProvider("h2"),
        CustomElementTypes.HEADING_3 to SimpleTagProvider("h3"),
        CustomElementTypes.HEADING_4 to SimpleTagProvider("h4"),
        CustomElementTypes.HEADING_5 to SimpleTagProvider("h5"),
        CustomElementTypes.HEADING_6 to SimpleTagProvider("h6"),
        CustomElementTypes.ULIST to CustomOpenCloseGeneratingProvider("ul", "style=\"margin-block-start:${margin}px;margin-block-end:${margin}px\""),
        CustomElementTypes.ULIST_NESTED to SimpleTagProvider("ul"),
        CustomElementTypes.ULIST_ITEM to SimpleTagProvider("li"),
        CustomTokenTypes.UL_ITEM_CONTENT to TrimmingInlineHolderProvider(),
        CustomElementTypes.STRONG to SimpleInlineTagProvider("b", 1, -1),
        CustomElementTypes.ITALIC to SimpleInlineTagProvider("i", 1, -1),
        CustomElementTypes.STRIKETHROUGH to SimpleInlineTagProvider("del", 1, -1),
        CustomElementTypes.UNDERLINE to SimpleInlineTagProvider("u", 1, -1),

    )

    override fun createInlinesLexer(): MarkdownLexer = MarkdownLexer(_MarkdownLexer())

    override val sequentialParserManager: SequentialParserManager = object : SequentialParserManager() {
        override fun getParserSequence(): List<SequentialParser> {
            return sequentialParsers
        }
    }

    override fun createHtmlGeneratingProviders(linkMap: LinkMap, baseURI: URI?): Map<IElementType, GeneratingProvider>
        = generatingProviders
}