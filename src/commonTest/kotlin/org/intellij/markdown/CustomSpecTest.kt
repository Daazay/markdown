package org.intellij.markdown

import kotlin.test.Test
import kotlin.test.assertTrue

class CustomSpecTest : SpecTest(org.intellij.markdown.flavours.custom.CustomFlavourDescriptor(16)) {
    @Test
    fun testHeadings1() = doTest(
        markdown = """
            ## heading 1
            ### heading 2
            #### heading 3
            ##### heading 4
            ###### heading 5
            ####### heading 6
        """.trimIndent(),
        html = html {
            heading(1) { text("heading 1") }
            heading(2) { text("heading 2") }
            heading(3) { text("heading 3") }
            heading(4) { text("heading 4") }
            heading(5) { text("heading 5") }
            heading(6) { text("heading 6") }
        }
    )

    @Test
    fun testHeadings2() = doTest(
        markdown = """
            # not heading 1
            ######## not heading 2
            """.trimIndent(),
        html = html {
                paragraph {
                text("# not heading 1")
                br()
                text("######## not heading 2")
            }
        }
    )

    @Test
    fun testHeadings3() = doTest(
        markdown = """
            ##
            ##123
            ##abc
        """.trimIndent(),
        html = html {
            paragraph {
                text("##")
                br()
                text("##123")
                br()
                text("##abc")
            }
        }
    )

    @Test
    fun testHeadings4() = doTest(
        markdown = "##              heading            \n",
        html = html {
            heading(1) { text("heading") }
        }
    )

    @Test
    fun testHeadings5() = doTest(
        markdown = "           ## heading\n",
        html = html {
            heading(1) { text("heading") }
        }
    )

    @Test
    fun testHeadings6() = doTest(
        markdown = "## heading ##      hashes\n",
        html = html {
            heading(1) { text("heading ## hashes") }
        }
    )

    @Test
    fun testHeading7() = doTest(
        markdown = "## **bold** __italic__ ~~strike~~ ++underline++",
        html = html {
            heading(1) {
                bold { text("bold") }
                text(" ")
                italic { text("italic") }
                text(" ")
                strike { text("strike") }
                text(" ")
                underline { text("underline") }
            }
        }
    )

    @Test
    fun testHeading8() = doTest(
        markdown = "## **bold __italic ~~strike ++underline++~~__**",
        html = html {
            heading(1) {
                bold {
                    text("bold")
                    text(" ")
                    italic {
                        text("italic")
                        text(" ")
                        strike {
                            text("strike")
                            text(" ")
                            underline { text("underline") }
                        }
                    }
                }
            }
        }
    )

    @Test
    fun testParagraph1() = doTest(
        markdown = "some text",
        html = html {
            paragraph { text("some text") }
        }
    )

    @Test
    fun testParagraph2() = doTest(
        markdown = """
            some text
            some text
        """.trimMargin(),
        html = html {
                paragraph {
                text("some text")
                br()
                text("some text")
            }
        }
    )

    @Test
    fun testParagraph3() = doTest(
        markdown = """
            some text
            
            some text
            """.trimIndent(),
        html = html {
            paragraph { text("some text") }
            paragraph { text("some text") }
        }
    )

    @Test
    fun testParagraph4() = doTest(
        markdown = """
            some text
            ## heading
            some text
        """.trimIndent(),
        html = html {
            paragraph { text("some text") }
            heading(1) { text("heading") }
            paragraph { text("some text") }
        }
    )


    @Test
    fun testParagraph5() = doTest(
        markdown = """
            paragraph
            paragraph
            
            
            
                     paragraph
            
            paragraph
            """.trimIndent(),
        html = html {
            paragraph {
                text("paragraph")
                br()
                text("paragraph")
            }
            paragraph { text("paragraph") }
            paragraph { text("paragraph") }
        }
    )

    @Test
    fun testParagraph6() = doTest(
        markdown = "This is **bold** and __italic__ text with ~~strikethrough~~ and ++underline++.",
        html = html {
            paragraph {
                text("This is ")
                bold { text("bold") }
                text(" and ")
                italic { text("italic") }
                text(" text with ")
                strike { text("strikethrough") }
                text(" and ")
                underline { text("underline") }
                text(".")
            }
        }
    )

    @Test
    fun testParagraph7() = doTest(
        markdown = """
            First line with **bold**.
            Second line with __italic__.
            Third line with ~~strike~~.
            """.trimIndent(),
        html = html {
            paragraph {
                text("First line with ")
                bold { text("bold") }
                text(".")
                br()
                text("Second line with ")
                italic { text("italic") }
                text(".")
                br()
                text("Third line with ")
                strike { text("strike") }
                text(".")
            }
        }
    )

    @Test
    fun testInlineFormatting1() = doTest(
        markdown = "This is **bold text** in a sentence.",
        html = html {
            paragraph {
                text("This is ")
                bold { text("bold text") }
                text(" in a sentence.")
            }
        }
    )

    @Test
    fun testInlineFormatting2() = doTest(
        markdown = "This is __italic text__ in a sentence.",
        html = html {
            paragraph {
                text("This is ")
                italic { text("italic text") }
                text(" in a sentence.")
            }
        }
    )

    @Test
    fun testInlineFormatting3() = doTest(
        markdown = "This is ~~strikethrough text~~ in a sentence.",
        html = html {
            paragraph {
                text("This is ")
                strike { text("strikethrough text") }
                text(" in a sentence.")
            }
        }
    )

    @Test
    fun testInlineFormatting4() = doTest(
        markdown = "This is ++underlined text++ in a sentence.",
        html = html {
            paragraph {
                text("This is ")
                underline { text("underlined text") }
                text(" in a sentence.")
            }
        }
    )

    @Test
    fun testInlineFormatting5() = doTest(
        markdown = "This is **bold with __italic inside__** text.",
        html = html {
            paragraph {
                text("This is ")
                bold {
                    text("bold with ")
                    italic { text("italic inside") }
                }
                text(" text.")
            }
        }
    )

    @Test
    fun testInlineFormatting6() = doTest(
        markdown = "**Bold** __Italic__ ~~Strike~~ ++Underline++",
        html = html {
            paragraph {
                bold { text("Bold") }
                text(" ")
                italic { text("Italic") }
                text(" ")
                strike { text("Strike") }
                text(" ")
                underline { text("Underline") }
            }
        }
    )

    @Test
    fun testInlineFormatting7() = doTest(
        markdown = "This is \\**not bold\\** and \\__not italic\\__.",
        html = html {
            paragraph {
                text("This is **not bold** and __not italic__.")
            }
        }
    )

    @Test
    fun testInlineFormatting8() = doTest(
        markdown = "This is **unclosed bold __with italic__",
        html = html {
            paragraph {
                text("This is **unclosed bold ")
                italic { text("with italic") }
            }
        }
    )

    @Test
    fun testUnorderedList1() = doTest(
        markdown = """
            -- A
            -- B
        """.trimIndent(),
        html = html {
                ulist {
                item { text("A") }
                item { text("B") }
            }
        }
    )

    @Test
    fun testUnorderedList2() = doTest(
        markdown = """
            -- A
            --- B
            -- C
            """.trimIndent(),
        html = html {
                ulist {
                    item { text("A") }
                    ulist { item { text("B") } }
                    item { text("C")
                }
            }
        }
    )

    @Test
    fun testUnorderedList3() = doTest(
        markdown = """
            -- A
            --- B
            ---- C
            """.trimIndent(),
        html = html {
            ulist {
                item { text("A") }
                ulist {
                    item { text("B") }
                    ulist { item { text("C") } }
                }
            }
        }
    )

    @Test
    fun testUnorderedList4() = doTest(
        markdown = """
            -- A
            --- B
            ---- C
            --- D
            -- E
            """.trimIndent(),
        html = html {
            ulist {
                item { text("A") }
                ulist {
                    item { text("B") }
                    ulist { item { text("C") } }
                    item { text("D") }
                }
                item { text("E") }
            }
        }
    )

    @Test
    fun testUnorderedList5() = doTest(
        markdown = """
            -- A
            --- B
            ---- C
            -- D
            """.trimIndent(),
        html = html {
            ulist {
                item { text("A") }
                ulist {
                    item { text("B") }
                    ulist { item { text("C") } }
                }
                item { text("D") }
            }
        }
    )

    @Test
    fun testUnorderedList6() {
        assertTrue(runCatching { doTest(
            markdown = """
            ---- A
            --- B
            -- C          
        """.trimIndent(),
            html = html {
                ulist {
                    ulist {
                        ulist {
                            item { text("A") }
                        }
                        item { text("B") }
                    }
                    item { text("C") }
                }
            }
        ) }.isFailure)
    }

    @Test
    fun testUnorderedList7() = doTest(
        markdown = """
            -- **Bold item**
            --- __Italic nested item__
            -- ~~Strike item~~
        """.trimIndent(),
        html = html {
            ulist {
                item { bold { text("Bold item") } }
                ulist {
                    item { italic { text("Italic nested item") } }
                }
                item { strike { text("Strike item") } }
            }
        }
    )

    @Test
    fun testHLine1() = doTest(
        markdown = """
            -----
                   -----
            -----      
                 -----     
            ---
            -------
            test ----- 
            -----     test
        """.trimIndent(),
        html = html {
            hr()
            hr()
            hr()
            hr()
            paragraph {
                text("---")
                br()
                text("-------")
                br()
                text("test -----")
                br()
                text("----- test")
            }
        }
    )

    @Test
    fun testHLine2() = doTest(
        markdown = """
            ## heading 1
            -----
            some text 1
            -----
            some text 2
            -- A
            --- B
            -----
            -- C
            """.trimIndent(),
        html = html {
            heading(1) { text("heading 1") }
            hr()
            paragraph { text("some text 1") }
            hr()
            paragraph { text("some text 2") }
            ulist {
                item { text("A") }
                ulist { item { text("B") } }
            }
            hr()
            ulist { item { text("C") } }
        }
    )

    @Test
    fun testHLine3() = doTest(
        markdown = """
            -----
            -----
            -----
            -----
            -----
        """.trimIndent(),
        html = html {
            hr()
            hr()
            hr()
            hr()
            hr()
        }
    )

    @Test
    fun testOrderedList1() = doTest(
        markdown = """
            1.. A
            1.. B
            """.trimIndent(),
        html = html {
            olist(1) {
                item { text("A") }
                item { text("B") }
            }
        }
    )

    @Test
    fun testOrderedList2() = doTest(
        markdown = """
            1.. A
            2... B
            1.. C
            """.trimIndent(),
        html = html {
            olist(1) {
                item { text("A") }
                olist(2) { item { text("B") } }
                item { text("C") }
            }
        }
    )

    @Test
    fun testOrderedList3() = doTest(
        markdown = """
            1.. A
            2... B
            3.... C
            """.trimIndent(),
        html = html {
            olist(1) {
                item { text("A") }
                olist(2) {
                    item { text("B") }
                    olist(3) { item { text("C") } }
                }
            }
        }
    )

    @Test
    fun testOrderedList4() = doTest(
        markdown = """
            1.. A
            2... B
            3.... C
            2... D
            1.. E
            """.trimIndent(),
        html = html {
            olist(1) {
                item { text("A") }
                olist(2) {
                    item { text("B") }
                    olist(3) { item { text("C") } }
                    item { text("D") }
                }
                item { text("E") }
            }
        }
    )

    @Test
    fun testOrderedList5() = doTest(
        markdown = """
            1.. A
            2... B
            3.... C
            1.. D
        """.trimIndent(),
        html = html {
            olist(1) {
                item { text("A") }
                olist(2) {
                    item { text("B") }
                    olist(3) { item { text("C") } }
                }
                item { text("D") }
            }
        }
    )

    @Test
    fun testOrderedList6() {
        assertTrue(runCatching { doTest(
            markdown = """
                3.... A
                2... B
                1.. C
            """.trimIndent(),
            html = html {
                olist(3) {
                    olist(2) {
                        olist(1) { item { text("A") } }
                        item { text("B") }
                    }
                    item { text("C") }
                }
            }
        ) }.isFailure)
    }

    @Test
    fun testOrderedList7() = doTest(
        markdown = """
            1.. **Bold item**
            2... __Italic nested item__
            1.. ~~Strike item~~
        """.trimIndent(),
        html = html {
            olist(1) {
                item {
                    bold { text("Bold item") }
                }
                olist(2) {
                    item {
                        italic { text("Italic nested item") }
                    }
                }
                item {
                    strike { text("Strike item") }
                }
            }
        }
    )

    @Test
    fun testOrderedList8() = doTest(
        markdown = """
            5.. Item A
            6.. Item B
            7.. Item C
        """.trimIndent(),
        html = html {
            olist(5) {
                item { text("Item A") }
                item { text("Item B") }
                item { text("Item C") }
            }
        }
    )

    @Test
    fun testMixedContent1() = doTest(
        markdown = """
            ## Main Heading

            This is a **paragraph** with __mixed__ formatting.

            -----

            -- List item 1
            --- Nested item
            -- List item 2

            Another paragraph with ~~strikethrough~~ text.

            ### Sub Heading

            1.. Ordered item
            2... Nested ordered
            1.. Back to top level

            Final paragraph with ++underline++.
        """.trimIndent(),
        html = html {
            heading(1) { text("Main Heading") }
            paragraph {
                text("This is a ")
                bold { text("paragraph") }
                text(" with ")
                italic { text("mixed") }
                text(" formatting.")
            }
            hr()
            ulist {
                item { text("List item 1") }
                ulist { item { text("Nested item") } }
                item { text("List item 2") }
            }
            paragraph {
                text("Another paragraph with ")
                strike { text("strikethrough") }
                text(" text.")
            }
            heading(2) { text("Sub Heading") }
            olist(1) {
                item { text("Ordered item") }
                olist(2) { item { text("Nested ordered") } }
                item { text("Back to top level") }
            }
            paragraph {
                text("Final paragraph with ")
                underline { text("underline") }
                text(".")
            }
        }
    )

    @Test
    fun testEdgeCaseWhitespace() = doTest(
        markdown = """



        """.trimIndent(),
        html = html { }
    )
}