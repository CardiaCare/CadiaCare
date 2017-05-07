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
            File fileq = MainActivity.mContext.getFilesDir();//new AttachFile(dirPath);
            File[] files = fileq.listFiles();
//            for (int i = 0; i < files.length; i++)
//                System.out.println("Test! files " + files[i].toString());

            OkHttpClient client = new OkHttpClient();

//            AttachFile file = new AttachFile("feedback.json"); // MainActivity.storage.getECGFile() //questionnaire.json

//            AttachFile file = new AttachFile(MainActivity.mContext.getFilesDir(), MainActivity.storage.getECGFile());
//            AttachFile file = new AttachFile(MainActivity.mContext.getFilesDir(), "ecgfile1");
//            AttachFile file = new AttachFile(MainActivity.mContext.getFilesDir(), "lbp.txt");

            // Проверяем, есть ли в массиве ECGService.ecgFiles неотправленные файлы
            // Если они есть, то отправляем их все
            int ecgFilesSize;
            if (ECGService.connected_flag) {
                ecgFilesSize = ECGService.ecgFiles.size() - 1;
            } else {
                ecgFilesSize = ECGService.ecgFiles.size();
            }

            for (int i = 0; i < ecgFilesSize; i++) {
                File file = new File(MainActivity.mContext.getFilesDir(), ECGService.ecgFiles.getFirst());

//                try {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(
//                            ECGService.mContext.openFileInput(ECGService.ecgFiles.getFirst())));
//                    String str = "";
//                    while ((str = br.readLine()) != null) {
//                        Log.d("ECGService", ECGService.ecgFiles.getFirst() + " = " + str);
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                System.out.println("Test! file " + file.toString());

                RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        //.addFormDataPart("title", "Square Logo")
                        .addFormDataPart("data", "ecgfile",
                                RequestBody.create(MediaType.parse("text/plain"), file))
                        .build();

                String credential = Credentials.basic(MainActivity.storage.getAccountToken(), "");


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

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
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

                ECGService.ecgFiles.removeFirst();
                file.delete();
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
