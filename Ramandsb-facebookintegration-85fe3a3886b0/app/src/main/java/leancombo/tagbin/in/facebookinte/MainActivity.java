package leancombo.tagbin.in.facebookinte;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.facebook.share.widget.ShareDialog;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    LoginButton login;
    CallbackManager callbackManager;
    String text;
    final int DIALOG_LOADING = 0;
    String url = "https://graph.facebook.com/me/feed";
    String accestoken;
    TextView tvname;
    TextView tvmyname;
    TextView tvemail;
    TextView tvmyemail, statustv, title;
    ImageView view;
    int i=0;
    View textwrapper;
    boolean bool = false;
    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    Button logout;
    Toolbar toolbar;
    View pad;
    boolean aBoolean=false;
   public static boolean checkin=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        tvname = (TextView) findViewById(R.id.name);
        tvmyname = (TextView) findViewById(R.id.myname);
        tvmyemail = (TextView) findViewById(R.id.myemail);
        statustv = (TextView) findViewById(R.id.statustv);
        title = (TextView) findViewById(R.id.title);
        view = (ImageView) findViewById(R.id.view);
        textwrapper = findViewById(R.id.textwrap);
        textwrapper.setVisibility(View.INVISIBLE);
        logout = (Button) findViewById(R.id.logout);
        logout.setVisibility(View.INVISIBLE);
        view.setImageDrawable(getResources().getDrawable(R.drawable.kohler));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);
        pad = findViewById(R.id.pad);
        pad.setVisibility(View.VISIBLE);
        toolbar.setTitle("");
        mVolleySingleton = VolleySingleton.getInstance();
        mImageLoader = mVolleySingleton.getImageLoader();
        callbackManager = CallbackManager.Factory.create();
        login = (LoginButton) findViewById(R.id.login_button);
        login.setReadPermissions("public_profile email publish_actions");
        if (aBoolean){
            login.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            pad.setVisibility(View.GONE);
            toolbar.setTitle("");
        }else {
            textwrapper.setVisibility(View.INVISIBLE);
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            view.setImageDrawable(getResources().getDrawable(R.drawable.kohler));
            statustv.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            pad.setVisibility(View.VISIBLE);
        }


        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (AccessToken.getCurrentAccessToken() != null) {
                    bool = true;
                    textwrapper.setVisibility(View.VISIBLE);
                    Log.d("loginreslut", loginResult.getAccessToken().getToken());

                    accestoken = loginResult.getAccessToken().getToken();
                    RequestData();
                    aBoolean=true;
                    login.setVisibility(View.INVISIBLE);
                    logout.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    pad.setVisibility(View.GONE);
                    toolbar.setTitle("");

                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login Canceled", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onError(FacebookException exception) {

            }
        });


//        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//            @Override
//            public void onSuccess(Sharer.Result result) {
//
//                Log.d("success", result.toString());
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d("error", error.toString());
//
//            }
//        });
//        if (shareDialog.canShow(ShareLinkContent.class)) {
//
//            ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                    .setContentTitle("Testing title")
//                    .setContentDescription("testing description")
//                    .setContentUrl(Uri.parse("https://goo.gl/UVsKF3"))
//                    .build();
//
//            shareDialog.show(linkContent);
//        }
//


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_LOADING:
                final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.loading);
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //onBackPressed();
                    }
                });
                return dialog;

            default:
                return null;
        }
    }

    ;

    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        text = "<b>Name :</b> " + json.getString("name") + "<br><br><b>Email :</b> " + json.getString("email") + "<br><br><b>Profile link :</b> " + json.getString("link");


                        String id = json.getString("id");
                        String name = json.getString("name");
                        String link = json.getString("link");
                        String email = json.getString("email");
                        JSONObject picture = json.getJSONObject("picture");
                        JSONObject data = picture.getJSONObject("data");
                        String url = data.getString("url");
                        Log.d("Details", name + "\n" + link + "\n" + email + "\n" + url + "\n" + id);
                        Log.d("GraphResponse", response.toString());
                        Log.d("JSONObject", object.toString());
                        setData(id, name, link, email, url);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();

    }


    public void click(View v) {
        makeRequest();

    }


    public void logout() {
        LoginManager.getInstance().logOut();
        aBoolean=false;
        textwrapper.setVisibility(View.INVISIBLE);
        login.setVisibility(View.VISIBLE);
        logout.setVisibility(View.GONE);
        view.setImageDrawable(getResources().getDrawable(R.drawable.kohler));
        statustv.setVisibility(View.VISIBLE);
        statustv.setText("Successfully Checked in and Logged out");
        toolbar.setVisibility(View.GONE);
        pad.setVisibility(View.VISIBLE);
    }

    private void makeRequest() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        logout();
                        Intent intent = new Intent(MainActivity.this,CouponAct.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();
                        Log.v("writtem:%n %s", response.toString());

                        checkin=true;
                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("error:%n %s", error.toString());
                        Toast.makeText(MainActivity.this, "Permission Denied !!", Toast.LENGTH_LONG);
                        checkin=false;
                        logout();
                        Intent intent = new Intent(MainActivity.this,CouponAct.class);
                        startActivity(intent);
                        finish();
                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", accestoken);
//                params.put("message","hi hello message");
//                params.put("link", "http://tagbin.in/");
                params.put("place", "106487939387579");

                return params;
            }


        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
//        requestQueue.cancelAll(tag);

    }


    public void setData(String id, String name, String link, String email, String url) {
        String myid = id;
        String myname = name;
        String mylink = link;
        String myemail = email;
        String myurl = url;

        loadImages(url);
        tvmyname.setText(myname);
        tvmyemail.setText(myemail);
        title.setText(myname);


    }

    private void loadImages(final String urlThumbnail) {

        mImageLoader.get(urlThumbnail, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                view.setImageBitmap(response.getBitmap());
                BitmapDrawable icon = new BitmapDrawable(getResources(), response.getBitmap());
                toolbar.setLogo(icon);
//                holder.imageView.setImageUrl(urlThumbnail, mImageLoader);
                Log.d("image recieved", response.toString());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("image error", error.toString());


            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
//        LoginManager.getInstance().logOut();
//        textwrapper.setVisibility(View.INVISIBLE);
//        login.setVisibility(View.VISIBLE);
//        logout.setVisibility(View.GONE);
//        view.setImageDrawable(getResources().getDrawable(R.drawable.tagbin_logo));
//        statustv.setVisibility(View.INVISIBLE);
//        toolbar.setVisibility(View.GONE);
//        pad.setVisibility(View.VISIBLE);
    }
}
