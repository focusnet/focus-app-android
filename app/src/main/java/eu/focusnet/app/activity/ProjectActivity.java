package eu.focusnet.app.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import eu.focusnet.app.fragment.ProjectFragment;
import eu.focusnet.app.manager.FragmentManager;
import eu.focusnet.app.util.Constant;

public class ProjectActivity extends AppCompatActivity {

    private String projectTitle, projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if(projectTitle == null && projectId == null) {
            projectTitle = getIntent().getStringExtra(Constant.TITLE);
            //Path is the same as projectId
            projectId = getIntent().getStringExtra(Constant.PATH);
        }
        else{
            projectTitle = savedInstanceState.getString(Constant.TITLE);
            //Path is the same as projectId
            projectId = savedInstanceState.getString(Constant.PATH);
        }

        setTitle(projectTitle);
        Fragment fragment = new ProjectFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.PATH, projectId);
        fragment.setArguments(bundle);
        FragmentManager.replaceFragment(R.id.project_container, fragment, getFragmentManager());
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putString(Constant.TITLE, projectTitle);
        saveInstanceState.putString(Constant.PATH, projectId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
