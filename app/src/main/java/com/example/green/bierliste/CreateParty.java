package com.example.green.bierliste;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Activity called from Hostbutton at StartMenue.
 * It is used to create a new Party with a name and an image.
 */
public class CreateParty extends AppCompatActivity {

    /**
     * Controls
     */
    private ImageView ivPartyImage;
    private EditText etPartyName;
    private LinearLayout llPartyImageAndName;
    private MenuItem actionFinish;
    private MenuItem actionForward;
    private MenuItem actionBack;

    private ListView lvAddedDrinks;
    private SurfaceView svDetectNewDrink;
    private LinearLayout llDrinkParameters;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private EditText etAddDrinkBarcode;
    private EditText etAddDrinkName;
    private EditText etAddDrinkPrice;

    private String PartyName;
    private Image PartyImage;


    private enum Step{
        ImageAndName, Drinks
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_party);

        InitializeViews();
        showStep(Step.ImageAndName);
    }


    private void InitializeViews(){
        ivPartyImage = findViewById(R.id.ivPartyImage);
        etPartyName = findViewById(R.id.etPartyName);

        llPartyImageAndName = findViewById(R.id.llPartyImageAndName);

        lvAddedDrinks = findViewById(R.id.lvAddedDrinks);
        svDetectNewDrink = findViewById(R.id.svDetectNewDrink);
        llDrinkParameters = findViewById(R.id.llDrinkParameters);

        barcodeDetector = new BarcodeDetector.Builder(this).build();

        cameraSource = new CameraSource.Builder(this,barcodeDetector)
                .build();
        cameraFocus(cameraSource, Camera.Parameters.FOCUS_MODE_AUTO);

        svDetectNewDrink.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {


                try {
                    /*if(ActivityCompat.checkSelfPermission(CreateParty.this, android.Manifest.permission.CAMERA)!= PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
                    {
                        Toast.makeText(CreateParty.this, "No Camera permissions", Toast.LENGTH_SHORT).show();
                        return;
                    }*/

                    cameraSource.start(svDetectNewDrink.getHolder());


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size()>0){
                    svDetectNewDrink.setVisibility(View.GONE);
                    llDrinkParameters.setVisibility(View.VISIBLE);
                    etAddDrinkBarcode.setText(barcodes.valueAt(0).rawValue);
                    etAddDrinkName.setText(barcodes.valueAt(0).displayValue);
                }
            }
        });
    }

    private void showStep(Step step){
        if(step==Step.ImageAndName){
            toogleEditPartyImageAndName(true);
            toogleEditDrinks(false);
        }
        else if(step==Step.Drinks){
            toogleEditPartyImageAndName(false);
            toogleEditDrinks(true);
        }
    }

    private void toogleEditPartyImageAndName(boolean show){
        int visibility = show?View.VISIBLE:View.GONE;

        ivPartyImage.setVisibility(visibility);
        llPartyImageAndName.setVisibility(visibility);

        lvAddedDrinks.setVisibility(View.GONE);

        if(actionForward!=null){
            actionForward.setVisible(show);
        }

        if(actionFinish!=null){
            actionFinish.setVisible(!show);
        }

        if(actionBack !=null){
            actionBack.setVisible(!show);
        }
    }

    /**
     * Checks if everything is valid for the first step
     * @return
     */
    private boolean validStepPartyImageAndName(){
        if(PartyImage==null){
            Toast.makeText(CreateParty.this, "Hey Dude! You need an image!", Toast.LENGTH_SHORT).show();
            return false;
        }

        PartyName = etPartyName.getText().toString();

        if(PartyName==null||PartyName==""){
            Toast.makeText(CreateParty.this, "Hey Dude! You forget a Party Name! No Name No Game!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void toogleEditDrinks(boolean show){
        int visibility = show?View.VISIBLE:View.GONE;

        lvAddedDrinks.setVisibility(visibility);

        if(actionForward!=null){
            actionForward.setVisible(!show);
        }

        if(actionFinish!=null){
            actionFinish.setVisible(show);
        }


        if(actionBack !=null){
            actionBack.setVisible(show);
        }

        ArrayList<Drink> defaultDrinks = new ArrayList<>();
        defaultDrinks.add(new Drink(1,"a","Füchschen",2.1));
        defaultDrinks.add(new Drink(2,"b","Mühlen Kölsch",2.1));
        defaultDrinks.add(new Drink(3,"c","Köpi",2.1));

        DrinkListViewAdapter drinkListViewAdapter = new DrinkListViewAdapter(this,defaultDrinks);
        lvAddedDrinks.setAdapter(drinkListViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu_create_party, menu);

        actionFinish = menu.findItem(R.id.action_finish);
        actionForward = menu.findItem(R.id.action_forward);
        actionBack = menu.findItem(R.id.action_back);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_forward) {
            if(validStepPartyImageAndName()) {
                showStep(Step.Drinks);
            }
        }
        else if(id==R.id.action_back){
            showStep(Step.ImageAndName);
        }
        else if(id==R.id.action_finish){

        }

        return super.onOptionsItemSelected(item);
    }

    public void SelectImageButtonClicked(View view) {
        //use of https://github.com/esafirm/android-image-picker
        ImagePicker.create(this)
                .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                .folderMode(true) // folder mode (false by default)
                .toolbarFolderTitle("Party Image") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                .single() // single mode
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                .start();

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // get a single image only
            PartyImage = ImagePicker.getFirstImageOrNull(data);

            if(PartyImage!=null) {

                Bitmap myBitmap = BitmapFactory.decodeFile(PartyImage.getPath());

                ivPartyImage.setImageBitmap(myBitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void addNewDrinkButtonClicked(View view) {
    }

    @StringDef({
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
            Camera.Parameters.FOCUS_MODE_AUTO,
            Camera.Parameters.FOCUS_MODE_EDOF,
            Camera.Parameters.FOCUS_MODE_FIXED,
            Camera.Parameters.FOCUS_MODE_INFINITY,
            Camera.Parameters.FOCUS_MODE_MACRO
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface FocusMode {}

    /**
     * <p>
     * Sets the Mobile Vision API provided {@link com.google.android.gms.vision.CameraSource}'s
     * focus mode. Use {@link Camera.Parameters#FOCUS_MODE_CONTINUOUS_PICTURE} or
     * {@link Camera.Parameters#FOCUS_MODE_CONTINUOUS_VIDEO} for continuous autofocus.
     * </p>
     * <p>
     * Note that the CameraSource's {@link CameraSource#start()} or
     * {@link CameraSource#start(SurfaceHolder)} has to be called and the camera image has to be
     * showing prior using this method as the CameraSource only creates the camera after calling
     * one of those methods and the camera is not available immediately. You could implement some
     * kind of a callback method for the SurfaceHolder that notifies you when the imaging is ready
     * or use a direct action (e.g. button press) to set the focus mode.
     * </p>
     * <p>
     * Check out <a href="https://github.com/googlesamples/android-vision/blob/master/face/multi-tracker/app/src/main/java/com/google/android/gms/samples/vision/face/multitracker/ui/camera/CameraSourcePreview.java#L84">CameraSourcePreview.java</a>
     * which contains the method <code>startIfReady()</code> that has the following line:
     * <blockquote><code>mCameraSource.start(mSurfaceView.getHolder());</code></blockquote><br>
     * After this call you can use our <code>cameraFocus(...)</code> method because the camera is ready.
     * </p>
     *
     * @param cameraSource The CameraSource built with {@link com.google.android.gms.vision.CameraSource.Builder}.
     * @param focusMode    The focus mode. See {@link android.hardware.Camera.Parameters} for possible values.
     * @return true if the camera's focus is set; false otherwise.
     * @see com.google.android.gms.vision.CameraSource
     * @see android.hardware.Camera.Parameters
     */
    public static boolean cameraFocus(@NonNull CameraSource cameraSource, @FocusMode @NonNull String focusMode) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();

                        if (!params.getSupportedFocusModes().contains(focusMode)) {
                            return false;
                        }

                        params.setFocusMode(focusMode);
                        camera.setParameters(params);
                        return true;
                    }

                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        return false;
    }
}
