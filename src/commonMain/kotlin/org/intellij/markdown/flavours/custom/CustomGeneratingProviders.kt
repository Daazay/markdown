package org.intellij.markdown.flavours.custom

import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.html.OpenCloseGeneratingProvider
import org.intellij.markdown.html.TrimmingInlineHolderProvider

open class CustomOpenCloseGeneratingProvider(
    val tagName: String,
    val attributes: String? = null,
): OpenCloseGeneratingProvider() {
    override fun openTag(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
        visitor.consumeTagOpen(node, tagName, attributes)
    }

    override fun closeTag(visitor: HtmlGenerator.HtmlGeneratingVisitor, text: String, node: ASTNode) {
        visitor.consumeTagClose(tagName)
    }
}

open class CustomTrimmingInlineHolderProvider : TrimmingInlineHolderProvider() {
    override fun childrenToRender(node: ASTNode): List<ASTNode> {
        val children = node.children

        val trimmed = children
            .dropWhile { it.type == CustomTokenTypes.WHITE_SPACE }
            .dropLastWhile { it.type == CustomTokenTypes.WHITE_SPACE }

        val result = mutableListOf<ASTNode>()
        var prevWasWs = false
        for (child in trimmed) {
            if (child.type == CustomTokenTypes.WHITE_SPACE) {
                if (!prevWasWs) {
                    result.add(child)
                }
                prevWasWs = true
            }
            else {
                result.add(child)
                prevWasWs = false
            }
        }

        return result
    }
}