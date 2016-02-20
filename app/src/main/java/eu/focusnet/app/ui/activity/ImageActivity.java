package eu.focusnet.app.ui.activity;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import eu.focusnet.app.R;

/**
 * Activity use to display an image
 */
public class ImageActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		ImageView tookImage = (ImageView) findViewById(R.id.tookImage);
//        byte[] bytes = getIntent().getByteArrayExtra("image");
//        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		Uri imageUri = (Uri) getIntent().getExtras().get("imageUri");
		tookImage.setImageURI(imageUri);

	}
}