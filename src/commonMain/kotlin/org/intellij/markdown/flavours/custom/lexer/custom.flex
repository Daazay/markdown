package org.intellij.markdown.flavours.custom.lexer;

import org.intellij.markdown.MarkdownElementTypes;import org.intellij.markdown.flavours.custom.CustomTokenTypes;

import org.intellij.markdown.IElementType;
import org.intellij.markdown.lexer.GeneratedLexer;
import org.intellij.markdown.MarkdownTokenTypes;

import java.util.Stack;

%%

%class _CustomLexer
%implements GeneratedLexer

%unicode
%public

%function advance
%type IElementType

%{

%}

// PATTERNS

DIGIT      = [0-9]
WHITESPACE = [ \t\f]
EOL        = \R
ANY_CHAR   = [^]
TEXT_CHAR  = [^\r\n]

STRONG_CHAR        = "*"
ITALIC_CHAR        = "_"
STRIKETHROUGH_CHAR = "~"
UNDERLINE_CHAR     = "+"

INLINE_DELIM_CHAR   = ({STRONG_CHAR} | {ITALIC_CHAR} | {STRIKETHROUGH_CHAR} | {UNDERLINE_CHAR})
INLINE_DELIM_MARKER = {INLINE_DELIM_CHAR}{2,2}
ESCAPED_DELIM_CHAR  = \\{INLINE_DELIM_CHAR}

%%

<YYINITIAL> {
    {INLINE_DELIM_MARKER} { return getDelimiterTokenType(); }

    {ESCAPED_DELIM_CHAR} { return MarkdownTokenTypes.TEXT; }
}

{ANY_CHAR} { return MarkdownTokenTypes.TEXT; }