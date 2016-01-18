package eu.focusnet.app.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import eu.focusnet.app.R;
import eu.focusnet.app.activity.ImageActivity;
import eu.focusnet.app.common.WidgetFragment;
import eu.focusnet.app.util.ViewUtil;

/**
 * Created by yandypiedra on 13.01.16.
 */
public class CameraWidgetFragment extends WidgetFragment {


    private final int PICTURE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;

    private Button deleteButton, viewButton, takePictureButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_camera, container, false);

        imageView = (ImageView) viewRoot.findViewById(R.id.picture);

        viewButton = (Button) viewRoot.findViewById(R.id.button_view);
        viewButton.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View v) {
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
                                            public void onClick(View v) {
                                                imageView.setImageBitmap(ViewUtil.getBitmap(getActivity(), R.drawable.focus_logo));
                                                deleteButton.setEnabled(false);
                                                viewButton.setEnabled(false);
                                                takePictureButton.setText("Take a Picture");
                                                imageUri = null;
                                            }
                                        }
        );



        takePictureButton = (Button) viewRoot.findViewById(R.id.button_take_picture);
        takePictureButton.setOnClickListener(new View.OnClickListener()

                                             {
                                                 @Override
                                                 public void onClick(View v) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICTURE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                //    Bitmap tookPicture = (Bitmap) data.getExtras().get("data");
//                Bitmap thumbnail = null;
//                try {
//                    thumbnail = MediaStore.Images.Media.getBitmap(
//                            getContentResolver(), imageUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                //  ImageView imageView = (ImageView) findViewById(R.id.imageView);
                // imageView.setImageBitmap(tookPicture);

                imageView.setImageURI(imageUri);
                deleteButton.setEnabled(true);
                viewButton.setEnabled(true);
                takePictureButton.setText("Replace Picture");
            }
        }
    }

}
