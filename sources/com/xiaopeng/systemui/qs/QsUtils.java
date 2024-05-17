package com.xiaopeng.systemui.qs;

import android.os.Environment;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
/* loaded from: classes24.dex */
public class QsUtils {
    public static final String TAG = "QsUtils";

    public static QsPanelSetting loadQsSettingFromJson() {
        QsPanelSetting qsPanelSetting = new QsPanelSetting();
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("D:\\SystemUI_8155\\QsSettings\\E38functions.json", new String[0]));
            qsPanelSetting = (QsPanelSetting) gson.fromJson(reader, (Class<Object>) QsPanelSetting.class);
            reader.close();
            return qsPanelSetting;
        } catch (Exception ex) {
            ex.printStackTrace();
            return qsPanelSetting;
        }
    }

    public static ArrayList<ArrayList<TileState>> loadVehicleTileStateListFromJson() {
        String filePath = Environment.getRootDirectory() + "/etc/xuiservice/systemui/qs_panel_functions.json";
        ArrayList<ArrayList<TileState>> tileStateArrayList = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            Log.i(TAG, "loadVehicleTileStateListFromJson: filePath: " + filePath + " == null");
        }
        try {
            Gson gson = new Gson();
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    stringBuilder.append(line);
                } else {
                    bufferedReader.close();
                    inputStreamReader.close();
                    tileStateArrayList = (ArrayList) gson.fromJson(stringBuilder.toString(), new TypeToken<ArrayList<ArrayList<TileState>>>() { // from class: com.xiaopeng.systemui.qs.QsUtils.1
                    }.getType());
                    bufferedReader.close();
                    return tileStateArrayList;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return tileStateArrayList;
        }
    }
}
