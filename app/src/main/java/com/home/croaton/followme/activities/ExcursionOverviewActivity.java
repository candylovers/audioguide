package com.home.croaton.followme.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.home.croaton.followme.R;
import com.home.croaton.followme.domain.Excursion;
import com.home.croaton.followme.domain.ExcursionBrief;
import com.home.croaton.followme.download.IExcursionDownloader;
import com.home.croaton.followme.download.S3ExcursionDownloader;

import java.util.HashMap;

public class ExcursionOverviewActivity extends AppCompatActivity {

    private ExcursionBrief excursion;

    private Button openButton;
    private Button loadButton;
    private ProgressDialog progressDialog;
    private SliderLayout _slider;
    private String _language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_overview);

        Intent intent = getIntent();
        excursion = intent.getParcelableExtra(IntentNames.SELECTED_EXCURSION_BRIEF);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        _language = sharedPref.getString(getString(R.string.settings_language_preference), "ru");

        initUI();
    }

    private void initUI() {
        _slider = (SliderLayout)findViewById(R.id.slider);
        HashMap<String,Integer> file_maps = new HashMap<>();
        file_maps.put("Write smart text here1", R.drawable.gamlastan);
        file_maps.put("Write smart text here2", R.drawable.gamlastan1);
        file_maps.put("Write smart text here3", R.drawable.gamlastan2);

        for(String name : file_maps.keySet()){
            DefaultSliderView textSliderView = new DefaultSliderView(this);

            // initialize a SliderLayout
            textSliderView
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            _slider.addSlider(textSliderView);
        }
        _slider.setLayoutMode(ViewGroup.LAYOUT_MODE_CLIP_BOUNDS);
        _slider.setPresetTransformer(SliderLayout.Transformer.RotateDown);
        _slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        _slider.setCustomAnimation(new DescriptionAnimation());
        _slider.setDuration(4000);

        openButton = (Button) findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExcursionOverviewActivity.this, MapsActivity.class);
                intent.putExtra(IntentNames.SELECTED_EXCURSION_NAME, excursion.getName());
                startActivity(intent);
            }
        });

        loadButton = (Button) findViewById(R.id.load_button);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadExcursionTask downloadTask = new DownloadExcursionTask(ExcursionOverviewActivity.this);
                downloadTask.execute(excursion);
            }
        });

        progressDialog = new ProgressDialog(ExcursionOverviewActivity.this);
        progressDialog.setMessage("A message");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);

        TextView aboutExcursion = (TextView)findViewById(R.id.excursion_overview);
        aboutExcursion.setText(excursion.getContentByLanguage(_language).getOverview());

        TextView excursionDuration = (TextView)findViewById(R.id.excursion_duration);
        excursionDuration.setText(getString(R.string.excursion_duration) + Double.toString(excursion.getDuration()));

        TextView excursionLength = (TextView)findViewById(R.id.excursion_length);
        excursionLength.setText(getString(R.string.excursion_length)+ Double.toString(excursion.getLength()));

        TextView excursionCost = (TextView)findViewById(R.id.excursion_cost);
        excursionCost.setText(getString(R.string.excursion_cost)+ Double.toString(excursion.getCost()) + "€");
    }

    private class DownloadExcursionTask extends AsyncTask<ExcursionBrief, Integer, Excursion> {
        private final Context context;

        public DownloadExcursionTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Excursion doInBackground(ExcursionBrief... inputs) {
            IExcursionDownloader downloader = new S3ExcursionDownloader(context);
            return downloader.downloadExcursion(inputs[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Excursion result) {
            progressDialog.dismiss();
        }
    }
}
