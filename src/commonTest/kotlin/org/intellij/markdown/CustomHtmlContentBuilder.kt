package org.intellij.markdown

@DslMarker
annotation class HtmlDsl

@HtmlDsl
open class CustomHtmlBaseBuilder {
    protected val sb = StringBuilder()
    override fun toString(): String = sb.toString()
}

@HtmlDsl
interface CustomFormattedContent {
    fun text(value: String)
    fun bold(block: CustomFormattedContent.() -> Unit)
    fun italic(block: CustomFormattedContent.() -> Unit)
    fun strike(block: CustomFormattedContent.() -> Unit)
    fun underline(block: CustomFormattedContent.() -> Unit)
}

@HtmlDsl
interface CustomHeadingContent: CustomFormattedContent

@HtmlDsl
interface CustomParagraphContent: CustomFormattedContent {
    fun br()
}

@HtmlDsl
interface CustomListItemContent: CustomFormattedContent

interface CustomListContent {
    fun item(block: CustomListItemContent.() -> Unit)
    fun ulist(block: CustomListContent.() -> Unit)
    fun olist(start: Int, block: CustomListContent.() -> Unit)
}

@HtmlDsl
open class CustomFormattedContentBuilder: CustomHtmlBaseBuilder(), CustomFormattedContent {
    override fun text(value: String) {
        sb.append(value)
    }

    private inline fun wrap(tag: String, block: CustomFormattedContent.() -> Unit) {
        sb.append("<$tag>")
            .append(CustomFormattedContentBuilder().apply(block))
            .append("</$tag>")
    }

    override fun bold(block: CustomFormattedContent.() -> Unit) = wrap("b", block)
    override fun italic(block: CustomFormattedContent.() -> Unit) = wrap("i", block)
    override fun strike(block: CustomFormattedContent.() -> Unit) = wrap("del", block)
    override fun underline(block: CustomFormattedContent.() -> Unit) = wrap("u", block)
}

@HtmlDsl
class CustomHeadingContentBuilder(private val level: Int): CustomFormattedContentBuilder(), CustomHeadingContent {
    override fun toString(): String = "<h$level>$sb</h$level>"
}

@HtmlDsl
class CustomParagraphContentBuilder(
    private val margin: Int,
) : CustomFormattedContentBuilder(), CustomParagraphContent {
    override fun br() {
        sb.append("<br>")
    }
    override fun toString(): String = "<p style=\"margin:${margin}px 0\">$sb</p>"
}

@HtmlDsl
class CustomListItemContentBuilder : CustomFormattedContentBuilder(), CustomListItemContent {
    override fun toString(): String = "<li>$sb</li>"
}

@HtmlDsl
open class CustomListContentBuilder(
    private val margin: Int,
) : CustomHtmlBaseBuilder(), CustomListContent {
    override fun item(block: CustomListItemContent.() -> Unit) {
        sb.append(CustomListItemContentBuilder().apply(block))
    }

    override fun ulist(block: CustomListContent.() -> Unit) {
        sb.append(CustomUListContentBuilder(margin, true).apply(block))
    }

    override fun olist(start: Int, block: CustomListContent.() -> Unit) {
        sb.append(CustomOListContentBuilder(margin, start, true).apply(block))
    }
}

@HtmlDsl
class CustomUListContentBuilder(
    private val margin: Int,
    private val nested: Boolean = false
) : CustomListContentBuilder(margin) {
    override fun toString(): String {
        val style = if (!nested) " style=\"margin-block-start:${margin}px;margin-block-end:${margin}px\"" else ""
        return "<ul$style>$sb</ul>"
    }
}

@HtmlDsl
class CustomOListContentBuilder(
    private val margin: Int,
    private val start: Int,
    private val nested: Boolean,
) : CustomListContentBuilder(margin) {
    override fun toString(): String {
        val style = if (!nested) " style=\"margin-block-start:${margin}px;margin-block-end:${margin}px\"" else ""
        return "<ol start=\"$start\"$style>$sb</ol>"
    }
}

class CustomHtmlContentBuilder(
    private val margin: Int,
) : CustomHtmlBaseBuilder() {
    fun heading(level: Int, block: CustomHeadingContent.() -> Unit) = sb.append(CustomHeadingContentBuilder(level).apply(block))
    fun paragraph(block: CustomParagraphContent.() -> Unit) = sb.append(CustomParagraphContentBuilder(margin).apply(block))
    fun ulist(block: CustomUListContentBuilder.() -> Unit) = sb.append(CustomUListContentBuilder(margin, false).apply(block))
    fun olist(start: Int, block: CustomOListContentBuilder.() -> Unit) = sb.append(CustomOListContentBuilder(margin, start, false).apply(block))
    fun hr() = sb.append("<hr style=\"margin:${margin}px 0;height:2px;border:none;color:#333;background-color:#333;\">")
}

fun html(margin: Int = 16, block: CustomHtmlContentBuilder.() -> Unit): String =
    CustomHtmlContentBuilder(margin).apply(block).toString()