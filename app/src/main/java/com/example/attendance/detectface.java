package com.example.attendance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.attendance.Helper.GraphicOverlay;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class detectface extends AppCompatActivity {

    Button faceDetectButton;
    GraphicOverlay graphicOverlay;
    CameraView cameraView;
    android.app.AlertDialog alertDialog;
    FusedLocationProviderClient fusedLocationProviderClient;
    //NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext())
    float Lati;
    float Longi;

    TextView textView;

    public void onobj(String detectFaceFlag,Bitmap bitmap) {

        Toast.makeText(detectface.this, "Success camera " + detectFaceFlag, Toast.LENGTH_SHORT).show();

        long timestamp = System.currentTimeMillis();
        System.out.println("TimeStamp:::: " + timestamp);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                Lati = (float) location.getLatitude();
                System.out.println("Latitude:::" + Lati);
                Longi = (float) location.getLongitude();
                System.out.println("Longitude:::" + Longi);
            }
        });

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //System.out.println("Encoded : "+encoded);


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.29.179:5000/addCheckIn/";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", "1");
            jsonObject.put("Latitude", Lati);
            jsonObject.put("Longitude", Longi);
            jsonObject.put("TimeStamp",timestamp);
            jsonObject.put("Image",encoded);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //      Request a string response from the provided URL.
        System.out.println("Response"+ jsonObject);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String res = response.getString("comment");
                            //notificationHelper.sendHighPriorityNotification("Success","",detectface.class);
                            Toast.makeText(detectface.this, res.toString(), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(detectface.this, "That didn't work!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detectface);


        faceDetectButton=findViewById(R.id.detect_face_btn);
        graphicOverlay=findViewById(R.id.graphic_overlay);
        cameraView=findViewById(R.id.camera_view);
        cameraView.toggleFacing();
        alertDialog=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait")
                .setCancelable(false)
                .build();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
//        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                Location location = task.getResult();
//                Lati = (float) location.getLatitude();
//                System.out.println("Latitude:::" + Lati);
//                Longi = (float) location.getLongitude();
//                System.out.println("Longitude:::" + Longi);
//            }
//        });
        faceDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(detectface.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    cameraView.start();
                    cameraView.captureImage();
                    graphicOverlay.clear();
                }else {
                    ActivityCompat.requestPermissions(detectface.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }

            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @SuppressLint("MissingPermission")
            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                alertDialog.show();
                Bitmap bitmap=cameraKitImage.getBitmap();
                bitmap=Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                byte[] byteArray = byteArrayOutputStream .toByteArray();
                processFacedetection(bitmap);


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }


    private void processFacedetection(Bitmap bitmap) {


        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions=new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector= FirebaseVision.getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
//                for(FirebaseVisionFace face : firebaseVisionFaces) {
//                    Rect rect = face.getBoundingBox();
//                    float rotY = face.getHeadEulerAngleY();
//                    float rotZ = face.getHeadEulerAngleZ();
//                }

                onobj("True",bitmap);
                alertDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(detectface.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                //onobj("False");
            }
        });
    }

//    private void getFaceResults(List<FirebaseVisionFace> firebaseVisionFaces) {
//        int counter=0;
//        for(FirebaseVisionFace face : firebaseVisionFaces)
//        {
//            Rect rect=face.getBoundingBox();
//            float rotY=face.getHeadEulerAngleY();
//            float rotZ=face.getHeadEulerAngleZ();
//
//
//
//            //EYE
//            List<FirebaseVisionPoint> lefteyecontour=face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
//            System.out.println("Left eye Contour"+lefteyecontour);
//            List<FirebaseVisionPoint> righteyecontour=face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).getPoints();
//            System.out.println("Right eye Contour"+righteyecontour);
//
//            if(face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY)
//            {
//                float rightEyeOpenProbability=face.getRightEyeOpenProbability();
//                System.out.println("Right eye"+rightEyeOpenProbability);
//            }
//            if(face.getLeftEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY)
//            {
//                float leftEyeOpenProbability=face.getLeftEyeOpenProbability();
//                System.out.println("Left eye"+leftEyeOpenProbability);
//            }
//
//            float earl,earr;
//            float r1= (float) Math.sqrt(Math.pow((righteyecontour.get(15).getY() - righteyecontour.get(1).getY()),2) + Math.pow((righteyecontour.get(15).getX() - righteyecontour.get(1).getX()),2));
//            float r2= (float) Math.sqrt(Math.pow((righteyecontour.get(14).getY() - righteyecontour.get(2).getY()),2) + Math.pow((righteyecontour.get(14).getX() - righteyecontour.get(2).getX()),2));
//            float r3= (float) Math.sqrt(Math.pow((righteyecontour.get(13).getY() - righteyecontour.get(3).getY()),2) + Math.pow((righteyecontour.get(13).getX() - righteyecontour.get(3).getX()),2));
//            float r4= (float) Math.sqrt(Math.pow((righteyecontour.get(12).getY() - righteyecontour.get(4).getY()),2) + Math.pow((righteyecontour.get(12).getX() - righteyecontour.get(4).getX()),2));
//            float r5= (float) Math.sqrt(Math.pow((righteyecontour.get(11).getY() - righteyecontour.get(5).getY()),2) + Math.pow((righteyecontour.get(11).getX() - righteyecontour.get(5).getX()),2));
//            float r6= (float) Math.sqrt(Math.pow((righteyecontour.get(10).getY() - righteyecontour.get(6).getY()),2) + Math.pow((righteyecontour.get(10).getX() - righteyecontour.get(6).getX()),2));
//            float r7= (float) Math.sqrt(Math.pow((righteyecontour.get(9).getY() - righteyecontour.get(7).getY()),2) + Math.pow((righteyecontour.get(9).getX() - righteyecontour.get(7).getX()),2));
//            float r8= (float) Math.sqrt(Math.pow((righteyecontour.get(0).getY() - righteyecontour.get(8).getY()),2) + Math.pow((righteyecontour.get(0).getX() - righteyecontour.get(8).getX()),2));
//
//            earr=(r1+r2+r3+r4+r5+r6+r7)/(2*r8);
//            System.out.println("Right Eye Aspect Ratio = "+earr);
//
//            float l1= (float) Math.sqrt(Math.pow((lefteyecontour.get(15).getY() - lefteyecontour.get(1).getY()),2) + Math.pow((lefteyecontour.get(15).getX() - lefteyecontour.get(1).getX()),2));
//            float l2= (float) Math.sqrt(Math.pow((lefteyecontour.get(14).getY() - lefteyecontour.get(2).getY()),2) + Math.pow((lefteyecontour.get(14).getX() - lefteyecontour.get(2).getX()),2));
//            float l3= (float) Math.sqrt(Math.pow((lefteyecontour.get(13).getY() - lefteyecontour.get(3).getY()),2) + Math.pow((lefteyecontour.get(13).getX() - lefteyecontour.get(3).getX()),2));
//            float l4= (float) Math.sqrt(Math.pow((lefteyecontour.get(12).getY() - lefteyecontour.get(4).getY()),2) + Math.pow((lefteyecontour.get(12).getX() - lefteyecontour.get(4).getX()),2));
//            float l5= (float) Math.sqrt(Math.pow((lefteyecontour.get(11).getY() - lefteyecontour.get(5).getY()),2) + Math.pow((lefteyecontour.get(11).getX() - lefteyecontour.get(5).getX()),2));
//            float l6= (float) Math.sqrt(Math.pow((lefteyecontour.get(10).getY() - lefteyecontour.get(6).getY()),2) + Math.pow((lefteyecontour.get(10).getX() - lefteyecontour.get(6).getX()),2));
//            float l7= (float) Math.sqrt(Math.pow((lefteyecontour.get(9).getY() - lefteyecontour.get(7).getY()),2) + Math.pow((lefteyecontour.get(9).getX() - lefteyecontour.get(7).getX()),2));
//            float l8= (float) Math.sqrt(Math.pow((lefteyecontour.get(0).getY() - lefteyecontour.get(8).getY()),2) + Math.pow((lefteyecontour.get(0).getX() - lefteyecontour.get(8).getX()),2));
//
//            earl=(l1+l2+l3+l4+l5+l6+l7)/(2*l8);
//            System.out.println("Left Eye Aspect Ratio = "+earl);
//
//            //MOUTH
//
//            List<FirebaseVisionPoint> upperLiptopContour=face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).getPoints();
//            System.out.println("Upper Lip Top"+upperLiptopContour);
//            List<FirebaseVisionPoint> upperLipBottomContour=face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();
//            System.out.println("Upper Lip Bottom"+upperLipBottomContour);
//
//            List<FirebaseVisionPoint> lowerLiptopContour=face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP).getPoints();
//            System.out.println("Lower Lip Top"+lowerLiptopContour);
//            List<FirebaseVisionPoint> lowerLipBottomContour=face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).getPoints();
//            System.out.println("Lower Lip Bottom"+lowerLipBottomContour);
//
//            if(face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY)
//            {
//                float smileProb=face.getSmilingProbability();
//                System.out.println("Smile Probability"+smileProb);
//            }
//
//
//            //EAR
//            FirebaseVisionFaceLandmark leftear=face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
//            if(leftear != null)
//            {
//                FirebaseVisionPoint leftEarPos=leftear.getPosition();
//                System.out.println("Left Ear Position"+leftEarPos);
//            }
//
//            FirebaseVisionFaceLandmark rightear=face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);
//            if(leftear != null)
//            {
//                FirebaseVisionPoint rightEarPos=rightear.getPosition();
//                System.out.println("Right Ear Position"+rightEarPos);
//            }
//
//            if(face.getTrackingId() != FirebaseVisionFace.INVALID_ID)
//            {
//                int id=face.getTrackingId();
//            }
//
//            RectOverlay rectOverlay=new RectOverlay(graphicOverlay,rect);
//            graphicOverlay.add(rectOverlay);
//            counter =counter+1;
//        }
//        alertDialog.dismiss();
//    }
}