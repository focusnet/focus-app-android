package eu.focusnet.app.model.internal.widgets;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.internal.DataContext;

/**
 * Created by admin on 28.01.2016.
 */

//TODO implement this class with its methods
public class CameraWidgetInstance extends WidgetInstance
{

	private String savedImage;

	/**
	 * C'tor
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public CameraWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	@Override
	protected void processConfig()
	{

	}

	/**
	 * Save the image being captured as a base64-encoded string.
	 */
	public void saveImage(Bitmap bitmap)
	{
		if (bitmap == null) {
			this.savedImage = null;
			return;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream .toByteArray();
		this.savedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
	}

	/**
	 * Get the saved image.
	 *
	 * @return
	 */
	public String getSavedImage()
	{
		return savedImage;
	}
}
