package com.example.milfittracker.repo;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

public class StandardsRepo {
    private final Context context;
    private final Gson gson = new Gson();

    public StandardsRepo(Context context) {
        this.context = context;
    }

    public Map<String, Map<String, Map<String, List<StandardsTable.StandardEntry>>>> loadStandards(String branch) {
        try {
            String fileName = branch + ".json";
            InputStream is = context.getAssets().open(fileName);
            return gson.fromJson(new InputStreamReader(is),
                    new TypeToken<Map<String, Map<String, Map<String, List<StandardsTable.StandardEntry>>>>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
