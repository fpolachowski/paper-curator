package de.fpolachowski.papercurator.util

class StringManipulator {
    companion object {
        private fun removeLineBreaks(text: String): String {
            return text.replace(Regex("""(\r\n)|\n|"""), "")
        }

        private fun removeWideSpaces(text: String): String {
            return text.replace(Regex("""\s+"""), " ")
        }

        private fun removeIllegalCharacters(text: String): String {
            return text.replace("\u0000", "")
        }

        fun cleanString(text : String): String {
            var newText = removeLineBreaks(text)
            newText = removeWideSpaces(newText)
            newText = removeIllegalCharacters(newText)
            return newText
        }
    }

}