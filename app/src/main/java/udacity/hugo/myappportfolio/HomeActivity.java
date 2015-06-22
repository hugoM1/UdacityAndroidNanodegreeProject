package udacity.hugo.myappportfolio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import udacity.hugo.myappportfolio.nav.NavigationHelper;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSpotify, btnScores, btnLibrary, btnBuidlIt, btnXYZReader, btnCapstone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnSpotify = (Button) findViewById(R.id.btn_spotify_streamer);
        btnScores = (Button) findViewById(R.id.btn_scores_app);
        btnLibrary = (Button) findViewById(R.id.btn_library_app);
        btnBuidlIt = (Button) findViewById(R.id.btn_build_it_app);
        btnXYZReader = (Button) findViewById(R.id.btn_xyz_reader_app);
        btnCapstone = (Button) findViewById(R.id.btn_capstone_app);

        btnSpotify.setOnClickListener(this);
        btnScores.setOnClickListener(this);
        btnLibrary.setOnClickListener(this);
        btnBuidlIt.setOnClickListener(this);
        btnXYZReader.setOnClickListener(this);
        btnCapstone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        switch (v.getId()){
            case R.id.btn_spotify_streamer:{
                startApp(String.valueOf(button.getText()));
            }break;
            case R.id.btn_scores_app:{
                startApp(String.valueOf(button.getText()));
            }break;
            case R.id.btn_library_app:{
                startApp(String.valueOf(button.getText()));
            }break;
            case R.id.btn_build_it_app:{
                startApp(String.valueOf(button.getText()));
            }break;
            case R.id.btn_xyz_reader_app:{
                startApp(String.valueOf(button.getText()));
            }break;
            case R.id.btn_capstone_app:{
                startApp(String.valueOf(button.getText()));
            }break;
        }
    }

    private void startApp(String msg){
        NavigationHelper.openCurrentApp(this, msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
