package eu.focusnet.app.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import eu.focusnet.app.activity.R;

/**
 * Created by admin on 06.10.2015.
 */
public class CustomProgressDialog extends ProgressDialog {

        private AnimationDrawable animation;

        public CustomProgressDialog(Context context) {
            super(context);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.custom_progress_dialog);

            ImageView la = (ImageView) findViewById(R.id.animation);
            la.setBackgroundResource(R.drawable.custom_progress_dialog_animation);
            animation = (AnimationDrawable) la.getBackground();
        }

        @Override
        public void show() {
            super.show();
            animation.start();
        }

        @Override
        public void dismiss() {
            super.dismiss();
            animation.stop();
        }
}
