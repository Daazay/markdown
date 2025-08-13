package org.intellij.markdown

import kotlin.test.Test

class CustomSpecTest : SpecTest(org.intellij.markdown.flavours.custom.CustomFlavourDescriptor(16)) {
    @Test
    fun testHeadings1() = doTest(
        markdown = "## heading 1\n### heading 2\n#### heading 3\n##### heading 4\n###### heading 5\n####### heading 6\n",
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
        markdown = "# not heading 1\n######## not heading 2\n",
        html = html { paragraph {
            text("# not heading 1")
            br()
            text("######## not heading 2")
        } }
    )

    @Test
    fun testHeadings3() = doTest(
        markdown = "##\n##123\n##abc\n",
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
        markdown = "## **strong**\n",
        html = html { heading(1) { bold { text("strong") } } }
    )

    @Test
    fun testHeadings5() = doTest(
        markdown = "##                     heading                         \n",
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
        markdown = "some text\nsome text",
        html = html { paragraph {
            text("some text")
            br()
            text("some text")
        } }
    )

    @Test
    fun testParagraph3() = doTest(
        markdown = "some text\n\nsome text",
        html = html {
            paragraph { text("some text") }
            paragraph { text("some text") }
        }
    )

    @Test
    fun testParagraph4() = doTest(
        markdown = "some text\n## heading\nsome text",
        html = html {
            paragraph { text("some text") }
            heading(1) { text("heading") }
            paragraph { text("some text") }
        }
    )


    @Test
    fun testParagraph5() = doTest(
        markdown =
            "paragraph\n" +
            "paragraph\n" +
            "\n" +
            "\n" +
            "\n" +
            "    paragraph\n" +
            "\n" +
            "paragraph\n",
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
        markdown =
            "-- A\n" +
            "-- B\n",
        html = html { ulist {
            item { text("A") }
            item { text("B") }
        } }
    )

    @Test
    fun testUnorderedList2() = doTest(
        markdown =
            "-- A\n" +
            "--- B\n" +
            "-- C\n",
        html = html { ulist {
            item { text("A") }
            ulist { item { text("B") } }
            item { text("C") }
        } }
    )

    @Test
    fun testUnorderedList3() = doTest(
        markdown =
            "-- A\n" +
            "--- B\n" +
            "---- C\n",
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
        markdown =
            "-- A\n" +
            "--- B\n" +
            "---- C\n" +
            "--- D\n" +
            "-- E\n",
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
        markdown =
            "-- A\n" +
            "--- B\n" +
            "---- C\n" +
            "-- D\n",
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
    fun testUnorderedList6() = doTest(
        markdown =
            "---- A\n" +
            "--- B\n" +
            "-- C\n",
        html = html { ulist { ulist { ulist {
                        item { text("A") }
                    }
                    item { text("B") }
                }
                item { text("C") }
            }
        }
    )


    @Test
    fun testUnorderedList7() = doTest(
        markdown =
            "## Heading 1\n" +
            "### Heading 2\n" +
            "some paragraph\n" +
            "\n" +
            "some sentence 1\n" +
            "some sentence 2\n" +
            "---- A\n" +
            "--- B\n" +
            "-- C\n" +
            "some end paragraph\n",
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
                ulist {
                    ulist {
                        item { text("A") }
                    }
                    item { text("B") }
                }
                item { text("C") }
            }
            paragraph { text("some end paragraph") }
        }
    )

    @Test
    fun testHLine1() = doTest(
        markdown =
            "-----\n" +
            "       -----\n" +
            "-----      \n" +
            "     -----     \n" +
            "---\n" +
            "-------\n" +
            "test ----- \n" +
            "-----     test\n",
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
        markdown =
            "## heading 1\n" +
            "-----\n" +
            "some text 1\n" +
            "-----\n" +
            "some text 2\n" +
            "-- A\n" +
            "--- B\n" +
            "-----\n" +
            "--- C\n",
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
            ulist { ulist { item { text("C") } } }
        }
    )
}