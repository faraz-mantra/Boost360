/*
 * Copyright (C) 2020 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.ime.theme

import java.util.*

/**
 * Enum class which specifies all theme modes available. Used in the Settings to properly manage
 * different use cases when the day or night theme should be active.
 */
enum class ThemeMode {
    ALWAYS_DAY,
    ALWAYS_NIGHT,
    FOLLOW_SYSTEM,
    FOLLOW_TIME;

    companion object {
        fun fromString(string: String): ThemeMode {
            return valueOf(string.toUpperCase(Locale.ENGLISH))
        }
    }
}
