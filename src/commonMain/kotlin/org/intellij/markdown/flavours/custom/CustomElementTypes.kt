package org.intellij.markdown.flavours.custom

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementType
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object CustomElementTypes {
    @JvmField
    val STRONG: IElementType = MarkdownElementType("CUSTOM_STRONG_EL")
    @JvmField
    val ITALIC: IElementType = MarkdownElementType("CUSTOM_ITALIC_EL")
    @JvmField
    val STRIKETHROUGH: IElementType = MarkdownElementType("CUSTOM_STRIKETHROUGH_EL")
    @JvmField
    val UNDERLINE: IElementType = MarkdownElementType("CUSTOM_UNDERLINE_EL")

    @JvmField
    val HEADING_1: IElementType = MarkdownElementType("CUSTOM_HEADING_1_EL")
    @JvmField
    val HEADING_2: IElementType = MarkdownElementType("CUSTOM_HEADING_2_EL")
    @JvmField
    val HEADING_3: IElementType = MarkdownElementType("CUSTOM_HEADING_3_EL")
    @JvmField
    val HEADING_4: IElementType = MarkdownElementType("CUSTOM_HEADING_4_EL")
    @JvmField
    val HEADING_5: IElementType = MarkdownElementType("CUSTOM_HEADING_5_EL")
    @JvmField
    val HEADING_6: IElementType = MarkdownElementType("CUSTOM_HEADING_6_EL")

    @JvmField
    val ALL_HEADING: Array<IElementType> = arrayOf(
        HEADING_1, HEADING_2, HEADING_3, HEADING_4, HEADING_5, HEADING_6
    )

    @JvmField
    val ULIST: IElementType = MarkdownElementType("CUSTOM_ULIST_EL")
    @JvmField
    val ULIST_NESTED: IElementType = MarkdownElementType("CUSTOM_ULIST_NESTED_EL")
    @JvmField
    val ULIST_ITEM: IElementType = MarkdownElementType("CUSTOM_ULIST_ITEM")

    @JvmField
    val OLIST: IElementType = MarkdownElementType("CUSTOM_OLIST_EL")
    @JvmField
    val OLIST_NESTED: IElementType = MarkdownElementType("CUSTOM_OLIST_NESTED_EL")
    @JvmField
    val OLIST_ITEM: IElementType = MarkdownElementType("CUSTOM_OLIST_ITEM")

    @JvmField
    val PARAGRAPH: IElementType = MarkdownElementType("CUSTOM_PARAGRAPH_EL", true)

    @JvmField
    val ALL_BLOCKS: Array<IElementType> = arrayOf(
        PARAGRAPH, ULIST, ULIST_NESTED, OLIST_ITEM, OLIST_NESTED
    )

    @JvmStatic
    fun heading(level: Int): IElementType = when (level) {
        1 -> HEADING_1
        2 -> HEADING_2
        3 -> HEADING_3
        4 -> HEADING_4
        5 -> HEADING_5
        6 -> HEADING_6
        else -> throw IllegalArgumentException("Invalid heading level: $level")
    }
}