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

package dev.patrickgold.florisboard.ime.text

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.view.KeyEvent
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.google.android.material.tabs.TabLayout
import dev.patrickgold.florisboard.BuildConfig
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.BusinessFeatureEnum
import dev.patrickgold.florisboard.customization.BusinessFeaturesViewModel
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.adapter.SharedAdapter
import dev.patrickgold.florisboard.customization.util.MethodUtils
import dev.patrickgold.florisboard.customization.util.SharedPrefUtil
import dev.patrickgold.florisboard.databinding.BusinessFeaturesLayoutBinding
import dev.patrickgold.florisboard.ime.core.*
import dev.patrickgold.florisboard.ime.dictionary.Dictionary
import dev.patrickgold.florisboard.ime.dictionary.DictionaryManager
import dev.patrickgold.florisboard.ime.extension.AssetRef
import dev.patrickgold.florisboard.ime.extension.AssetSource
import dev.patrickgold.florisboard.ime.nlp.Token
import dev.patrickgold.florisboard.ime.nlp.toStringList
import dev.patrickgold.florisboard.ime.text.editing.EditingKeyboardView
import dev.patrickgold.florisboard.ime.text.gestures.SwipeAction
import dev.patrickgold.florisboard.ime.text.key.*
import dev.patrickgold.florisboard.ime.text.keyboard.KeyboardMode
import dev.patrickgold.florisboard.ime.text.keyboard.KeyboardView
import dev.patrickgold.florisboard.ime.text.layout.LayoutManager
import dev.patrickgold.florisboard.ime.text.smartbar.SmartbarView
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import kotlin.math.roundToLong

/**
 * TextInputManager is responsible for managing everything which is related to text input. All of
 * the following count as text input: character, numeric (+advanced), phone and symbol layouts.
 *
 * All of the UI for the different keyboard layouts are kept under the same container element and
 * are separated from media-related UI. The core [FlorisBoard] will pass any event defined in
 * [FlorisBoard.EventListener] through to this class.
 *
 * TextInputManager is also the hub in the communication between the system, the active editor
 * instance and the Smartbar.
 */
class TextInputManager private constructor() : CoroutineScope by MainScope(), InputKeyEventReceiver,
        FlorisBoard.EventListener, SmartbarView.EventListener, TabLayout.OnTabSelectedListener,
        SwipeRefreshLayout.OnRefreshListener, OnItemClickListener {

    private val florisboard = FlorisBoard.getInstance()
    private val activeEditorInstance: EditorInstance
        get() = florisboard.activeEditorInstance

    private var activeKeyboardMode: KeyboardMode? = null
    private var animator: ObjectAnimator? = null
    private val keyboardViews = EnumMap<KeyboardMode, KeyboardView>(KeyboardMode::class.java)
    private var editingKeyboardView: EditingKeyboardView? = null
    private var loadingPlaceholderKeyboard: KeyboardView? = null
    private var textViewFlipper: ViewFlipper? = null
    private var textViewGroup: LinearLayout? = null
    private val dictionaryManager: DictionaryManager = DictionaryManager.default()
    private var activeDictionary: Dictionary<String, Int>? = null
    val inputEventDispatcher: InputEventDispatcher = InputEventDispatcher.new(
            parentScope = this,
            repeatableKeyCodes = intArrayOf(
                    KeyCode.ARROW_DOWN,
                    KeyCode.ARROW_LEFT,
                    KeyCode.ARROW_RIGHT,
                    KeyCode.ARROW_UP,
                    KeyCode.DELETE,
                    KeyCode.FORWARD_DELETE
            )
    )

    var keyVariation: KeyVariation = KeyVariation.NORMAL
    val layoutManager = LayoutManager(florisboard)
    private var smartbarView: SmartbarView? = null

    // Caps/Shift related properties
    var caps: Boolean = false
        private set
    var capsLock: Boolean = false
        private set
    private var newCapsState: Boolean = false

    // Composing text related properties
    var isManualSelectionMode: Boolean = false
    private var isManualSelectionModeStart: Boolean = false
    private var isManualSelectionModeEnd: Boolean = false

    private lateinit var mContext: Context
    private var showFeatureUI = false
    private var currentTab: TabLayout.Tab? = null
    private lateinit var binding: BusinessFeaturesLayoutBinding
    private lateinit var recyclerViewPost: RecyclerView
    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var businessFeatureProgressBar: ProgressBar
    private val viewModel: BusinessFeaturesViewModel = BusinessFeaturesViewModel()
    private val adapter: SharedAdapter = SharedAdapter(this)
    private val pagerSnapHelper = PagerSnapHelper()


    companion object {
        private var instance: TextInputManager? = null

        @Synchronized
        fun getInstance(): TextInputManager {
            if (instance == null) {
                instance = TextInputManager()
            }
            return instance!!
        }
    }

    init {
        florisboard.addEventListener(this)
    }

    /**
     * Non-UI-related setup + preloading of all required computed layouts (asynchronous in the
     * background).
     */
    override fun onCreate() {
        Timber.i("onCreate()")

        inputEventDispatcher.keyEventReceiver = this
        var subtypes = florisboard.subtypeManager.subtypes
        if (subtypes.isEmpty()) {
            subtypes = listOf(Subtype.DEFAULT)
        }
        for (subtype in subtypes) {
            for (mode in KeyboardMode.values()) {
                layoutManager.preloadComputedLayout(mode, subtype, florisboard.prefs)
            }
        }
    }

    override fun onCreateInputView() {
        keyboardViews.clear()
    }

    private suspend fun addKeyboardView(mode: KeyboardMode) {
        val keyboardView = KeyboardView(florisboard.context)
        keyboardView.computedLayout = layoutManager.fetchComputedLayoutAsync(mode, florisboard.activeSubtype, florisboard.prefs).await()
        keyboardViews[mode] = keyboardView
        textViewFlipper?.addView(keyboardView)
    }

    /**
     * Sets up the newly registered input view.
     */
    override fun onRegisterInputView(inputView: InputView) {
        Timber.i("onRegisterInputView(inputView)")

        mContext = inputView.context
        textViewGroup = inputView.findViewById(R.id.text_input)
        textViewFlipper = inputView.findViewById(R.id.text_input_view_flipper)
        editingKeyboardView = inputView.findViewById(R.id.editing)
        loadingPlaceholderKeyboard = inputView.findViewById(R.id.keyboard_preview)

        // initialize business features views
        binding = BusinessFeaturesLayoutBinding.bind(inputView.findViewById(R.id.business_features))

        swipeRefresh = binding.swipeRefresh
        swipeRefresh.setOnRefreshListener(this)

        businessFeatureProgressBar = binding.businessFeatureProgress

        recyclerViewPost = binding.productShareRvList.also {
            it.layoutManager = LinearLayoutManager(mContext).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            it.adapter = adapter
        }
        pagerSnapHelper.attachToRecyclerView(recyclerViewPost)

        recyclerViewPhotos = binding.rvListPhotos.also {
            it.layoutManager = GridLayoutManager(mContext, 2,
                    GridLayoutManager.HORIZONTAL, false)
            it.adapter = adapter
        }
        launch(Dispatchers.Main) {
            textViewGroup?.let {
                animator = ObjectAnimator.ofFloat(it, "alpha", 0.9f, 1.0f).apply {
                    duration = 125
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.REVERSE
                    start()
                    launch {
                        delay(duration)
                        try {
                            duration = 500
                            setFloatValues(1.0f, 0.4f)
                        } catch (_: Exception) {
                        }
                    }
                }
            }
            val activeKeyboardMode = getActiveKeyboardMode()
            addKeyboardView(activeKeyboardMode)
            setActiveKeyboardMode(activeKeyboardMode)
            animator?.cancel()
            textViewGroup?.let {
                animator = ObjectAnimator.ofFloat(it, "alpha", it.alpha, 1.0f).apply {
                    duration = (((1.0f - it.alpha) / 0.6f) * 125f).roundToLong()
                    repeatCount = 0
                    start()
                }
            }
            for (mode in KeyboardMode.values()) {
                if (mode != activeKeyboardMode && mode != KeyboardMode.SMARTBAR_NUMBER_ROW) {
                    addKeyboardView(mode)
                }
            }
        }
    }

    fun registerSmartbarView(view: SmartbarView) {
        smartbarView = view
        smartbarView?.setEventListener(this)
    }

    fun unregisterSmartbarView(view: SmartbarView) {
        if (smartbarView == view) {
            smartbarView = null
        }
    }

    /**
     * Cancels all coroutines and cleans up.
     */
    override fun onDestroy() {
        Timber.i("onDestroy()")
        inputEventDispatcher.keyEventReceiver = null
        inputEventDispatcher.close()
        clearObservers()
        cancel()
        layoutManager.onDestroy()
        instance = null
    }

    private fun clearObservers() {
        viewModel.updates.removeObserver {}
        viewModel.details.removeObserver {}
        viewModel.photos.removeObserver {}
        viewModel.products.removeObserver {}
    }

    /**
     * Evaluates the [activeKeyboardMode], [keyVariation] and [EditorInstance.isComposingEnabled]
     * property values when starting to interact with a input editor. Also resets the composing
     * texts and sets the initial caps mode accordingly.
     */
    override fun onStartInputView(instance: EditorInstance, restarting: Boolean) {
        val keyboardMode = when (instance.inputAttributes.type) {
            InputAttributes.Type.NUMBER -> {
                keyVariation = KeyVariation.NORMAL
                KeyboardMode.NUMERIC
            }
            InputAttributes.Type.PHONE -> {
                keyVariation = KeyVariation.NORMAL
                KeyboardMode.PHONE
            }
            InputAttributes.Type.TEXT -> {
                keyVariation = when (instance.inputAttributes.variation) {
                    InputAttributes.Variation.EMAIL_ADDRESS,
                    InputAttributes.Variation.WEB_EMAIL_ADDRESS -> {
                        KeyVariation.EMAIL_ADDRESS
                    }
                    InputAttributes.Variation.PASSWORD,
                    InputAttributes.Variation.VISIBLE_PASSWORD,
                    InputAttributes.Variation.WEB_PASSWORD -> {
                        KeyVariation.PASSWORD
                    }
                    InputAttributes.Variation.URI -> {
                        KeyVariation.URI
                    }
                    else -> {
                        KeyVariation.NORMAL
                    }
                }
                KeyboardMode.CHARACTERS
            }
            else -> {
                keyVariation = KeyVariation.NORMAL
                KeyboardMode.CHARACTERS
            }
        }
        instance.apply {
            isComposingEnabled = when (keyboardMode) {
                KeyboardMode.NUMERIC,
                KeyboardMode.PHONE,
                KeyboardMode.PHONE2 -> false
                else -> keyVariation != KeyVariation.PASSWORD &&
                        florisboard.prefs.suggestion.enabled// &&
                //!instance.inputAttributes.flagTextAutoComplete &&
                //!instance.inputAttributes.flagTextNoSuggestions
            }
            isPrivateMode = florisboard.prefs.advanced.forcePrivateMode ||
                    imeOptions.flagNoPersonalizedLearning
        }
        if (!florisboard.prefs.correction.rememberCapsLockState) {
            capsLock = false
        }
        updateCapsState()
        setActiveKeyboardMode(keyboardMode)
        smartbarView?.updateSmartbarState()
    }

    /**
     * Handle stuff when finishing to interact with a input editor.
     */
    override fun onFinishInputView(finishingInput: Boolean) {
        smartbarView?.updateSmartbarState()
    }

    override fun onWindowShown() {
        keyboardViews[KeyboardMode.CHARACTERS]?.updateVisibility()
        smartbarView?.updateSmartbarState()
    }

    /**
     * Gets [activeKeyboardMode].
     *
     * @return If null [KeyboardMode.CHARACTERS], else [activeKeyboardMode].
     */
    fun getActiveKeyboardMode(): KeyboardMode {
        return activeKeyboardMode ?: KeyboardMode.CHARACTERS
    }

    /**
     * Sets [activeKeyboardMode] and updates the [SmartbarView.isQuickActionsVisible] state.
     */
    private fun setActiveKeyboardMode(mode: KeyboardMode) {
        textViewFlipper?.displayedChild = textViewFlipper?.indexOfChild(when (mode) {
            KeyboardMode.EDITING -> editingKeyboardView
            KeyboardMode.BUSINESS_FEATURES -> binding.root
            else -> keyboardViews[mode]
        })?.coerceAtLeast(0) ?: 0
        keyboardViews[mode]?.updateVisibility()
        keyboardViews[mode]?.requestLayout()
        keyboardViews[mode]?.requestLayoutAllKeys()
        activeKeyboardMode = mode
        isManualSelectionMode = false
        isManualSelectionModeStart = false
        isManualSelectionModeEnd = false
        smartbarView?.isQuickActionsVisible = false
        if (mode != KeyboardMode.BUSINESS_FEATURES)
            smartbarView?.isBusinessFeatureVisible = false
        smartbarView?.updateSmartbarState()
    }

    override fun onSubtypeChanged(newSubtype: Subtype) {
        launch {
            if (activeEditorInstance.isComposingEnabled) {
                withContext(Dispatchers.IO) {
                    dictionaryManager.loadDictionary(AssetRef(AssetSource.Assets, "ime/dict/en.flict")).let {
                        activeDictionary = it.getOrDefault(null)
                    }
                }
            }
            val keyboardView = keyboardViews[KeyboardMode.CHARACTERS]
            keyboardView?.computedLayout = layoutManager.fetchComputedLayoutAsync(KeyboardMode.CHARACTERS, newSubtype, florisboard.prefs).await()
            keyboardView?.updateVisibility()
        }
    }

    /**
     * Main logic point for processing cursor updates as well as parsing the current composing word
     * and passing this info on to the [SmartbarView] to turn it into candidate suggestions.
     */
    override fun onUpdateSelection() {
        if (!inputEventDispatcher.isPressed(KeyCode.SHIFT)) {
            updateCapsState()
        }
        smartbarView?.updateSmartbarState()
        if (BuildConfig.DEBUG) {
            Timber.i("current word: ${activeEditorInstance.cachedInput.currentWord.text}")
        }
        if (activeEditorInstance.isComposingEnabled && !inputEventDispatcher.isPressed(KeyCode.DELETE)) {
            if (activeEditorInstance.shouldReevaluateComposingSuggestions) {
                activeEditorInstance.shouldReevaluateComposingSuggestions = false
                activeDictionary?.let {
                    launch(Dispatchers.Default) {
                        val startTime = System.nanoTime()
                        val suggestions = it.getTokenPredictions(
                                precedingTokens = listOf(),
                                currentToken = Token(activeEditorInstance.cachedInput.currentWord.text),
                                maxSuggestionCount = 3,
                                allowPossiblyOffensive = !florisboard.prefs.suggestion.blockPossiblyOffensive
                        ).toStringList()
                        if (BuildConfig.DEBUG) {
                            val elapsed = (System.nanoTime() - startTime) / 1000.0
                            Timber.i("sugg fetch time: $elapsed us")
                        }
                        withContext(Dispatchers.Main) {
                            smartbarView?.setCandidateSuggestionWords(startTime, suggestions)
                            smartbarView?.updateCandidateSuggestionCapsState()
                        }
                    }
                }
            } else {
                smartbarView?.setCandidateSuggestionWords(System.nanoTime(), listOf())
            }
        }
    }

    override fun onPrimaryClipChanged() {
        smartbarView?.onPrimaryClipChanged()
    }

    /**
     * Updates the current caps state according to the [EditorInstance.cursorCapsMode], while
     * respecting [capsLock] property and the correction.autoCapitalization preference.
     */
    private fun updateCapsState() {
        if (!capsLock) {
            caps = florisboard.prefs.correction.autoCapitalization &&
                    activeEditorInstance.cursorCapsMode != InputAttributes.CapsMode.NONE
            keyboardViews[activeKeyboardMode]?.invalidateAllKeys()
        }
    }

    /**
     * Executes a given [SwipeAction]. Ignores any [SwipeAction] but the ones relevant for this
     * class.
     */
    fun executeSwipeAction(swipeAction: SwipeAction) {
        val keyData = when (swipeAction) {
            SwipeAction.DELETE_WORD -> KeyData.DELETE_WORD
            SwipeAction.INSERT_SPACE -> KeyData.SPACE
            SwipeAction.MOVE_CURSOR_DOWN -> KeyData.ARROW_DOWN
            SwipeAction.MOVE_CURSOR_UP -> KeyData.ARROW_UP
            SwipeAction.MOVE_CURSOR_LEFT -> KeyData.ARROW_LEFT
            SwipeAction.MOVE_CURSOR_RIGHT -> KeyData.ARROW_RIGHT
            SwipeAction.MOVE_CURSOR_START_OF_LINE -> KeyData.MOVE_START_OF_LINE
            SwipeAction.MOVE_CURSOR_END_OF_LINE -> KeyData.MOVE_END_OF_LINE
            SwipeAction.MOVE_CURSOR_START_OF_PAGE -> KeyData.MOVE_START_OF_PAGE
            SwipeAction.MOVE_CURSOR_END_OF_PAGE -> KeyData.MOVE_END_OF_PAGE
            SwipeAction.SHIFT -> KeyData.SHIFT
            SwipeAction.SHOW_INPUT_METHOD_PICKER -> KeyData.SHOW_INPUT_METHOD_PICKER
            else -> null
        }
        if (keyData != null) {
            inputEventDispatcher.send(InputKeyEvent.downUp(keyData))
        }
    }

    override fun onSmartbarBackButtonPressed() {
        setActiveKeyboardMode(KeyboardMode.CHARACTERS)
    }

    override fun onSmartbarCandidatePressed(word: String) {
        activeEditorInstance.commitCompletion(word)
    }

    override fun onSmartbarPrivateModeButtonClicked() {
        Toast.makeText(florisboard.context, R.string.private_mode_dialog__title, Toast.LENGTH_LONG).show()
    }

    override fun onSmartbarQuickActionPressed(quickActionId: Int) {
        when (quickActionId) {
            R.id.quick_action_switch_to_editing_context -> {
                if (activeKeyboardMode == KeyboardMode.EDITING) {
                    setActiveKeyboardMode(KeyboardMode.CHARACTERS)
                } else {
                    setActiveKeyboardMode(KeyboardMode.EDITING)
                }
            }
            R.id.quick_action_switch_to_media_context -> florisboard.setActiveInput(R.id.media_input)
            R.id.quick_action_open_settings -> florisboard.launchSettings()
            R.id.quick_action_one_handed_toggle -> florisboard.toggleOneHandedMode(isRight = true)
            R.id.quick_action_undo -> {
                activeEditorInstance.performUndo()
                return
            }
            R.id.quick_action_redo -> {
                activeEditorInstance.performRedo()
                return
            }
            R.id.business_feature_toggle_action -> {
                showFeatureUI = !showFeatureUI
                if (!showFeatureUI) {
                    setActiveKeyboardMode(KeyboardMode.CHARACTERS)
                }
            }
        }
        smartbarView?.isQuickActionsVisible = false
        smartbarView?.updateSmartbarState()
    }

    /**
     * Handles a [KeyCode.DELETE] event.
     */
    private fun handleDelete() {
        isManualSelectionMode = false
        isManualSelectionModeStart = false
        isManualSelectionModeEnd = false
        activeEditorInstance.deleteBackwards()
    }

    /**
     * Handles a [KeyCode.DELETE_WORD] event.
     */
    private fun handleDeleteWord() {
        isManualSelectionMode = false
        isManualSelectionModeStart = false
        isManualSelectionModeEnd = false
        activeEditorInstance.deleteWordsBeforeCursor(1)
    }

    /**
     * Handles a [KeyCode.ENTER] event.
     */
    private fun handleEnter() {
        if (activeEditorInstance.imeOptions.flagNoEnterAction) {
            activeEditorInstance.performEnter()
        } else {
            when (activeEditorInstance.imeOptions.action) {
                ImeOptions.Action.DONE,
                ImeOptions.Action.GO,
                ImeOptions.Action.NEXT,
                ImeOptions.Action.PREVIOUS,
                ImeOptions.Action.SEARCH,
                ImeOptions.Action.SEND -> {
                    activeEditorInstance.performEnterAction(activeEditorInstance.imeOptions.action)
                }
                else -> activeEditorInstance.performEnter()
            }
        }
    }

    /**
     * Handles a [KeyCode.LANGUAGE_SWITCH] event. Also handles if the language switch should cycle
     * FlorisBoard internal or system-wide.
     */
    private fun handleLanguageSwitch() {
        when (florisboard.prefs.keyboard.utilityKeyAction) {
            UtilityKeyAction.DYNAMIC_SWITCH_LANGUAGE_EMOJIS,
            UtilityKeyAction.SWITCH_LANGUAGE -> florisboard.switchToNextSubtype()
            else -> florisboard.switchToNextKeyboard()
        }
    }

    /**
     * Handles a [KeyCode.SHIFT] down event.
     */
    private fun handleShiftDown(ev: InputKeyEvent) {
        if (ev.isConsecutiveEventOf(inputEventDispatcher.lastKeyEventDown, florisboard.prefs.keyboard.longPressDelay.toLong())) {
            newCapsState = true
            caps = true
            capsLock = true
        } else {
            newCapsState = !caps
            caps = true
            capsLock = false
        }
        keyboardViews[activeKeyboardMode]?.invalidateAllKeys()
        smartbarView?.updateCandidateSuggestionCapsState()
    }

    /**
     * Handles a [KeyCode.SHIFT] up event.
     */
    private fun handleShiftUp() {
        caps = newCapsState
        keyboardViews[activeKeyboardMode]?.invalidateAllKeys()
        smartbarView?.updateCandidateSuggestionCapsState()
    }

    /**
     * Handles a [KeyCode.SHIFT] cancel event.
     */
    private fun handleShiftCancel() {
        caps = false
        capsLock = false
        keyboardViews[activeKeyboardMode]?.invalidateAllKeys()
        smartbarView?.updateCandidateSuggestionCapsState()
    }

    /**
     * Handles a [KeyCode.SHIFT] up event.
     */
    private fun handleShiftLock() {
        val lastKeyEvent = inputEventDispatcher.lastKeyEventDown ?: return
        if (lastKeyEvent.data.code == KeyCode.SHIFT && lastKeyEvent.action == InputKeyEvent.Action.DOWN) {
            newCapsState = true
            caps = true
            capsLock = true
            keyboardViews[activeKeyboardMode]?.invalidateAllKeys()
            smartbarView?.updateCandidateSuggestionCapsState()
        }
    }

    /**
     * Handles a [KeyCode.SPACE] event. Also handles the auto-correction of two space taps if
     * enabled by the user.
     */
    private fun handleSpace(ev: InputKeyEvent) {
        if (florisboard.prefs.correction.doubleSpacePeriod) {
            if (ev.isConsecutiveEventOf(inputEventDispatcher.lastKeyEventUp, florisboard.prefs.keyboard.longPressDelay.toLong())) {
                val text = activeEditorInstance.getTextBeforeCursor(2)
                if (text.length == 2 && !text.matches("""[.!?‽\s][\s]""".toRegex())) {
                    activeEditorInstance.deleteBackwards()
                    activeEditorInstance.commitText(".")
                }
            }
        }
        activeEditorInstance.commitText(KeyCode.SPACE.toChar().toString())
    }

    /**
     * Handles [KeyCode] arrow and move events, behaves differently depending on text selection.
     */
    private fun handleArrow(code: Int, count: Int) = activeEditorInstance.apply {
        val isShiftPressed = isManualSelectionMode || inputEventDispatcher.isPressed(KeyCode.SHIFT)
        when (code) {
            KeyCode.ARROW_DOWN -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = false
                    isManualSelectionModeEnd = true
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, meta(shift = isShiftPressed), count)
            }
            KeyCode.ARROW_LEFT -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = true
                    isManualSelectionModeEnd = false
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, meta(shift = isShiftPressed), count)
            }
            KeyCode.ARROW_RIGHT -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = false
                    isManualSelectionModeEnd = true
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, meta(shift = isShiftPressed), count)
            }
            KeyCode.ARROW_UP -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = true
                    isManualSelectionModeEnd = false
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_UP, meta(shift = isShiftPressed), count)
            }
            KeyCode.MOVE_START_OF_PAGE -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = true
                    isManualSelectionModeEnd = false
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_UP, meta(alt = true, shift = isShiftPressed), count)
            }
            KeyCode.MOVE_END_OF_PAGE -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = false
                    isManualSelectionModeEnd = true
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, meta(alt = true, shift = isShiftPressed), count)
            }
            KeyCode.MOVE_START_OF_LINE -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = true
                    isManualSelectionModeEnd = false
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, meta(alt = true, shift = isShiftPressed), count)
            }
            KeyCode.MOVE_END_OF_LINE -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = false
                    isManualSelectionModeEnd = true
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, meta(alt = true, shift = isShiftPressed), count)
            }
        }
    }

    /**
     * Handles a [KeyCode.CLIPBOARD_SELECT] event.
     */
    private fun handleClipboardSelect() = activeEditorInstance.apply {
        if (selection.isSelectionMode) {
            if (isManualSelectionMode && isManualSelectionModeStart) {
                selection.updateAndNotify(selection.start, selection.start)
            } else {
                selection.updateAndNotify(selection.end, selection.end)
            }
            isManualSelectionMode = false
        } else {
            isManualSelectionMode = !isManualSelectionMode
            // Must call to update UI properly
            editingKeyboardView?.onUpdateSelection()
        }
    }

    /**
     * Adjusts a given key data for caps state and returns the correct reference.
     */
    private fun getAdjustedKeyData(keyData: KeyData): KeyData {
        return if (caps && keyData is FlorisKeyData && keyData.shift != null) {
            keyData.shift!!
        } else {
            keyData
        }
    }

    override fun onInputKeyDown(ev: InputKeyEvent) {
        val data = getAdjustedKeyData(ev.data)
        when (data.code) {
            KeyCode.INTERNAL_BATCH_EDIT -> {
                florisboard.beginInternalBatchEdit()
                return
            }
            KeyCode.SHIFT -> {
                handleShiftDown(ev)
            }
        }
    }

    override fun onInputKeyUp(ev: InputKeyEvent) {
        val data = getAdjustedKeyData(ev.data)
        when (data.code) {
            KeyCode.ARROW_DOWN,
            KeyCode.ARROW_LEFT,
            KeyCode.ARROW_RIGHT,
            KeyCode.ARROW_UP,
            KeyCode.MOVE_START_OF_PAGE,
            KeyCode.MOVE_END_OF_PAGE,
            KeyCode.MOVE_START_OF_LINE,
            KeyCode.MOVE_END_OF_LINE -> if (ev.action == InputKeyEvent.Action.DOWN_UP || ev.action == InputKeyEvent.Action.REPEAT) {
                handleArrow(data.code, ev.count)
            } else {
                handleArrow(data.code, 1)
            }
            KeyCode.CLIPBOARD_CUT -> activeEditorInstance.performClipboardCut()
            KeyCode.CLIPBOARD_COPY -> activeEditorInstance.performClipboardCopy()
            KeyCode.CLIPBOARD_PASTE -> {
                activeEditorInstance.performClipboardPaste()
                smartbarView?.resetClipboardSuggestion()
            }
            KeyCode.CLIPBOARD_SELECT -> handleClipboardSelect()
            KeyCode.CLIPBOARD_SELECT_ALL -> activeEditorInstance.performClipboardSelectAll()
            KeyCode.DELETE -> {
                handleDelete()
                if (ev.action == InputKeyEvent.Action.DOWN_UP || ev.action == InputKeyEvent.Action.UP) {
                    smartbarView?.resetClipboardSuggestion()
                }
            }
            KeyCode.DELETE_WORD -> {
                handleDeleteWord()
                if (ev.action == InputKeyEvent.Action.DOWN_UP || ev.action == InputKeyEvent.Action.UP) {
                    smartbarView?.resetClipboardSuggestion()
                }
            }
            KeyCode.ENTER -> {
                handleEnter()
                smartbarView?.resetClipboardSuggestion()
            }
            KeyCode.INTERNAL_BATCH_EDIT -> {
                florisboard.endInternalBatchEdit()
                return
            }
            KeyCode.LANGUAGE_SWITCH -> handleLanguageSwitch()
            KeyCode.SETTINGS -> florisboard.launchSettings()
            KeyCode.SHIFT -> handleShiftUp()
            KeyCode.SHIFT_LOCK -> handleShiftLock()
            KeyCode.SHOW_INPUT_METHOD_PICKER -> florisboard.imeManager?.showInputMethodPicker()
            KeyCode.SWITCH_TO_MEDIA_CONTEXT -> florisboard.setActiveInput(R.id.media_input)
            KeyCode.SWITCH_TO_TEXT_CONTEXT -> florisboard.setActiveInput(R.id.text_input)
            KeyCode.TOGGLE_ONE_HANDED_MODE_LEFT -> florisboard.toggleOneHandedMode(isRight = false)
            KeyCode.TOGGLE_ONE_HANDED_MODE_RIGHT -> florisboard.toggleOneHandedMode(isRight = true)
            KeyCode.VIEW_CHARACTERS -> setActiveKeyboardMode(KeyboardMode.CHARACTERS)
            KeyCode.VIEW_NUMERIC -> setActiveKeyboardMode(KeyboardMode.NUMERIC)
            KeyCode.VIEW_NUMERIC_ADVANCED -> setActiveKeyboardMode(KeyboardMode.NUMERIC_ADVANCED)
            KeyCode.VIEW_PHONE -> setActiveKeyboardMode(KeyboardMode.PHONE)
            KeyCode.VIEW_PHONE2 -> setActiveKeyboardMode(KeyboardMode.PHONE2)
            KeyCode.VIEW_SYMBOLS -> setActiveKeyboardMode(KeyboardMode.SYMBOLS)
            KeyCode.VIEW_SYMBOLS2 -> setActiveKeyboardMode(KeyboardMode.SYMBOLS2)
            else -> {
                when (activeKeyboardMode) {
                    KeyboardMode.NUMERIC,
                    KeyboardMode.NUMERIC_ADVANCED,
                    KeyboardMode.PHONE,
                    KeyboardMode.PHONE2 -> when (data.type) {
                        KeyType.CHARACTER,
                        KeyType.NUMERIC -> {
                            val text = data.code.toChar().toString()
                            activeEditorInstance.commitText(text)
                        }
                        else -> when (data.code) {
                            KeyCode.PHONE_PAUSE,
                            KeyCode.PHONE_WAIT -> {
                                val text = data.code.toChar().toString()
                                activeEditorInstance.commitText(text)
                            }
                        }
                    }
                    else -> when (data.type) {
                        KeyType.CHARACTER, KeyType.NUMERIC -> when (data.code) {
                            KeyCode.SPACE -> handleSpace(ev)
                            KeyCode.URI_COMPONENT_TLD -> {
                                val tld = data.label.toLowerCase(Locale.ENGLISH)
                                activeEditorInstance.commitText(tld)
                            }
                            else -> {
                                var text = data.code.toChar().toString()
                                text = when (caps && activeKeyboardMode == KeyboardMode.CHARACTERS) {
                                    true -> text.toUpperCase(florisboard.activeSubtype.locale)
                                    false -> text
                                }
                                activeEditorInstance.commitText(text)
                            }
                        }
                        else -> {
                            Timber.e("sendKeyPress(keyData): Received unknown key: $data")
                        }
                    }
                }
                smartbarView?.resetClipboardSuggestion()
            }
        }
        if (data.code != KeyCode.SHIFT && !capsLock && !inputEventDispatcher.isPressed(KeyCode.SHIFT)) {
            updateCapsState()
        }
        smartbarView?.updateSmartbarState()
    }

    override fun onInputKeyRepeat(ev: InputKeyEvent) {
        onInputKeyUp(ev)
    }

    override fun onInputKeyCancel(ev: InputKeyEvent) {
        val data = getAdjustedKeyData(ev.data)
        when (data.code) {
            KeyCode.SHIFT -> handleShiftCancel()
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        currentTab = tab
        if (showFeatureUI) {
            setActiveKeyboardMode(KeyboardMode.BUSINESS_FEATURES)
        } else {
            setActiveKeyboardMode(KeyboardMode.CHARACTERS)
        }
        tab?.position?.let { BusinessFeatureEnum.values()[it] }?.let { showSelectedBusinessFeature(it) }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    private fun showSelectedBusinessFeature(businessFeatureEnum: BusinessFeatureEnum) {
        when (businessFeatureEnum) {
            BusinessFeatureEnum.UPDATES -> {
                binding.clSelectionLayout.gone()
                binding.productShareRvList.visible()
                binding.rvListPhotos.gone()
            }
            BusinessFeatureEnum.INVENTORY -> {
                binding.clSelectionLayout.gone()
                binding.productShareRvList.visible()
                binding.rvListPhotos.gone()
            }
            BusinessFeatureEnum.PHOTOS -> {
                binding.clSelectionLayout.visible()
                binding.productShareRvList.gone()
                binding.rvListPhotos.visible()
            }
            BusinessFeatureEnum.DETAILS -> {
                binding.clSelectionLayout.gone()
                binding.productShareRvList.visible()
                binding.rvListPhotos.gone()
            }
        }

        initializeAdapters(businessFeatureEnum)
    }

    private fun initializeAdapters(businessFeatureEnum: BusinessFeatureEnum) {
        if (!SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).isLoggedIn) {
            Timber.i("Please do login")
            // show login UI
            binding.pleaseLoginCard.visible()
            binding.pleaseLoginCard.setOnClickListener {
                MethodUtils.startBoostActivity(mContext)
            }
        } else {
            binding.pleaseLoginCard.gone()
            if (MethodUtils.isOnline(mContext)) {
                businessFeatureProgressBar.visible()
                when (businessFeatureEnum) {
                    BusinessFeatureEnum.UPDATES -> {
                        viewModel.getUpdates(
                                SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId,
                                mContext.getString(R.string.client_id),
                                0, 10
                        )
                        viewModel.updates.observeForever {
                            Timber.e("updates - $it.")
                            businessFeatureProgressBar.gone()
                            adapter.submitList(it.floats)
                        }
                    }
                    BusinessFeatureEnum.INVENTORY -> {
                        viewModel.getProducts(
                                SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag,
                                mContext.getString(R.string.client_id),
                                0, "SINGLE"
                        )
                        viewModel.products.observeForever {
                            Timber.e("products - $it.")
                            businessFeatureProgressBar.gone()
                            adapter.submitList(it)
                        }
                    }
                    BusinessFeatureEnum.DETAILS -> {
                        viewModel.getDetails(
                                SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag,
                                mContext.getString(R.string.client_id)
                        )
                        viewModel.details.observeForever {
                            Timber.e("details - $it.")
                            businessFeatureProgressBar.gone()
                            adapter.submitList(listOf(it))
                        }
                    }
                    BusinessFeatureEnum.PHOTOS -> {
                        viewModel.getPhotos(
                                SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId
                        )
                        viewModel.photos.observeForever {
                            Timber.e("photos - $it.")
                            businessFeatureProgressBar.gone()
                            adapter.submitList(it)
                        }
                    }
                }
            } else {
                Toast.makeText(mContext, "Check your Network Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRefresh() {
        swipeRefresh.isRefreshing = false
        Toast.makeText(mContext, "Functionality to be implemented", Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(pos: Int, item: BaseRecyclerItem) {
        currentTab?.position?.let { BusinessFeatureEnum.values()[it] }?.let { handleListItemClick(it, pos, item) }
    }

    private fun handleListItemClick(businessFeatureEnum: BusinessFeatureEnum, pos: Int, item: BaseRecyclerItem) {
        when (businessFeatureEnum) {
            BusinessFeatureEnum.UPDATES -> {
                Timber.i("pos - $pos item = $item")
            }
            BusinessFeatureEnum.INVENTORY -> {
                Timber.i("pos - $pos item = $item")
            }
            BusinessFeatureEnum.PHOTOS -> {
                Timber.i("pos - $pos item = $item")
            }
            BusinessFeatureEnum.DETAILS -> {
                Timber.i("pos - $pos item = $item")
            }
        }
    }
}
