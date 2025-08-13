package org.intellij.markdown.flavours.custom

import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.html.OpenCloseGeneratingProvider

class CustomOpenCloseGeneratingProvider(
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