package org.intellij.markdown.flavours.custom

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementType

object CustomElementTypes {
    val STRONG: IElementType = MarkdownElementType("CUSTOM_STRONG_EL")
    val ITALIC: IElementType = MarkdownElementType("CUSTOM_ITALIC_EL")
    val STRIKETHROUGH: IElementType = MarkdownElementType("CUSTOM_STRIKETHROUGH_EL")
    val UNDERLINE: IElementType = MarkdownElementType("CUSTOM_UNDERLINE_EL")

    val HEADING_1: IElementType = MarkdownElementType("CUSTOM_HEADING_1_EL")
    val HEADING_2: IElementType = MarkdownElementType("CUSTOM_HEADING_2_EL")
    val HEADING_3: IElementType = MarkdownElementType("CUSTOM_HEADING_3_EL")
    val HEADING_4: IElementType = MarkdownElementType("CUSTOM_HEADING_4_EL")
    val HEADING_5: IElementType = MarkdownElementType("CUSTOM_HEADING_5_EL")
    val HEADING_6: IElementType = MarkdownElementType("CUSTOM_HEADING_6_EL")

    val ULIST: IElementType = MarkdownElementType("CUSTOM_ULIST_EL")
    val ULIST_NESTED: IElementType = MarkdownElementType("CUSTOM_ULIST_NESTED_EL")
    val ULIST_ITEM: IElementType = MarkdownElementType("CUSTOM_ULIST_ITEM")

    val OLIST: IElementType = MarkdownElementType("CUSTOM_OLIST_EL")
    val OLIST_NESTED: IElementType = MarkdownElementType("CUSTOM_OLIST_NESTED_EL")
    val OLIST_ITEM: IElementType = MarkdownElementType("CUSTOM_OLIST_ITEM")

    val PARAGRAPH: IElementType = MarkdownElementType("CUSTOM_PARAGRAPH_EL", true)
}