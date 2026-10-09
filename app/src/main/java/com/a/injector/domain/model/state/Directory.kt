package com.a.injector.domain.model.state

enum class Directory(
    val absoluteName: String
) {
    Image("image"),
    Downloaded("downloaded"),
    Extracted("extracted")
}