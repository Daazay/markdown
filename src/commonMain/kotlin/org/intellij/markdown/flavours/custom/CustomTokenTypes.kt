package org.intellij.markdown.flavours.custom

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementType

object CustomTokenTypes {
    val STRONG_MARKER: IElementType = MarkdownElementType("CUSTOM_STRONG_MARKER_TOK", true)
    val ITALIC_MARKER: IElementType = MarkdownElementType("CUSTOM_ITALIC_MARKER_TOK", true)
    val STRIKETHROUGH_MARKER: IElementType = MarkdownElementType("CUSTOM_STRIKETHROUGH_MARKER_TOK", true)
    val UNDERLINE_MARKER: IElementType = MarkdownElementType("CUSTOM_UNDERLINE_MARKER_TOK", true)

    val HEADING_MARKER: IElementType = MarkdownElementType("CUSTOM_HEADING_MARKER_TOK", true)
    val HEADING_CONTENT: IElementType = MarkdownElementType("CUSTOM_HEADING_CONTENT_TOK", true)

    val UL_ITEM_MARKER: IElementType = MarkdownElementType("CUSTOM_UL_ITEM_MARKER_TOK", true)
    val UL_ITEM_CONTENT: IElementType = MarkdownElementType("CUSTOM_UL_ITEM_CONTENT_TOK", true)

    val LINEBREAK: IElementType = MarkdownElementType("CUSTOM_LINEBREAK_TOK", true)
    val HR: IElementType = MarkdownElementType("CUSTOM_HR_TOK", true)

    val WHITE_SPACE: IElementType = MarkdownElementType("CUSTOM_WHITE_SPACE_TOK", true)
}