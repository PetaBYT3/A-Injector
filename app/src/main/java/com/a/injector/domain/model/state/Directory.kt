package com.a.injector.domain.model.state

enum class Directory(
    val absoluteName: String
) {
    Image("image"),
    Script("script"),
    Extracted("extracted")
}