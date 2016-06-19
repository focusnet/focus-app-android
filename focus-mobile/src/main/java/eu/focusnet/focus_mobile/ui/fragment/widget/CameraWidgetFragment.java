/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.focus_mobile.ui.fragment.widget;

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

import java.io.IOException;

import eu.focusnet.focus_mobile.R;
import eu.focusnet.focus_mobile.exception.FocusInternalErrorException;
import eu.focusnet.focus_mobile.model.internal.widgets.CameraWidgetInstance;
import eu.focusnet.focus_mobile.ui.activity.ImageActivity;
import eu.focusnet.focus_mobile.ui.util.Constant;

/**
 * Created by yandypiedra on 13.01.16.
 * <p/>
 * FIXME FIXME
 * FIXME change of configuration during capture (e.g. change orentiation) make app crash.
 * Probably because we don't handle savedInstance correctly everywhere.
 * May also affect external app.. Does not happen systematically.
 */
public class CameraWidgetFragment extends WidgetFragment
{

	private final int CAMERA_REQUEST = 19432;
	private Uri imageUri;
	private Uri tmpImageUri;
	private ImageView imageView;

	private Button deleteButton, viewButton, takePictureButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// setup
		super.onCreate(savedInstanceState);
		this.setupWidget(inflater.inflate(R.layout.fragment_widget_camera, container, false));

		imageView = (ImageView) this.rootView.findViewById(R.id.captured_image);

		viewButton = (Button) this.rootView.findViewById(R.id.button_view);
		viewButton.setOnClickListener(new View.OnClickListener()

									  {
										  @Override
										  public void onClick(View v)
										  {
											  Intent intent = new Intent(getActivity(), ImageActivity.class);
											  intent.putExtra(Constant.UI_EXTRA_IMAGE_URI, imageUri);
											  startActivity(intent);
										  }
									  }
		);

		deleteButton = (Button) this.rootView.findViewById(R.id.button_delete);
		deleteButton.setOnClickListener(new View.OnClickListener()

										{
											@Override
											public void onClick(View v)
											{
												imageView.setImageBitmap(null);
												deleteButton.setEnabled(false);
												viewButton.setEnabled(false);
												takePictureButton.setText(R.string.take_a_picture);
												imageUri = null;
												((CameraWidgetInstance) widgetInstance).saveImage(null);
											}
										}
		);


		takePictureButton = (Button) this.rootView.findViewById(R.id.button_take_picture);
		takePictureButton.setOnClickListener(new View.OnClickListener()
											 {
												 @Override
												 public void onClick(View v)
												 {
													 Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
													 ContentValues values = new ContentValues();
													 tmpImageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
													 cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpImageUri);
													 startActivityForResult(cameraIntent, CAMERA_REQUEST);
												 }
											 }

		);

		return this.rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
			// get a bitmap from imageuri and save it
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), tmpImageUri);
				imageView.setImageBitmap(bitmap);
				((CameraWidgetInstance) (this.widgetInstance)).saveImage(bitmap);
			}
			catch (IOException ex) {
				throw new FocusInternalErrorException("Cannot retrieve bitmap from file.");
			}

			imageUri = tmpImageUri;

			deleteButton.setEnabled(true);
			viewButton.setEnabled(true);
			takePictureButton.setText(R.string.replace_picture);
		}
	}

}
