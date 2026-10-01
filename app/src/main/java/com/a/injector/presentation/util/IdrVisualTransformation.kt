package com.a.injector.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class IdrVisualTransformation(
    private val currencySymbol: String = "Rp "
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val number = originalText.toLongOrNull() ?: 0L
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedText = "$currencySymbol${formatter.format(number)}"

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val substring = originalText.take(offset)
                val subNumber = substring.toLongOrNull() ?: 0L
                val subFormatted = "$currencySymbol${formatter.format(subNumber)}"
                return subFormatted.length.coerceAtMost(formattedText.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= currencySymbol.length) return 0
                val digitsBeforeOffset = formattedText.take(offset).count { it.isDigit() }
                return digitsBeforeOffset.coerceAtMost(originalText.length)
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}