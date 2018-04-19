package org.mozilla.focus.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.focus.history.model.Site;
import org.mozilla.focus.home.HomeFragment;
import org.mozilla.focus.locale.Locales;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by mozillabeijing on 2017/12/14.
 */

public class HotListUtils {
    public static JSONArray getHotSitesJsonArrayFromAssets(Context context) {
        JSONArray obj = null;
        try {
            obj = new JSONArray(loadHotSitesFromAssets(context));
            long lastViewTimestampSystem = System.currentTimeMillis();
            for (int i = 0 ; i < obj.length(); i++) {
                ((JSONObject)obj.get(i)).put("lastViewTimestamp",lastViewTimestampSystem);
            }
            //saveDefaultSites(context, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static String loadHotSitesFromAssets(Context context) {
        String json = "[]";
        try {
            final Locale locale = Locale.getDefault();
            final String fileName = "hotlist.json";
            final String localPath = "topsites/" + Locales.getLanguage(locale);
            final String defaultPath = "topsites/hotlist_default.json";

            final AssetManager assetManager = context.getAssets();
            final List<String> localFiles = Arrays.asList(assetManager.list(localPath));

            InputStream is;
            if (localFiles.contains(fileName)) {
                is = assetManager.open(localPath+"/"+fileName);
            } else {
                is = assetManager.open(defaultPath);
            }

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            return json;
        }
    }

    /*public static void saveDefaultSites(Context context, JSONArray obj) {
        if (context == null) {
            return;
        }
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(HomeFragment.TOPSITES_PREF, obj.toString())
                .apply();
    }*/

    public static List<Site> paresJsonToList(Context context, JSONArray jsonArray) {
        List<Site> defaultSites = new ArrayList<>();
        try {
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json_site = (JSONObject)jsonArray.get(i);
                    final Site site = new Site();
                    site.setId(json_site.getLong("id"));
                    site.setUrl(json_site.getString("url"));
                    site.setTitle(json_site.getString("title"));
                    site.setViewCount(json_site.getLong("viewCount"));
                    site.setLastViewTimestamp(json_site.getLong("lastViewTimestamp"));
                    String icon_name = json_site.getString("favicon");
                    site.setFavIconUri("//android_asset/topsites/icon/"+icon_name);
                    defaultSites.add(site);
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return defaultSites;
        }
    }
}