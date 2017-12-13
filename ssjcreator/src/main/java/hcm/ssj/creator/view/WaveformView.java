/*
 * WaveformView.java
 * Copyright (c) 2017
 * Authors: Ionut Damian, Michael Dietz, Frank Gaibler, Daniel Langerenken, Simon Flutura,
 * Vitalijs Krumins, Antonio Grieco
 * *****************************************************
 * This file is part of the Social Signal Interpretation for Java (SSJ) framework
 * developed at the Lab for Human Centered Multimedia of the University of Augsburg.
 *
 * SSJ has been inspired by the SSI (http://openssi.net) framework. SSJ is not a
 * one-to-one port of SSI to Java, it is an approximation. Nor does SSJ pretend
 * to offer SSI's comprehensive functionality and performance (this is java after all).
 * Nevertheless, SSJ borrows a lot of programming patterns from SSI.
 *
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this library; if not, see <http://www.gnu.org/licenses/>.
 */

package hcm.ssj.creator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import hcm.ssj.audio.AudioUtils;
import hcm.ssj.creator.R;

/**
 * Custom view to display audio waveform.
 */
public class WaveformView extends View
{
	private static final boolean ENABLE_ANTI_ALIAS = true;
	private static final float STROKE_THICKNESS = 4;

	private Paint strokePaint;
	private Paint fillPaint;
	private Rect drawRect;

	private int width;
	private int height;

	private short[] samples;
	private Bitmap cachedWaveformBitmap;

	public WaveformView(Context context)
	{
		super(context);
		init(null, 0);
	}

	public WaveformView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
	}

	public WaveformView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	public void setSamples(short[] s)
	{
		samples = s;
		createWaveform();
	}

	@Override
	protected void onSizeChanged(int w, int h, int ow, int oh)
	{
		width = getMeasuredWidth();
		height = getMeasuredHeight();
		drawRect = new Rect(getPaddingLeft(), getPaddingTop(),
							width - getPaddingLeft() - getPaddingRight(),
							height - getPaddingTop() - getPaddingBottom());
		createWaveform();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if (cachedWaveformBitmap != null)
		{
			canvas.drawBitmap(cachedWaveformBitmap, null, drawRect, null);
		}
	}

	/**
	 * Read XML attribute values of a waveform view and initialize colors.
	 * @param attrs Set of XML attributes for customization options.
	 * @param defStyle Integer which represents the default style.
	 */
	private void init(AttributeSet attrs, int defStyle)
	{
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
																 R.styleable.WaveformView,
																 defStyle, 0);

		int strokeColor = getResources().getColor(R.color.colorWaveform);
		int fillColor = getResources().getColor(R.color.colorWaveformFill);
		a.recycle();


		strokePaint = new Paint();
		strokePaint.setColor(strokeColor);
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setStrokeWidth(STROKE_THICKNESS);
		strokePaint.setAntiAlias(ENABLE_ANTI_ALIAS);

		fillPaint = new Paint();
		fillPaint.setStyle(Paint.Style.FILL);
		fillPaint.setAntiAlias(ENABLE_ANTI_ALIAS);
		fillPaint.setColor(fillColor);

		drawRect = new Rect(0, 0, width, height);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 200);
		setLayoutParams(layoutParams);
	}

	/**
	 * Show audio waveform on screen.
	 */
	private void createWaveform()
	{
		if (width <= 0 || height <= 0 || samples == null)
		{
			return;
		}
		Canvas canvas;
		cachedWaveformBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(cachedWaveformBitmap);

		Path waveformPath = drawWaveform(width, height, samples);
		canvas.drawPath(waveformPath, fillPaint);
		canvas.drawPath(waveformPath, strokePaint);
		invalidate();
	}

	/**
	 * Draw waveform as a line path from given audio samples.
	 * @param width Width of view.
	 * @param height Height of view.
	 * @param buffer Audio samples.
	 * @return Waveform path.
	 */
	private Path drawWaveform(int width, int height, short[] buffer)
	{
		Path waveformPath = new Path();
		float centerY = height / 2.0f;
		float max = Short.MAX_VALUE;

		short[][] extremes = AudioUtils.getExtremes(buffer, width);

		// Start path at the origin.
		waveformPath.moveTo(0, centerY);

		// Draw maximums.
		for (int x = 0; x < width; x++)
		{
			short sample = extremes[x][0];
			float y = centerY - ((sample / max) * centerY);
			waveformPath.lineTo(x, y);
		}

		// Draw minimums.
		for (int x = width - 1; x >= 0; x--)
		{
			short sample = extremes[x][1];
			float y = centerY - ((sample / max) * centerY);
			waveformPath.lineTo(x, y);
		}

		waveformPath.close();
		return waveformPath;
	}
}
