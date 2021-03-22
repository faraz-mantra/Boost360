package dev.patrickgold.florisboard.customization

import androidx.annotation.LayoutRes
import dev.patrickgold.florisboard.R

enum class BusinessFeatureEnum(name: String) {
    UPDATES("Updates"),
    INVENTORY("Inventory"),
    PHOTOS("Photos"),
    DETAILS("Details");

    @LayoutRes
    fun getLayout(): Int {
        return when (this) {
            UPDATES -> R.layout.adapter_item_text
            INVENTORY -> R.layout.adapter_item_product
            PHOTOS -> R.layout.adapter_item_photos
            DETAILS -> R.layout.adapter_item_details
        }
    }
}