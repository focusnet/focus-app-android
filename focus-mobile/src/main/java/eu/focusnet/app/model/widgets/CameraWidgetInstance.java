/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.widgets;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.gson.WidgetTemplate;


/**
 * An instance containing all information pertaining to a camera capture widget.
 */
public class CameraWidgetInstance extends DataCollectionWidgetInstance
{

	/**
	 * Base64 representation of the captured image
	 */
	private String savedImage;

	/**
	 * C'tor
	 *
	 * @param wTpl Inherited
	 * @param layoutConfig Inherited
	 * @param dataCtx Inherited
	 */
	public CameraWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	/**
	 * Nothing specific to perform
	 */
	@Override
	protected void processSpecificConfig()
	{

	}

	/**
	 * Save the image being captured as a base64-encoded string.
	 *
	 * @param bitmap The Bitmap image having been captured.
	 */
	public void saveImage(Bitmap bitmap)
	{
		if (bitmap == null) {
			this.savedImage = null;
			return;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		this.savedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
	}

}
