package app.example.app.postapi;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    ProgressDialog dialog;
    String entered_email, entered_pwd;
    TextView response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        response = (TextView) findViewById(R.id.res);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pwd);
    }



    public  void login(View v){

        entered_email = email.getText().toString();
        entered_pwd = password.getText().toString();

        if(entered_email.isEmpty() && entered_pwd.isEmpty()){
            Toast.makeText(getApplicationContext(), "Entered valid credential", Toast.LENGTH_LONG).show();
        }else{
            postData input = new postData();
            input.execute(new String[]{"https://www.latrobe.edu.au/asdetect/api/appl/login"});
        }
    }



    public class postData extends AsyncTask<String, Void, String>{

        @Override
        public void onPreExecute(){
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Loading..");
            dialog.setMessage("Please wait");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {


            HttpURLConnection connection = null;
            OutputStreamWriter writer;
            BufferedReader reader = null;
            String result = null;


            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();


                JSONObject objInput = new JSONObject();
                objInput.put("email", entered_email);
                objInput.put("password", entered_pwd);


                writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(objInput.toString());
                writer.close();


                int response_code = connection.getResponseCode();
                if(response_code == HttpURLConnection.HTTP_OK){
                    InputStream input = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));

                    StringBuffer buffer =new StringBuffer();
                    String lines;


                    while((lines = reader.readLine()) != null){
                        buffer.append(lines+"\n");
                    }
                    result = buffer.toString();
                }else{
                    result = "UnAuthorised";
                }

            }catch (Exception e){
                dialog.dismiss();
            }


            return result;
        }


        @Override
        protected void onPostExecute(String values) {
            try {
                JSONObject obj = new JSONObject(values);
                response.setText(obj.getJSONObject("data").getString("token"));
                dialog.dismiss();

            }catch (Exception e){

            }
        }

    }
}
