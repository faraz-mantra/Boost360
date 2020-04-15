package com.framework.views.flipview

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.ArcShape
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.annotation.*
import androidx.annotation.IntRange
import com.framework.R


class FlipView : ViewFlipper, SVGPictureDrawable, View.OnClickListener {
    /**
     * Custom Listener
     */
    interface OnFlippingListener {
        fun onFlipped(flipView: FlipView?, checked: Boolean)
    }

    /**
     * View listener
     */
    private var mFlippingListener = EMPTY_LISTENER

    /**
     * Gets the front TextView if present in the layout.
     *
     * @return the front TextView, null if not added in the layout
     * @see .setFrontText
     */
    /**
     * Reference to the TextView of the FrontLayout if exists
     */
    var frontTextView: TextView? = null
        private set

    /**
     * Gets the current front ImageView for the *unchecked* status.
     *
     * @return the current front ImageView
     * @see .setFrontImage
     * @see .setFrontImageBitmap
     */
    /**
     * Reference to the ImageView of the FrontLayout if exists
     */
    var frontImageView: ImageView? = null
        private set
    private var frontImagePadding = 0

    /**
     * Gets the current rear ImageView for the *checked* status.
     *
     * @return the current rear ImageView
     * @see .setRearImage
     * @see .setRearImageBitmap
     */
    /**
     * Reference to the ImageView of the RearLayout if exists
     */
    var rearImageView: ImageView? = null
        private set
    private var rearImagePadding = 0

    //***********************************
    // PICTURE DRAWABLE IMPLEMENTATION **
    //***********************************
    /**
     * [.LAYER_TYPE_SOFTWARE] is automatically set for you, only on the ImageView reference.
     *
     * @param drawable The SVG Drawable
     */
    /**
     * Drawable used for SVG images for the front View Only
     */
    override var pictureDrawable: PictureDrawable? = null
        set(drawable) {
            field = drawable
            if (frontImageView == null) {
                Log.w(TAG, "ImageView not found in the first child of the FrontLayout. Image cannot be set!")
                return
            }
            frontImageView!!.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            frontImageView!!.setImageDrawable(pictureDrawable)
        }

    /**
     * @return the Animation of this FlipView layout
     */
    var initialLayoutAnimation: Animation? = null
        private set

    /**
     * @return the animation of the rear ImageView.
     */
    var rearImageAnimation: Animation? = null
        private set
    private var initialLayoutAnimationDuration: Long = 0
    private var mainAnimationDuration: Long = 0
    private var rearImageAnimationDuration: Long = 0
    private var rearImageAnimationDelay: Long = 0
    private var anticipateInAnimationTime: Long = 0
    private var mFlipInterval = DEFAULT_INTERVAL

    //****************
    // CONSTRUCTORS **
    //****************
    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    /**
     * Already part of the extended views:
     *
     *  * `inAnimation`: from `ViewAnimator`, identifier for the animation to use when a view is shown.
     *  * `outAnimation`: from `ViewAnimator`, identifier for the animation to use when a view is hidden.
     *  * `animateFirstView`: from `ViewAnimator`, defines whether to animate the current View when the ViewAnimation is first displayed.
     *  * `flipInterval`: from `ViewFlipper`, time before next animation.
     *  * `autoStart`: from `ViewFlipper`, when true, automatically start animating.
     *
     *
     * @param attrs The view's attributes.
     */
    private fun init(attrs: AttributeSet?) {
        // Read and apply provided attributes
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.FlipView, 0, 0)

        // Flags
        val checked = a.getBoolean(R.styleable.FlipView_checked, false)
        val startupAnimation = a.getBoolean(R.styleable.FlipView_enableInitialAnimation, false)
        val animateDesignChildViewsOnly = a.getBoolean(R.styleable.FlipView_animateDesignLayoutOnly, false)
        if (!animateDesignChildViewsOnly) {
            // FrontView
            val frontLayout = a.getResourceId(R.styleable.FlipView_frontLayout, R.layout.flipview_front)
            val frontBackground = a.getDrawable(R.styleable.FlipView_frontBackground)
            val frontBackgroundColor = a.getColor(R.styleable.FlipView_frontBackgroundColor, Color.GRAY)
            val frontImage = a.getResourceId(R.styleable.FlipView_frontImage, 0)
            frontImagePadding = a.getDimension(R.styleable.FlipView_frontImagePadding, 0f).toInt()
            setFrontLayout(frontLayout)
            if (frontBackground == null) setChildBackgroundColor(FRONT_VIEW_INDEX, frontBackgroundColor) else setChildBackgroundDrawable(FRONT_VIEW_INDEX, frontBackground)
            setFrontImage(frontImage)

            // RearView
            val rearLayout = a.getResourceId(R.styleable.FlipView_rearLayout, R.layout.flipview_rear)
            val rearBackground = a.getDrawable(R.styleable.FlipView_rearBackground)
            val rearBackgroundColor = a.getColor(R.styleable.FlipView_rearBackgroundColor, Color.GRAY)
            val rearImage = a.getResourceId(R.styleable.FlipView_rearImage, R.drawable.ic_action_done)
            rearImagePadding = a.getDimension(R.styleable.FlipView_rearImagePadding, 0f).toInt()
            addRearLayout(rearLayout)
            if (rearBackground == null) setChildBackgroundColor(REAR_VIEW_INDEX, rearBackgroundColor) else setChildBackgroundDrawable(REAR_VIEW_INDEX, rearBackground)
            setRearImage(rearImage)
        }

        // Display the first rear view at start if requested
        if (checked) flipSilently(true)

        // Init main(Flip) animations
        mainAnimationDuration = a.getInteger(R.styleable.FlipView_animationDuration, FLIP_DURATION).toLong()
        rearImageAnimationDuration = a.getInteger(R.styleable.FlipView_rearImageAnimationDuration, REAR_IMAGE_ANIMATION_DURATION).toLong()
        rearImageAnimationDelay = a.getInteger(R.styleable.FlipView_rearImageAnimationDelay, mainAnimationDuration.toInt()).toLong()
        anticipateInAnimationTime = a.getInteger(R.styleable.FlipView_anticipateInAnimationTime, 0).toLong()
        if (!isInEditMode) {
            //This also initialize the in/out animations
            setMainAnimationDuration(mainAnimationDuration)
            if (a.getBoolean(R.styleable.FlipView_animateRearImage, true)) setRearImageAnimation(a.getResourceId(R.styleable.FlipView_rearImageAnimation, 0))
        }

        // Save initial animation settings
        initialLayoutAnimationDuration = a.getInteger(R.styleable.FlipView_initialLayoutAnimationDuration, INITIAL_ANIMATION_DURATION).toLong()
        setInitialLayoutAnimation(a.getResourceId(R.styleable.FlipView_initialLayoutAnimation, 0))
        // Show initial cascade step animation when view is first rendered
        if (startupAnimation && enableInitialAnimation && !isInEditMode) {
            animateLayout(initialLayoutAnimation)
        }
        a.recycle()

        // Apply default OnClickListener if clickable
        if (isClickable) setOnClickListener(this)
    }

    //*************
    // LISTENERS **
    //*************
    fun setOnFlippingListener(listener: OnFlippingListener) {
        mFlippingListener = listener
    }

    override fun onClick(v: View) {
        this.showNext()
    }

    /**
     * {@inheritDoc}
     *
     * By setting `true` will set the click listener to this view, by setting
     * `false` will remove the click listener completely.
     *
     * @param clickable true to make the view clickable, false otherwise
     */
    override fun setClickable(clickable: Boolean) {
        super.setClickable(clickable)
        if (clickable) setOnClickListener(this) else setOnClickListener(null)
    }

    /**
     * {@inheritDoc}
     *
     * **Note:** If the view was set as auto-start <u>and</u> [.setFlipInterval]
     * has not been called, re-enabling the view, it will have the default initial 3000ms delay.
     *
     * @param enabled true if this view is enabled and flip active, false otherwise.
     */
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (isAutoStart) {
            if (!enabled) stopFlipping() else postDelayed({ if (isEnabled) startFlipping() }, mFlipInterval.toLong())
        }
    }

    //**************
    // ANIMATIONS **
    //**************
    override fun setFlipInterval(@IntRange(from = 0) milliseconds: Int) {
        super.setFlipInterval(milliseconds)
        mFlipInterval = milliseconds
    }

    /*
	 * Override to always display content in design mode.
	 */
    override fun setInAnimation(context: Context, @AnimRes animationResId: Int) {
        if (!isInEditMode) super.setInAnimation(context, animationResId)
    }

    /*
	 * Override to always display content in design mode.
	 */
    override fun setOutAnimation(context: Context, @AnimRes animationResId: Int) {
        if (!isInEditMode) super.setOutAnimation(context, animationResId)
    }

    private fun initInAnimation(@IntRange(from = 0) duration: Long) {
        if (inAnimation == null) this.setInAnimation(context, R.anim.grow_from_middle_x_axis)
        super.getInAnimation().duration = duration
        super.getInAnimation().startOffset = if (anticipateInAnimationTime > duration) duration else duration - anticipateInAnimationTime
    }

    private fun initOutAnimation(@IntRange(from = 0) duration: Long) {
        if (outAnimation == null) this.setOutAnimation(context, R.anim.shrink_to_middle_x_axis)
        super.getOutAnimation().duration = duration
    }

    fun animateLayout(layoutAnimation: Animation?) {
        startAnimation(layoutAnimation)
    }

    @SuppressLint("ResourceType")
    fun setInitialLayoutAnimation(@AnimRes animationResId: Int) {
        try {
            setInitialLayoutAnimation(if (animationResId > 0) AnimationUtils.loadAnimation(context, animationResId) else createScaleAnimation()) //Usage of the method it's faster (not read from disk)
            if (DEBUG) Log.d(TAG, "Initial animation is active!")
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Initial animation with id " + animationResId
                    + " could not be found. Initial animation cannot be set!")
        }
    }

    /**
     * Custom initial layout animation.<br></br>
     *
     * **Note:** Duration and startOffset will be overridden by the following settings:
     * `duration = initialLayoutAnimationDuration;`<br></br>
     * `startOffset = initialDelay += SCALE_STEP_DELAY`.
     *
     * @param initialLayoutAnimation the new initial animation
     */
    fun setInitialLayoutAnimation(initialLayoutAnimation: Animation) {
        this.initialLayoutAnimation = initialLayoutAnimation
        initialLayoutAnimation.duration = initialLayoutAnimationDuration
        initialLayoutAnimation.startOffset = SCALE_STEP_DELAY.let { initialDelay += it; initialDelay }
        if (initialLayoutAnimation.interpolator == null) initialLayoutAnimation.interpolator = DecelerateInterpolator()
    }

    @SuppressLint("ResourceType")
    fun setRearImageAnimation(@AnimRes animationResId: Int) {
        try {
            setRearImageAnimation(AnimationUtils.loadAnimation(context,
                    if (animationResId > 0) animationResId else R.anim.scale_up))
            if (DEBUG) Log.d(TAG, "Rear animation is active!")
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Rear animation with id $animationResId could not be found. Rear animation cannot be set!")
        }
    }

    fun setRearImageAnimation(rearAnimation: Animation?) {
        rearImageAnimation = rearAnimation
        if (rearImageAnimationDuration > 0) rearImageAnimation!!.duration = rearImageAnimationDuration
    }

    /**
     * Gets the duration of the flip animation.
     *
     * @return the duration in milliseconds
     */
    fun getMainAnimationDuration(): Long {
        return inAnimation.duration
    }

    /**
     * Sets the duration of the main animation.
     *
     * @param duration The duration in milliseconds
     */
    fun setMainAnimationDuration(@IntRange(from = 0) duration: Long) {
        if (DEBUG) Log.d(TAG, "Setting mainAnimationDuration=$duration")
        mainAnimationDuration = duration
        initInAnimation(duration)
        initOutAnimation(duration)
    }

    /**
     * Gets the duration of the initial animation when the View is first displayed.
     *
     * @return the duration in milliseconds
     */
    fun getInitialLayoutAnimationDuration(): Long {
        return initialLayoutAnimationDuration
    }

    /**
     * Sets the duration of the initial animation when the View is first displayed.
     *
     * @param duration The duration in milliseconds
     */
    fun setInitialLayoutAnimationDuration(@IntRange(from = 0) duration: Long) {
        if (DEBUG) Log.d(TAG, "Setting initialLayoutAnimationDuration=$duration")
        initialLayoutAnimationDuration = duration
        if (initialLayoutAnimation != null) initialLayoutAnimation!!.duration = duration
    }

    /**
     * Gets the duration of the animation of the rear ImageView.
     *
     * @return the duration in milliseconds
     */
    fun getRearImageAnimationDuration(): Long {
        return rearImageAnimationDuration
    }

    /**
     * Sets the duration of the animation of the rear ImageView.
     *
     * @param duration The duration in milliseconds
     */
    fun setRearImageAnimationDuration(@IntRange(from = 0) duration: Long) {
        if (DEBUG) Log.d(TAG, "Setting rearImageAnimationDuration=$duration")
        rearImageAnimationDuration = duration
        if (rearImageAnimation != null) rearImageAnimation!!.duration = duration
    }

    /**
     * Get the anticipation time for InAnimation.
     *
     * @return the anticipation time in milliseconds
     */
    fun getAnticipateInAnimationTime(): Long {
        return anticipateInAnimationTime
    }

    /**
     * Sets the anticipation time for InAnimation: don't wait OutAnimation completion.
     * Depends by the effect that user desires, he can anticipate the entrance of rear layout.
     *
     *
     * - New delay is: the current [.mainAnimationDuration] anticipation time.<br></br>
     * - Max value is: the current mainAnimationDuration.
     *
     * Default value is 0.
     *
     * @param time the anticipation time in milliseconds
     */
    fun setAnticipateInAnimationTime(@IntRange(from = 0) time: Long) {
        if (DEBUG) Log.d(TAG, "Setting anticipateInAnimationTime=$time")
        anticipateInAnimationTime = time
    }

    /**
     * Gets the start animation delay of the rear ImageView.
     *
     * @return the delay in milliseconds
     */
    fun getRearImageAnimationDelay(): Long {
        return rearImageAnimationDelay
    }

    /**
     * Sets the start animation delay of the rear ImageView.
     *
     * @param delay the delay in milliseconds
     */
    fun setRearImageAnimationDelay(@IntRange(from = 0) delay: Long) {
        if (DEBUG) Log.d(TAG, "Setting rearImageAnimationDelay=$delay")
        rearImageAnimationDelay = delay
    }
    //************************
    // PERFORMING ANIMATION **
    //************************
    /**
     * Flips the current View and display to the next View now!
     *
     * Command ignored if the view is disabled.
     *
     * @see .setEnabled
     */
    override fun showNext() {
        this.showNext(0L)
    }

    /**
     * Flips the current View and display to the next View with a delay.
     *
     * Command ignored if the view is disabled.
     *
     * @param delay any custom delay
     * @see .setEnabled
     */
    fun showNext(delay: Long) {
        if (DEBUG) Log.d(TAG, "showNext " + (displayedChild + 1) + " delay=" + delay)
        this.flip(displayedChild + 1, delay)
    }

    /**
     * Flips the current View and display to the previous View now!
     *
     * Command ignored if the view is disabled.
     *
     * @see .setEnabled
     */
    override fun showPrevious() {
        this.showPrevious(0L)
    }

    /**
     * Flips the current View and display to the previous View with a delay.
     *
     * Command ignored if the view is disabled.
     *
     * @param delay any custom delay
     * @see .setEnabled
     */
    fun showPrevious(delay: Long) {
        if (DEBUG) Log.d(TAG, "showPrevious " + (displayedChild - 1) + " delay=" + delay)
        this.flip(displayedChild - 1, delay)
    }

    val isFlipped: Boolean
        get() = displayedChild > FRONT_VIEW_INDEX
    /**
     * Convenience method for layout that has only 2 child Views!
     *
     * Execute the flip animation with a custom delay.
     * Command ignored if the view is disabled.
     *
     * @param showRear `true` to show back View, `false` to show front View
     * @param delay    any custom delay
     * @see .setEnabled
     */
    /**
     * Convenience method for layout that has only 2 child Views!
     *
     * Execute the flip animation with No delay.
     * Command ignored if the view is disabled.
     *
     * @param showRear `true` to show back View, `false` to show front View
     * @see .setEnabled
     */
    @JvmOverloads
    fun flip(showRear: Boolean, delay: Long = 0L) {
        flip(if (showRear) REAR_VIEW_INDEX else FRONT_VIEW_INDEX, delay)
    }

    /**
     * Sets the state of this component to the given value, performing the
     * corresponding main animation and, if it exists, the rear Image animation.
     *
     * Command ignored if the view is disabled.
     *
     * @param whichChild the progressive index of the child View (first View has index=0).
     * @param delay      any custom delay
     * @see .setEnabled
     */
    fun flip(whichChild: Int, @IntRange(from = 0) delay: Long) {
        if (!isEnabled) {
            if (DEBUG) Log.w(TAG, "Can't flip while view is disabled")
            return
        }
        val childIndex = checkIndex(whichChild)
        if (DEBUG) {
            Log.d(TAG, "Flip! whichChild=$childIndex, previousChild=$displayedChild, delay=$delay")
        }
        // Issue #7 - Don't flip if the target child is the one currently displayed
        if (childIndex == displayedChild) {
            if (DEBUG) Log.w(TAG, "Already flipped to same whichChild=$whichChild")
            return
        }
        Handler().postDelayed({
            displayedChild = childIndex //Start main animation
            animateRearImageIfNeeded()
            mFlippingListener.onFlipped(this@FlipView, isFlipped)
        }, delay)
    }

    private fun animateRearImageIfNeeded() {
        if (isFlipped && rearImageView != null && rearImageAnimation != null) {
            rearImageView!!.alpha = 0f //Alpha 0 and Handler are needed to avoid the glitch of the rear image
            Handler().postDelayed({
                rearImageView!!.alpha = 1f
                rearImageView!!.startAnimation(rearImageAnimation)
            }, rearImageAnimationDelay) //Wait InAnimation completion before to start rearImageAnimation?
        }
    }

    /**
     * Convenience method for layout that has only 2 child Views, no animation will be performed.
     *
     * Command is always performed even if the view is disabled.
     *
     * @param showRear `true` to show back View, `false` to show front View
     * @see .flipSilently
     */
    fun flipSilently(showRear: Boolean) {
        flipSilently(if (showRear) REAR_VIEW_INDEX else FRONT_VIEW_INDEX)
    }

    /**
     * Shows a specific View immediately, no animation will be performed.
     *
     * Command is always performed even if the view is disabled.
     *
     * @param whichChild the index of the child view to display (first View has `index=0`).
     */
    fun flipSilently(whichChild: Int) {
        var whichChild = whichChild
        if (DEBUG) Log.d(TAG, "flipSilently whichChild=$whichChild")
        whichChild = checkIndex(whichChild)
        val inAnimation = super.getInAnimation()
        val outAnimation = super.getOutAnimation()
        super.setInAnimation(null)
        super.setOutAnimation(null)
        super.setDisplayedChild(whichChild)
        super.setInAnimation(inAnimation)
        super.setOutAnimation(outAnimation)
    }

    /**
     * Checks that, the index is never negative or bigger than the actual number of child Views.
     *
     *
     * - if negative: first child View is displayed;<br></br>
     * - if bigger than actual Views: last child View is displayed.
     *
     * The logic is different than [.setDisplayedChild], where:<br></br>
     * - if negative: last child View is displayed;<br></br>
     * - if bigger than actual Views: first child View is displayed.
     *
     * @param whichChild the index of the child View to display
     * @return the new index of the child View to display
     */
    private fun checkIndex(whichChild: Int): Int {
        if (whichChild < FRONT_VIEW_INDEX) return FRONT_VIEW_INDEX
        return if (whichChild > childCount) childCount else whichChild
    }
    //********************
    // LAYOUT AND VIEWS **
    //********************
    /**
     * Gets the View being displayed on the *front*. The front view is
     * displayed when the component is in state "not checked".
     *
     * @return the front view
     * @see .setFrontLayout
     * @see .setFrontLayout
     */
    val frontLayout: View
        get() = getChildAt(FRONT_VIEW_INDEX)

    /**
     * Sets the front view to be displayed when this component is in state *not checked*.
     *
     * The front view can be a ViewGroup.
     *
     * @param layoutResId the layout resource identifier.
     * @see .getFrontLayout
     * @see .setFrontLayout
     */
    fun setFrontLayout(@LayoutRes layoutResId: Int) {
        if (layoutResId == R.layout.flipview_front) {
            if (DEBUG) Log.d(TAG, "Adding inner FrontLayout")
        } else if (DEBUG) Log.d(TAG, "Setting user FrontLayout $layoutResId")
        setFrontLayout(LayoutInflater.from(context).inflate(layoutResId, this, false))
    }

    /**
     * Sets the front view to be displayed when this component is in state *not checked*.
     *
     * The provided view must be not `null`, or `IllegalArgumentException` will
     * be raised.
     * The front view can be a ViewGroup.
     *
     * @param view the front view. Must not be `null`
     * @see .getFrontLayout
     * @see .setFrontLayout
     */
    fun setFrontLayout(view: View) {
        var viewGroup: ViewGroup = this
        // If the View is another ViewGroup use it as front View to flip
        if (view is ViewGroup) {
            if (DEBUG) Log.d(TAG, "FrontLayout is a ViewGroup")
            viewGroup = view
        }
        // If any ImageView at first position is provided, reference to this front ImageView is saved.
        if (viewGroup.getChildAt(FRONT_VIEW_INDEX) is ImageView) {
            if (DEBUG) Log.d(TAG, "Found ImageView in FrontLayout")
            frontImageView = viewGroup.getChildAt(FRONT_VIEW_INDEX) as ImageView
        } else if (viewGroup.getChildAt(FRONT_VIEW_INDEX) is TextView) {
            if (DEBUG) Log.d(TAG, "Found TextView in FrontLayout")
            frontTextView = viewGroup.getChildAt(FRONT_VIEW_INDEX) as TextView
        }
        this.addView(view, FRONT_VIEW_INDEX)
    }

    /**
     * Gets the View being displayed on the *rear*. The rear view is
     * displayed when the component is in state "checked".
     *
     * @return the rear view
     * @see .addRearLayout
     * @see .addRearLayout
     */
    val rearLayout: View
        get() = getChildAt(REAR_VIEW_INDEX)

    /**
     * Adds the rear view to be displayed when this component is in state *checked*.
     *
     * The rear view can be a ViewGroup.
     *
     * @param layoutResId the layout resource identifier.
     * @throws IllegalArgumentException if the provided view is null
     * @see .getRearLayout
     * @see .addRearLayout
     */
    fun addRearLayout(@LayoutRes layoutResId: Int) {
        if (layoutResId == R.layout.flipview_rear) {
            if (DEBUG) Log.d(TAG, "Adding inner RearLayout")
        } else if (DEBUG) Log.d(TAG, "Adding user RearLayout $layoutResId")
        addRearLayout(LayoutInflater.from(context).inflate(layoutResId, this, false))
    }

    /**
     * Adds the rear view to be displayed when this component is in state *checked*.
     *
     * The provided view must be not `null`, or `IllegalArgumentException` will
     * be raised.
     * The rear view can be a ViewGroup.
     *
     * @param view the rear view. Must not be `null`
     * @throws IllegalArgumentException if the provided view is null
     * @see .getRearLayout
     * @see .addRearLayout
     */
    fun addRearLayout(view: View) {
        var viewGroup: ViewGroup = this
        // Assign current count as our Index for rear View in case multiples views are added.
        var whichChild = childCount //By default suppose it's already our rear View
        if (DEBUG) Log.d(TAG, "RearLayout index=$whichChild")
        // If the View is another ViewGroup use it as new our rear View to flip
        if (view is ViewGroup) {
            if (DEBUG) Log.d(TAG, "RearLayout is a ViewGroup")
            viewGroup = view
            whichChild = 0 //Override the index: use the first position to locate the ImageView in this ViewGroup
        }
        // If any ImageView is provided, reference to this rear ImageView is saved
        if (viewGroup.getChildAt(whichChild) is ImageView) {
            if (DEBUG) Log.d(TAG, "Found ImageView in the RearLayout")
            rearImageView = viewGroup.getChildAt(whichChild) as ImageView
        } else if (whichChild > 2) {
            rearImageView = null //Rollback in case multiple views are added (user must provide already the image in each layout added)
        }
        // Watch out! User can add first the rear view and after the front view that must be
        // always at position 0. While all rear views start from index = 1.
        this.addView(view, if (childCount == 0) REAR_VIEW_INDEX else childCount)
    }

    override fun addView(view: View, whichChild: Int) {
        requireNotNull(view) { "The provided view must not be null" }
        if (DEBUG) Log.d(TAG, "Setting child view at index $whichChild")
        if (super.getChildAt(whichChild) != null) {
            super.removeViewAt(whichChild)
        }
        super.addView(view, whichChild, super.generateDefaultLayoutParams())
    }

    /**
     * Sets the front text of the TextView that must be present in the layout.
     *
     * The text will be displayed for the *unchecked* status.
     *
     * @param text the new text for the TextView
     * @see .getFrontTextView
     */
    fun setFrontText(text: CharSequence?) {
        if (frontTextView == null) {
            Log.e(TAG, "TextView not found in the first child of the FrontLayout. Text cannot be set!")
            return
        }
        frontTextView!!.text = text
    }

    /**
     * Sets the front image for the *unchecked* status.
     *
     * @param imageResId must be a valid image resourceId
     * @see .getFrontImageView
     * @see .setFrontImageBitmap
     */
    fun setFrontImage(imageResId: Int) {
        if (frontImageView == null) {
            // Avoid the warning message if image is correctly null because of a TextView
            if (frontTextView == null) {
                Log.e(TAG, "ImageView not found in the first child of the FrontLayout. Image cannot be set!")
            }
            return
        }
        if (imageResId == 0) {
            Log.e(TAG, "Invalid imageResId=0")
            return
        }
        try {
            frontImageView!!.setPadding(frontImagePadding, frontImagePadding,
                    frontImagePadding, frontImagePadding)
            frontImageView!!.setImageResource(imageResId)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "No front resource image id $imageResId found. No Image can be set!")
        }
    }

    /**
     * Sets a bitmap for the front image for the *unchecked* status.
     *
     * @param bitmap the bitmap
     * @see .getFrontImageView
     * @see .setFrontImage
     */
    fun setFrontImageBitmap(bitmap: Bitmap?) {
        if (frontImageView == null) {
            Log.e(TAG, "ImageView not found in the first child of the FrontLayout. Bitmap cannot be set!")
            return
        }
        frontImageView!!.setImageBitmap(bitmap)
    }

    /**
     * Sets the rear image for the *checked* status.
     *
     * @param imageResId must be a valid image resourceId
     * @see .getRearImageView
     * @see .setRearImageBitmap
     */
    fun setRearImage(imageResId: Int) {
        if (rearImageView == null) {
            Log.e(TAG, "ImageView not found in the child of the RearLayout. Image cannot be set!")
            return
        }
        if (imageResId == 0) {
            Log.e(TAG, "Invalid imageResId=0")
            return
        }
        try {
            rearImageView!!.setPadding(rearImagePadding, rearImagePadding,
                    rearImagePadding, rearImagePadding)
            rearImageView!!.setImageResource(imageResId)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "No rear resource image id $imageResId found. Image cannot be set!")
        }
    }

    /**
     * Sets a bitmap for the rear image for the *checked* status.
     *
     * @param bitmap the bitmap
     * @see .getRearImageView
     * @see .setRearImage
     */
    fun setRearImageBitmap(bitmap: Bitmap?) {
        if (rearImageView == null) {
            Log.e(TAG, "ImageView not found in the child of the RearLayout. Bitmap cannot be set!")
            return
        }
        rearImageView!!.setImageBitmap(bitmap)
    }

    fun createBitmapFrom(pictureDrawable: PictureDrawable, size: Float): Bitmap {
        val radius = Math.ceil(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
                        resources.displayMetrics).toDouble()).toInt()
        val bitmap = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888)
        pictureDrawable.setBounds(0, 0, radius, radius)
        val canvas = Canvas(bitmap)
        canvas.drawPicture(pictureDrawable.picture, pictureDrawable.bounds)
        return bitmap
    }

    //************************
    // BACKGROUND DRAWABLES **
    //************************
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setChildBackgroundDrawable(whichChild: Int, @DrawableRes drawableResId: Int) {
        try {
            setChildBackgroundDrawable(whichChild,
                    if (hasLollipop()) context.getDrawable(drawableResId) else context.resources.getDrawable(drawableResId))
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Resource with id $drawableResId could not be found. Drawable cannot be set!")
        }
    }

    fun setChildBackgroundDrawable(whichChild: Int, drawable: Drawable?) {
        if (getChildAt(whichChild) != null) {
            getChildAt(whichChild).setBackgroundDrawable(drawable)
        }
    }

    fun getChildBackgroundDrawable(whichChild: Int): Drawable {
        return getChildAt(whichChild).background
    }

    fun setChildBackgroundColor(whichChild: Int, @ColorInt color: Int) {
        setChildBackgroundDrawable(whichChild, createOvalShapeDrawable(color))
    }

    companion object {
        private val TAG = FlipView::class.java.simpleName
        private var DEBUG = false
        private val EMPTY_LISTENER: OnFlippingListener = object : OnFlippingListener {
            override fun onFlipped(flipView: FlipView?, checked: Boolean) {}
        }

        /**
         * Child index to access the **front** view
         */
        const val FRONT_VIEW_INDEX = 0

        /**
         * Child index to access the **rear** view
         */
        const val REAR_VIEW_INDEX = 1

        /**
         * Animations attributes
         */
        private var enableInitialAnimation = true
        const val DEFAULT_INITIAL_DELAY = 500
        const val SCALE_STEP_DELAY = 35
        const val STOP_LAYOUT_ANIMATION_DELAY = 1500
        const val FLIP_INITIAL_DELAY = 250
        const val FLIP_DURATION = 100
        const val INITIAL_ANIMATION_DURATION = 250
        const val REAR_IMAGE_ANIMATION_DURATION = 150
        const val DEFAULT_INTERVAL = 3000
        private var initialDelay = DEFAULT_INITIAL_DELAY.toLong()
        //******************
        // STATIC METHODS **
        //******************
        /**
         * Calls this method once, to enable or disable DEBUG logs.
         *
         * DEBUG logs are disabled by default.
         *
         * @param enable true to show DEBUG logs, false to hide them.
         */
        fun enableLogs(enable: Boolean) {
            DEBUG = enable
        }

        /**
         * API 22
         *
         * @return true if the current Android version is Lollipop
         * @see Build.VERSION_CODES.LOLLIPOP_MR1
         */
        fun hasLollipop(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
        }

        /**
         * Creates a Scale animation programmatically.
         *
         * Usage of this method helps rendering the page much faster (it doesn't load the
         * animation file from disk).
         *
         * @return [ScaleAnimation] relative to self with pivotXY at 50%
         */
        fun createScaleAnimation(): Animation {
            return ScaleAnimation(0F, 1.0F, 0F, 1.0F,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)
        }
        /**
         * Reset initial layout animation delay to a custom delay. This avoid to continuously
         * increase the next step delay of the next FlipView on the screen!
         *
         * **Note:** Call this method at the beginning of `onCreate/onActivityCreated`.
         *
         * @param enable    optionally future start animation can be disabled
         * @param nextDelay the new custom initial delay
         * @see .resetLayoutAnimationDelay
         * @see .stopLayoutAnimation
         */
        /**
         * Convenience method for [.resetLayoutAnimationDelay].
         *
         * This method enables and resets initial layout animation delay to the default
         * [.DEFAULT_INITIAL_DELAY].
         * **Note:** Call this method at the beginning of `onCreate/onActivityCreated`.
         *
         * @see .resetLayoutAnimationDelay
         * @see .stopLayoutAnimation
         */
        @JvmOverloads
        fun resetLayoutAnimationDelay(enable: Boolean = true, @IntRange(from = 0) nextDelay: Long = DEFAULT_INITIAL_DELAY.toLong()) {
            enableInitialAnimation = enable
            initialDelay = nextDelay
        }

        /**
         * Stops and Resets layout animation after [.STOP_LAYOUT_ANIMATION_DELAY].
         *
         * This gives the time to perform all entry animations but to stop further animations when
         * screen is fully rendered: ALL Views will not perform initial animation anymore
         * until a new reset.
         * **Note:**
         * <br></br>- The delay time has been identified at 1.5 seconds (1500ms).
         * <br></br>- Call this method at the end of `onCreate/onActivityCreated`.
         *
         * @see .resetLayoutAnimationDelay
         * @see .resetLayoutAnimationDelay
         */
        fun stopLayoutAnimation() {
            Handler().postDelayed({ resetLayoutAnimationDelay(false, DEFAULT_INITIAL_DELAY.toLong()) }, STOP_LAYOUT_ANIMATION_DELAY.toLong())
        }
        //*******************
        // SHAPE DRAWABLES **
        //*******************
        /**
         * Helper for OvalShape constructor.
         *
         * @param color the desired color
         * @return `ShapeDrawable` with Oval shape
         */
        fun createOvalShapeDrawable(@ColorInt color: Int): ShapeDrawable {
            return createShapeDrawable(color, OvalShape())
        }

        /**
         * Helper for ArcShape constructor.
         *
         * @param color      the desired color
         * @param startAngle the angle (in degrees) where the arc begins
         * @param sweepAngle the sweep angle (in degrees).
         * Anything equal to or greater than 360 results in a complete circle/oval.
         * @return `ShapeDrawable` with Arc shape
         */
        fun createArcShapeDrawable(
                @ColorInt color: Int, startAngle: Float, sweepAngle: Float): ShapeDrawable {
            return createShapeDrawable(color, ArcShape(startAngle, sweepAngle))
        }

        /**
         * Helper for RoundRectShape constructor.
         * Specifies an outer (round)rect and an optional inner (round)rect.
         *
         * @param color      the desired color
         * @param outerRadii An array of 8 radius values, for the outer roundrect.
         * The first two floats are for the top-left corner (remaining pairs correspond clockwise).
         * For no rounded corners on the outer rectangle, pass null.
         * @param inset      A RectF that specifies the distance from the inner rect to each side of the outer rect.
         * For no inner, pass null.
         * @param innerRadii An array of 8 radius values, for the inner roundrect.
         * The first two floats are for the top-left corner (remaining pairs correspond clockwise).
         * For no rounded corners on the inner rectangle, pass null.
         * If inset parameter is null, this parameter is ignored.
         * @return `ShapeDrawable` with RoundRect shape
         */
        fun createRoundRectShapeDrawable(
                @ColorInt color: Int, outerRadii: FloatArray?, inset: RectF?, innerRadii: FloatArray?): ShapeDrawable {
            return createShapeDrawable(color, RoundRectShape(outerRadii, inset, innerRadii))
        }

        /**
         * Helper method to create ShapeDrawables at runtime.
         */
        private fun createShapeDrawable(@ColorInt color: Int, shape: Shape): ShapeDrawable {
            val shapeDrawable = ShapeDrawable(shape)
            shapeDrawable.paint.color = color
            shapeDrawable.paint.isAntiAlias = true
            shapeDrawable.paint.style = Paint.Style.FILL
            return shapeDrawable
        }
    }
}