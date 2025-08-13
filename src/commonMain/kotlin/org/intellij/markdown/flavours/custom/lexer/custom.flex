package org.intellij.markdown.flavours.custom.lexer;

import org.intellij.markdown.flavours.custom.CustomTokenTypes;
import org.intellij.markdown.MarkdownElementType;

import org.intellij.markdown.IElementType;
import org.intellij.markdown.lexer.GeneratedLexer;
import org.intellij.markdown.MarkdownTokenTypes;

%%

%class _CustomLexer
%implements GeneratedLexer

%unicode
%public
%caseless

%function advance
%type IElementType

%{

%}

// PATTERNS

WHITESPACE = [ \t\f]
EOL        = \R
TEXT_CHAR  = [^\r\n]

STRONG_CHAR        = "*"
ITALIC_CHAR        = "_"
STRIKETHROUGH_CHAR = "~"
UNDERLINE_CHAR     = "+"

INLINE_DELIM_CHAR   = ({STRONG_CHAR} | {ITALIC_CHAR} | {STRIKETHROUGH_CHAR} | {UNDERLINE_CHAR})
ESCAPED_DELIM_CHAR  = \\{INLINE_DELIM_CHAR}

%%

<YYINITIAL> {
    {STRONG_CHAR}{2,2}        { return CustomTokenTypes.STRONG_MARKER; }
    {ITALIC_CHAR}{2,2}        { return CustomTokenTypes.ITALIC_MARKER; }
    {STRIKETHROUGH_CHAR}{2,2} { return CustomTokenTypes.STRIKETHROUGH_MARKER; }
    {UNDERLINE_CHAR}{2,2}     { return CustomTokenTypes.UNDERLINE_MARKER; }

    {ESCAPED_DELIM_CHAR}      { return MarkdownTokenTypes.TEXT; }

    {WHITESPACE}* {EOL} {WHITESPACE}* {TEXT_CHAR} {
          yypushback(1);
          return MarkdownTokenTypes.HARD_LINE_BREAK;
    }

    {WHITESPACE}+          { return CustomTokenTypes.WHITE_SPACE; }

    {TEXT_CHAR}            { return MarkdownTokenTypes.TEXT; }
}