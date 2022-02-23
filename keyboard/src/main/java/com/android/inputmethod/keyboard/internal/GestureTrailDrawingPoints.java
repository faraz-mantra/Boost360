/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.inputmethod.keyboard.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.SystemClock;

import com.android.inputmethod.latin.utils.ResizableIntArray;

import io.separ.neural.inputmethod.Utils.ColorUtils;
import io.separ.neural.inputmethod.indic.Constants;
import io.separ.neural.inputmethod.indic.R;

/**
 * This class holds drawing points to represent a gesture trail. The gesture trail may contain
 * multiple non-contiguous gesture strokes and will be animated asynchronously from gesture input.
 * <p>
 * On the other hand, {@link GestureStrokeDrawingPoints} class holds drawing points of each gesture
 * stroke. This class holds drawing points of those gesture strokes to draw as a gesture trail.
 * Drawing points in this class will be asynchronously removed when fading out animation goes.
 */
final class GestureTrailDrawingPoints {
    public static final boolean DEBUG_SHOW_POINTS = false;
    public static final int POINT_TYPE_SAMPLED = 1;
    public static final int POINT_TYPE_INTERPOLATED = 2;

    private static final int DEFAULT_CAPACITY = GestureStrokeDrawingPoints.PREVIEW_CAPACITY;
    // Use this value as imaginary zero because x-coordinates may be zero.
    private static final int DOWN_EVENT_MARKER = -128;
    // These three {@link ResizableIntArray}s should be synchronized by {@link #mEventTimes}.
    private final ResizableIntArray mXCoordinates = new ResizableIntArray(DEFAULT_CAPACITY);
    private final ResizableIntArray mYCoordinates = new ResizableIntArray(DEFAULT_CAPACITY);
    private final ResizableIntArray mEventTimes = new ResizableIntArray(DEFAULT_CAPACITY);
    private final ResizableIntArray mPointTypes = new ResizableIntArray(
            DEBUG_SHOW_POINTS ? DEFAULT_CAPACITY : 0);
    private final RoundedLine mRoundedLine = new RoundedLine();
    private final Rect mRoundedLineBounds = new Rect();
    private int mCurrentStrokeId = -1;
    // The wall time of the zero value in {@link #mEventTimes}
    private long mCurrentTimeBase;
    private int mTrailStartIndex;
    private int mLastInterpolatedDrawIndex;

    private static int markAsDownEvent(final int xCoord) {
        return DOWN_EVENT_MARKER - xCoord;
    }

    private static boolean isDownEventXCoord(final int xCoordOrMark) {
        return xCoordOrMark <= DOWN_EVENT_MARKER;
    }

    private static int getXCoordValue(final int xCoordOrMark) {
        return isDownEventXCoord(xCoordOrMark)
                ? DOWN_EVENT_MARKER - xCoordOrMark : xCoordOrMark;
    }

    /**
     * Calculate the alpha of a gesture trail.
     * A gesture trail starts from fully opaque. After mFadeStartDelay has been passed, the alpha
     * of a trail reduces in proportion to the elapsed time. Then after mFadeDuration has been
     * passed, a trail becomes fully transparent.
     *
     * @param elapsedTime the elapsed time since a trail has been made.
     * @param params      gesture trail display parameters
     * @return the width of a gesture trail
     */
    private static int getAlpha(final int elapsedTime, final GestureTrailDrawingParams params) {
        if (elapsedTime < params.mFadeoutStartDelay) {
            return Constants.Color.ALPHA_OPAQUE;
        }
        final int decreasingAlpha = Constants.Color.ALPHA_OPAQUE
                * (elapsedTime - params.mFadeoutStartDelay)
                / params.mFadeoutDuration;
        return Constants.Color.ALPHA_OPAQUE - decreasingAlpha;
    }

    /**
     * Calculate the width of a gesture trail.
     * A gesture trail starts from the width of mTrailStartWidth and reduces its width in proportion
     * to the elapsed time. After mTrailEndWidth has been passed, the width becomes mTraiLEndWidth.
     *
     * @param elapsedTime the elapsed time since a trail has been made.
     * @param params      gesture trail display parameters
     * @return the width of a gesture trail
     */
    private static float getWidth(final int elapsedTime, final GestureTrailDrawingParams params) {
        final float deltaWidth = params.mTrailStartWidth - params.mTrailEndWidth;
        return params.mTrailStartWidth - (deltaWidth * elapsedTime) / params.mTrailLingerDuration;
    }

    public void addStroke(final GestureStrokeDrawingPoints stroke, final long downTime) {
        synchronized (mEventTimes) {
            addStrokeLocked(stroke, downTime);
        }
    }

    private void addStrokeLocked(final GestureStrokeDrawingPoints stroke, final long downTime) {
        final int trailSize = mEventTimes.getLength();
        stroke.appendPreviewStroke(mEventTimes, mXCoordinates, mYCoordinates, mPointTypes);
        if (mEventTimes.getLength() == trailSize) {
            return;
        }
        final int[] eventTimes = mEventTimes.getPrimitiveArray();
        final int strokeId = stroke.getGestureStrokeId();
        // Because interpolation algorithm in {@link GestureStrokeDrawingPoints} can't determine
        // the interpolated points in the last segment of gesture stroke, it may need recalculation
        // of interpolation when new segments are added to the stroke.
        // {@link #mLastInterpolatedDrawIndex} holds the start index of the last segment. It may
        // be updated by the interpolation
        // {@link GestureStrokeDrawingPoints#interpolatePreviewStroke}
        // or by animation {@link #drawGestureTrail(Canvas,Paint,Rect,GestureTrailDrawingParams)}
        // below.
        final int lastInterpolatedIndex = (strokeId == mCurrentStrokeId)
                ? mLastInterpolatedDrawIndex : trailSize;
        mLastInterpolatedDrawIndex = stroke.interpolateStrokeAndReturnStartIndexOfLastSegment(
                lastInterpolatedIndex, mEventTimes, mXCoordinates, mYCoordinates, mPointTypes);
        if (strokeId != mCurrentStrokeId) {
            final int elapsedTime = (int) (downTime - mCurrentTimeBase);
            for (int i = mTrailStartIndex; i < trailSize; i++) {
                // Decay the previous strokes' event times.
                eventTimes[i] -= elapsedTime;
            }
            final int[] xCoords = mXCoordinates.getPrimitiveArray();
            xCoords[trailSize] = markAsDownEvent(xCoords[trailSize]);
            mCurrentTimeBase = downTime - eventTimes[trailSize];
            mCurrentStrokeId = strokeId;
        }
    }

    /**
     * Draw gesture trail
     *
     * @param context
     * @param canvas        The canvas to draw the gesture trail
     * @param paint         The paint object to be used to draw the gesture trail
     * @param outBoundsRect the bounding box of this gesture trail drawing
     * @param params        The drawing parameters of gesture trail
     * @return true if some gesture trails remain to be drawn
     */
    public boolean drawGestureTrail(Context context, final Canvas canvas, final Paint paint,
                                    final Rect outBoundsRect, final GestureTrailDrawingParams params) {
        synchronized (mEventTimes) {
            return drawGestureTrailLocked(context, canvas, paint, outBoundsRect, params);
        }
    }

    private boolean drawGestureTrailLocked(Context context, Canvas canvas, Paint paint, Rect outBoundsRect, GestureTrailDrawingParams params) {
        outBoundsRect.setEmpty();
        int trailSize = this.mEventTimes.getLength();
        if (trailSize == 0) {
            return DEBUG_SHOW_POINTS;
        }
        int[] eventTimes = this.mEventTimes.getPrimitiveArray();
        int[] xCoords = this.mXCoordinates.getPrimitiveArray();
        int[] yCoords = this.mYCoordinates.getPrimitiveArray();
        int sinceDown = (int) (SystemClock.uptimeMillis() - this.mCurrentTimeBase);
        int startIndex = this.mTrailStartIndex;
        while (startIndex < trailSize && sinceDown - eventTimes[startIndex] >= params.mTrailLingerDuration) {
            startIndex += POINT_TYPE_SAMPLED;
        }
        this.mTrailStartIndex = startIndex;
        if (startIndex < trailSize) {
            paint.setColor(ColorUtils.colorProfile.getIcon());
            paint.setStyle(Paint.Style.FILL);
            RoundedLine roundedLine = this.mRoundedLine;
            int p1x = getXCoordValue(xCoords[startIndex]);
            int p1y = yCoords[startIndex];
            float r1 = getWidth(sinceDown - eventTimes[startIndex], params) / 2.0f;
            for (int i = startIndex + POINT_TYPE_SAMPLED; i < trailSize; i += POINT_TYPE_SAMPLED) {
                int elapsedTime = sinceDown - eventTimes[i];
                int p2x = getXCoordValue(xCoords[i]);
                int p2y = yCoords[i];
                float r2 = getWidth(elapsedTime, params) / 2.0f;
                if (!isDownEventXCoord(xCoords[i])) {
                    Path path = roundedLine.makePath((float) p1x, (float) p1y, r1 * params.mTrailBodyRatio, (float) p2x, (float) p2y, r2 * params.mTrailBodyRatio);
                    if (!path.isEmpty()) {
                        roundedLine.getBounds(this.mRoundedLineBounds);
                        if (params.mTrailShadowEnabled) {
                            float shadow2 = r2 * params.mTrailShadowRatio;
                            paint.setShadowLayer(shadow2, 0.0f, 0.0f, params.mTrailColor);
                            int shadowInset = -((int) Math.ceil((double) shadow2));
                            this.mRoundedLineBounds.inset(shadowInset, shadowInset);
                        }
                        outBoundsRect.union(this.mRoundedLineBounds);
                        paint.setAlpha(getAlpha(elapsedTime, params));
                        paint.setColor(context.getResources().getColor(R.color.primaryColor));
                        canvas.drawPath(path, paint);
                    }
                }
                p1x = p2x;
                p1y = p2y;
                r1 = r2;
            }
        }
        int newSize = trailSize - startIndex;
        if (newSize < startIndex) {
            this.mTrailStartIndex = 0;
            if (newSize > 0) {
                System.arraycopy(eventTimes, startIndex, eventTimes, 0, newSize);
                System.arraycopy(xCoords, startIndex, xCoords, 0, newSize);
                System.arraycopy(yCoords, startIndex, yCoords, 0, newSize);
            }
            this.mEventTimes.setLength(newSize);
            this.mXCoordinates.setLength(newSize);
            this.mYCoordinates.setLength(newSize);
            this.mLastInterpolatedDrawIndex = Math.max(this.mLastInterpolatedDrawIndex - startIndex, 0);
        }
        return newSize > 0 ? true : DEBUG_SHOW_POINTS;
    }

    private void debugDrawPoints(final Canvas canvas, final int startIndex, final int endIndex,
                                 final Paint paint) {
        final int[] xCoords = mXCoordinates.getPrimitiveArray();
        final int[] yCoords = mYCoordinates.getPrimitiveArray();
        final int[] pointTypes = mPointTypes.getPrimitiveArray();
        // {@link Paint} that is zero width stroke and anti alias off draws exactly 1 pixel.
        paint.setAntiAlias(false);
        paint.setStrokeWidth(0);
        for (int i = startIndex; i < endIndex; i++) {
            final int pointType = pointTypes[i];
            if (pointType == POINT_TYPE_INTERPOLATED) {
                paint.setColor(Color.RED);
            } else if (pointType == POINT_TYPE_SAMPLED) {
                paint.setColor(0xFFA000FF);
            } else {
                paint.setColor(Color.GREEN);
            }
            canvas.drawPoint(getXCoordValue(xCoords[i]), yCoords[i], paint);
        }
        paint.setAntiAlias(true);
    }
}