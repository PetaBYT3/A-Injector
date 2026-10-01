package com.a.injector.presentation.supportdev

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.a.injector.R
import com.a.injector.domain.model.StaticModel

enum class SupportMethod {
    Qris, Seabank
}

val supportMethods = listOf(
    StaticModel(
        id = SupportMethod.Qris,
        leadingContent = { Icon(Icons.Rounded.QrCode, null) },
        content = R.string.qris,
        supportingContent = R.string.qris_desc
    ),
    StaticModel(
        id = SupportMethod.Seabank,
        leadingContent = { Icon(ImageVector.vectorResource(R.drawable.seabank), null) },
        content = R.string.seabank,
        supportingContent = R.string.seabank_desc
    )
)


val validateSupportSteps = listOf(
    R.string.validate_supp_1,
    R.string.validate_supp_2,
    R.string.validate_supp_3,
    R.string.validate_supp_4,
    R.string.validate_supp_5,
    R.string.validate_supp_6,
)