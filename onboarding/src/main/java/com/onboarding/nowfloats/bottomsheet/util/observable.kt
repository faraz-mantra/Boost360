package com.onboarding.nowfloats.bottomsheet.util

import com.onboarding.nowfloats.bottomsheet.inerfaces.ContentBuilder
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> listenToUpdate(initialValue: T, builder: ContentBuilder, type: Int = 0)
        : ReadWriteProperty<Any?, T> = ChangeToNotify(initialValue, builder, type)

class ChangeToNotify<T>(
        initialValue: T,
        private val builder: ContentBuilder,
        private val type: Int
) : ObservableProperty<T>(initialValue) {

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        runOnUi {
            try {
                builder.updateContent(type)
            } catch (e: Exception) {
            }
        }
    }
}

fun <T> listenListToUpdate(initialValue: ObservableList<T>, builder: ContentBuilder)
        : ListChangeToNotify<T> = ListChangeToNotify(initialValue, builder)


class ListChangeToNotify<T>(initialValue: ObservableList<T>, builder: ContentBuilder) {
    private val value = initialValue

    init {
        initialValue.listener = object : DataCahngedListener<T> {
            override fun onAddAll(elements: Collection<T>) {
                runOnUi {
                    builder.updateContent(3)
                }
            }

            override fun onRemove(elements: Collection<T>) {
                runOnUi {
                    builder.updateContent(3)
                }
            }

            override fun onRemove(elements: T) {
                runOnUi {
                    builder.updateContent(3)
                }
            }

            override fun onAdd(element: T) {
                runOnUi {
                    builder.updateContent(1, initialValue.size - 1)
                }
            }

            override fun onAdd(index: Int, element: T) {
                runOnUi {
                    builder.updateContent(1, index)
                }
            }

            override fun onRemoveRange(fromIndex: Int, toIndex: Int) {
                runOnUi {
                    builder.updateContent(2, Pair(fromIndex, toIndex))
                }
            }

            override fun onSet(index: Int, element: T) {
                runOnUi {
                    builder.updateContent(3, index)
                }
            }

            override fun onRemoveAt(index: Int) {
                runOnUi {
                    builder.updateContent(2, Pair(index, index))
                }
            }
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): ObservableList<T> {
        return value
    }

}
