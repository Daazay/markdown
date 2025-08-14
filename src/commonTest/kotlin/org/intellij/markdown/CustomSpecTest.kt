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
        html = html { paragraph {
            text("# not heading 1")
            br()
            text("######## not heading 2")
        } }
    )

    @Test
    fun testHeadings3() = doTest(
        markdown = """
            ##
            ##123
            ##abc
        """.trimIndent(),
        html = html { paragraph {
            text("##")
            br()
            text("##123")
            br()
            text("##abc")
        } }
    )

    @Test
    fun testHeadings4() = doTest(
        markdown = "## **strong**",
        html = html { heading(1) { bold { text("strong") } } }
    )

    @Test
    fun testHeadings5() = doTest(
        markdown = "##              heading            \n",
        html = html { heading(1) { text("heading") } }
    )

    @Test
    fun testHeadings6() = doTest(
        markdown = "           ## heading\n",
        html = html { heading(1) { text("heading") } }
    )

    @Test
    fun testHeadings7() = doTest(
        markdown = "## heading ##      hashes\n",
        html = html { heading(1) { text("heading ## hashes") } }
    )

    @Test
    fun testParagraph1() = doTest(
        markdown = "some text",
        html = html { paragraph { text("some text") } }
    )

    @Test
    fun testParagraph2() = doTest(
        markdown = """
            some text
            some text
        """.trimMargin(),
        html = html { paragraph {
            text("some text")
            br()
            text("some text")
        } }
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
    fun testUnorderedList1() = doTest(
        markdown = """
            -- A
            -- B
        """.trimIndent(),
        html = html { ulist {
            item { text("A") }
            item { text("B") }
        } }
    )

    @Test
    fun testUnorderedList2() = doTest(
        markdown = """
            -- A
            --- B
            -- C
            """.trimIndent(),
        html = html { ulist {
            item { text("A") }
            ulist { item { text("B") } }
            item { text("C") }
        } }
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
            ## Heading 1
            ### Heading 2
            some paragraph
            
            some sentence 1
            some sentence 2
            -- A
            --- B
            -- C
            some end paragraph
            """.trimIndent(),
        html = html {
            heading(1) { text("Heading 1") }
            heading(2) { text("Heading 2") }
            paragraph { text("some paragraph") }
            paragraph {
                text("some sentence 1")
                br()
                text("some sentence 2")
            }
            ulist {
                item { text("A") }
                ulist {
                    item { text("B") }
                }
                item { text("C") }
            }
            paragraph { text("some end paragraph") }
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
    fun testOrderedList1() = doTest(
        markdown = """
            1.. A
            1.. B
            """.trimIndent(),
        html = html { olist(1) {
            item { text("A") }
            item { text("B") }
        } }
    )

    @Test
    fun testOrderedList2() = doTest(
        markdown = """
            1.. A
            2... B
            1.. C
            """.trimIndent(),
        html = html { olist(1) {
            item { text("A") }
            olist(2) { item { text("B") } }
            item { text("C") }
        } }
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
}