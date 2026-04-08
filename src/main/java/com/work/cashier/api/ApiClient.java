package com.work.cashier.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.cashier.data_transfert_object.employee.EmployeeDTO;
import com.work.cashier.notifications.NotificationType;
import com.work.cashier.notifications.NotificationsBuilder;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    public static <T> List<T> getAll(String url, Class<T> dynamic_class)
    {
        List<T> list = new ArrayList<>();
        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3)) // Réessaye 3 fois
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            body = body.trim();

            if (body.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(body);
                ObjectMapper mapper = new ObjectMapper();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    T obj = mapper.readValue(jsonObj.toString(), dynamic_class);
                    list.add(obj);
                }

            }else if (body.startsWith("{")) {
                System.out.println("Objet JSON unique : " + body);
                JSONObject jsonObj = new JSONObject(body);

                if (jsonObj.has("error")) {
                    String errorText = jsonObj.getString("error");
                    if(!jsonObj.getString("error").contains("Gap")) {
                        System.out.println("⚠ Erreur API détectée : " + errorText);
                        NotificationsBuilder.create(NotificationType.ERROR, errorText);
                    }
                }

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                T obj = mapper.readValue(jsonObj.toString(), dynamic_class);
                list.add(obj);
            }
            else {
                System.out.println("Contenu JSON invalide : " + body);
            }

        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(dynamic_class);
            logger.error("Erreur lors de l'appel à l'API : {}", e.getMessage(), e);
        }

        return list;
    }

    public static <T> ApiResult<T> getSalaries(String url, Class<T> dynamic_class) {
        List<T> list = new ArrayList<>();
        int total = 0;

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string().trim();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            if (body.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(body);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    T obj = mapper.readValue(jsonObj.toString(), dynamic_class);
                    list.add(obj);
                }
                total = list.size();
            }
            else if (body.startsWith("{")) {
                JSONObject jsonObj = new JSONObject(body);

                if (jsonObj.has("error")) {
                    System.out.println("⚠ Erreur API détectée : " + jsonObj.getString("error"));
                    NotificationsBuilder.create(NotificationType.ERROR, jsonObj.getString("error"));
                    return new ApiResult<>(0, list);
                }

                if (jsonObj.has("salaries")) {
                    total = jsonObj.optInt("total", 0);

                    JSONArray arr = jsonObj.getJSONArray("salaries");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject salaryObj = arr.getJSONObject(i);
                        T obj = mapper.readValue(salaryObj.toString(), dynamic_class);
                        list.add(obj);
                    }
                } else {
                    T obj = mapper.readValue(jsonObj.toString(), dynamic_class);
                    list.add(obj);
                    total = list.size();
                }
            } else {
                System.out.println("Contenu JSON invalide : " + body);
            }

        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(dynamic_class);
            logger.error("Erreur lors de l'appel à l'API : {}", e.getMessage(), e);
        }

        return new ApiResult<>(total, list);
    }


    public static <T> T getOneEntity(String url, Class<T> dynamicClass)
    {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string().trim();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            if (body.startsWith("{")) {
                JSONObject jsonObj = new JSONObject(body);

                if (jsonObj.has("error")) {
                    if(!jsonObj.getString("error").contains("Gap")) {
                        System.out.println("⚠ Erreur API détectée : " + jsonObj.getString("error"));
                        NotificationsBuilder.create(NotificationType.ERROR, jsonObj.getString("error"));
                    }
                    return null;
                }
                return mapper.readValue(jsonObj.toString(), dynamicClass);

            } else if (body.startsWith("[")) {
                System.out.println("[]");
                JSONArray jsonArray = new JSONArray(body);
                if (!jsonArray.isEmpty()) {
                    JSONObject jsonObj = jsonArray.getJSONObject(0);
                    return mapper.readValue(jsonObj.toString(), dynamicClass);
                } else {
                    System.out.println("⚠ Tableau JSON vide.");
                    return null;
                }

            } else {
                System.out.println("⚠ Contenu JSON invalide : " + body);
                return null;
            }
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(dynamicClass);
            logger.error("Erreur lors de l'appel à l'API : {}", e.getMessage(), e);
            return null;
        }
    }

    public static ArrayList<String> getListString(String url)
    {
        ArrayList<String> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String body = response.body().string().trim();

            if (body.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(body);

                for (int i = 0; i < jsonArray.length(); i++) {
                    String value = jsonArray.getString(i);
                    list.add(value);
                }

            } else if (body.startsWith("{")) {
                System.out.println("Objet JSON unique : " + body);

            } else {
                System.out.println("Contenu JSON invalide : " + body);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel API", e);
        }

        return list;
    }

    public static String getString(String url)
    {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3)) // Réessaye 3 fois
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            return response.body().string().trim();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel API", e);
        }

    }

    public static void insert(String url, Object dynamic_Class)
    {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3)) // Réessaye 3 fois
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        ObjectMapper mapper = new ObjectMapper();

        try {

            String json = mapper.writeValueAsString(dynamic_Class);

            RequestBody body = RequestBody.create(
                    json,
                    MediaType.parse("application/json")
            );
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    NotificationsBuilder.create(NotificationType.SUCCESS,"Insertion avec succés");
                } else {
                    String responseBody = response.body().string();
                    JSONObject error = new JSONObject(responseBody);
                    String message = error.getString("error");
                    NotificationsBuilder.create(NotificationType.ERROR,message);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel API", e);
        }

    }

    public static void insertMultipart(String url, EmployeeDTO employee, File imageFile) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        try {
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("firstName", employee.getFirstName())
                    .addFormDataPart("lastName", employee.getLastName())
                    .addFormDataPart("phoneNumber", employee.getPhoneNumber())
                    .addFormDataPart("password", employee.getPassword())
                    .addFormDataPart("cin", employee.getCin())
                    .addFormDataPart("daily", String.valueOf(employee.getDaily()))
                    .addFormDataPart("monthly", String.valueOf(employee.getMonthly()))
                    .addFormDataPart("job", String.valueOf(employee.getJob()))
                    .addFormDataPart("address", employee.getAddress());

            if (imageFile != null && imageFile.exists()) {
                multipartBuilder.addFormDataPart(
                        "image",
                        imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/jpeg"))
                );
            }

            RequestBody requestBody = multipartBuilder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    NotificationsBuilder.create(NotificationType.SUCCESS, "Insertion avec succès");
                } else {
                    String responseBody = response.body().string();
                    JSONObject error = new JSONObject(responseBody);
                    String message = error.optString("error", "Erreur inconnue");
                    NotificationsBuilder.create(NotificationType.ERROR, message);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel API", e);
        }

    }

    public static void addImage(String url, String id, File imageFile) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        try {
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", id);

            if (imageFile != null && imageFile.exists()) {
                multipartBuilder.addFormDataPart(
                        "image",
                        imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/jpeg"))
                );
            }

            RequestBody requestBody = multipartBuilder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .put(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    NotificationsBuilder.create(NotificationType.SUCCESS, "Insertion avec succès");
                } else {
                    String responseBody = response.body().string();
                    JSONObject error = new JSONObject(responseBody);
                    String message = error.optString("error", "Erreur inconnue");
                    NotificationsBuilder.create(NotificationType.ERROR, message);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Erreur lors de l'appel API", e);
        }

    }

    public static <T extends HasId> T getDataById(List<T> list, long selectedId) {
        return list.stream()
                .filter(item -> item.getId() == selectedId)
                .findFirst()
                .orElse(null);
    }

    public static boolean update(String url,Object dynamic_Class) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3)) // Réessaye 3 fois
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(dynamic_Class);

            RequestBody body = RequestBody.create(
                    json,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    NotificationsBuilder.create(NotificationType.SUCCESS,"Mis à jour avec succés");
                    return true;
                } else {
                    JSONObject error = new JSONObject(responseBody);
                    String message = error.getString("error");
                    NotificationsBuilder.create(NotificationType.ERROR,message);

                    return false;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel API", e);
        }

    }

    public static boolean delete(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3)) // Réessaye 3 fois
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                //System.out.println("✅ Année supprimée avec succès !");
                NotificationsBuilder.create(NotificationType.SUCCESS,"Suppression avec succés");
                return true;
            } else {
                NotificationsBuilder.create(NotificationType.INVALID_ACTION,"Erreur de suppression");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel API", e);
        }
        return false;
    }

}
