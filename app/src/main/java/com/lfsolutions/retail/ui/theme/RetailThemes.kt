package com.lfsolutions.retail.ui.theme

enum class RetailThemes(
    val themeName: String,
    val primary: String,
    val secondary: String,
    val tertiary: String,
    var selected: Boolean = false
) {
    Yellow(
        "Yellow",
        "#FEF400",
        "#FEF1AB",
        "#FF9800"
    ),
    Blue(
        "Blue",
        "#03A9F4",
        "#B3E5FC",
        "#536DFE"
    ),
    Green(
        "Green",
        "#8BC34A",
        "#DCEDC8",
        "#009688"
    ),
    Brown(
        "Brown",
        "#795548",
        "#D7CCC8",
        "#607D8B"
    ),
}