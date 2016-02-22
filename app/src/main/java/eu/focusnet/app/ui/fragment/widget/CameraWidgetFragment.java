/**
 *
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
 *
 */

package eu.focusnet.app.ui.fragment.widget;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import eu.focusnet.app.R;
import eu.focusnet.app.ui.activity.ImageActivity;
import eu.focusnet.app.model.internal.widgets.CameraWidgetInstance;
import eu.focusnet.app.ui.util.ViewUtil;

/**
 * Created by yandypiedra on 13.01.16.
 */
public class CameraWidgetFragment extends WidgetFragment
{

	private final int PICTURE_REQUEST = 1;
	private Uri imageUri;
	private ImageView imageView;
	private CameraWidgetInstance cameraWidgetInstance;

	private Button deleteButton, viewButton, takePictureButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_camera, container, false);

		setWidgetLayout(viewRoot);

		cameraWidgetInstance = (CameraWidgetInstance) getWidgetInstance();

		TextView title = (TextView) viewRoot.findViewById(R.id.title);
		title.setText(cameraWidgetInstance.getTitle());

		imageView = (ImageView) viewRoot.findViewById(R.id.picture);

		viewButton = (Button) viewRoot.findViewById(R.id.button_view);
		viewButton.setOnClickListener(new View.OnClickListener()

									  {
										  @Override
										  public void onClick(View v)
										  {
											  Intent intent = new Intent(getActivity(), ImageActivity.class);
											  intent.putExtra("imageUri", imageUri);
											  startActivity(intent);
										  }
									  }
		);

		deleteButton = (Button) viewRoot.findViewById(R.id.button_delete);
		deleteButton.setOnClickListener(new View.OnClickListener()

										{
											@Override
											public void onClick(View v)
											{
												imageView.setImageBitmap(ViewUtil.getBitmap(getActivity(), R.drawable.focus_logo));
												deleteButton.setEnabled(false);
												viewButton.setEnabled(false);
												takePictureButton.setText("Take a Picture");
												imageUri = null;
												cameraWidgetInstance.saveImage(null);
											}
										}
		);


		takePictureButton = (Button) viewRoot.findViewById(R.id.button_take_picture);
		takePictureButton.setOnClickListener(new View.OnClickListener()

											 {
												 @Override
												 public void onClick(View v)
												 {
													 ContentValues values = new ContentValues();
													 values.put(MediaStore.Images.Media.TITLE, "New Picture");
													 values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
													 imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
													 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
													 intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
													 startActivityForResult(intent, PICTURE_REQUEST);
												 }
											 }

		);

		return viewRoot;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == PICTURE_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {

				//  ImageView imageView = (ImageView) findViewById(R.id.imageView);
				// imageView.setImageBitmap(tookPicture);
// FIXME required

				this.cameraWidgetInstance.saveImage((Bitmap) data.getExtras().get("data"));

				imageView.setImageURI(imageUri);
				deleteButton.setEnabled(true);
				viewButton.setEnabled(true);
				takePictureButton.setText("Replace Picture");
			}
		}
	}

}
