package ru.cardiacare.cardiacare.idt_ecg;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.cardiacare.cardiacare.MainActivity;

//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.Credentials;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;

/* Отправка данных ЭКГ на сервер */

public class ECGPost extends AsyncTask<JSONObject, String, String> {

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8"); //MediaType.parse("multipart/form-data; boundary=------------------------dead");

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject... params) {

        try {
            String dirPath = "/";
            File fileq = MainActivity.mContext.getFilesDir();//new File(dirPath);
            File[] files = fileq.listFiles();
//            for (int i = 0; i < files.length; i++)
//                System.out.println("Test! files " + files[i].toString());

            OkHttpClient client = new OkHttpClient();

//            File file = new File("feedback.json"); // MainActivity.storage.getECGFile() //questionnaire.json

//            File file = new File(MainActivity.mContext.getFilesDir(), MainActivity.storage.getECGFile());
//            File file = new File(MainActivity.mContext.getFilesDir(), "ecgfile1");
//            File file = new File(MainActivity.mContext.getFilesDir(), "lbp.txt");

            // Проверяем, есть ли в массиве ECGService.ecgFiles неотправленные файлы
            // Если они есть, то отправляем их все
//            Log.i("ECGPost", "ecgFiles.size() = " + ECGService.ecgFiles.size());
            int ecgFilesSize = ECGService.ecgFiles.size();
            for (int i = 0; i < ecgFilesSize; i++) {
//                Log.i("ECGPost", "ecgFiles.getFirst() = " + ECGService.ecgFiles.getFirst());
                File file = new File(MainActivity.mContext.getFilesDir(), ECGService.ecgFiles.getFirst());

                System.out.println("Test! file " + file.toString());

                RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        //.addFormDataPart("title", "Square Logo")
                        .addFormDataPart("data", "ecgfile",
                                RequestBody.create(MediaType.parse("text/plain"), file))
                        .build();

//                System.out.println("Test! body " + body.toString());

                String credential = Credentials.basic(MainActivity.storage.getAccountToken(), "");

//                System.out.println("Test! token " + credential);

                Request request = new Request.Builder()
                        .url("http://api.cardiacare.ru/biosignals")
                        .addHeader("Authorization", credential)
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Accept", "*/*")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        // .addHeader("Content-Type", "multipart/form-data")
                        //.post(body)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

//                System.out.println("Test! response " + response.body().string());

//                System.out.println("Test! request " + request.body().toString());

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Log.e("Request", request.body().toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("Response", response.body().string());
                    }

//                @Override
//                public void onFailure(Request request, IOException e) {
//                    Log.e("Request", request.body().toString());
//                }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//                    Log.i("Response", response.body().string());
//                }
                });

//                Log.i("ECGPost", "ecgFiles до удаления отправленого = " + ECGService.ecgFiles.toString());
                ECGService.ecgFiles.removeFirst();
                file.delete();
//                Log.i("ECGPost", "ecgFiles после удаления отправленого = " + ECGService.ecgFiles.toString());
                MainActivity.storage.setECGFile("");
                System.out.println("Test! POST");
            }
        } catch (Exception e) {
            System.out.println("Test! exc " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
