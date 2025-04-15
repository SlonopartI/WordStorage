package net.project.slounik.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import net.project.slounik.ui.translation.TranslationWordsWatcher;
import net.project.slounik.utils.SQL.MultiDatabaseHelper;
import net.project.slounik.utils.SQL.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeMap;

public class FinderThread extends Thread {
    public TextFinder getFinder() {
        return finder;
    }

    public void setFinder(TextFinder finder) {
        this.finder = finder;
    }

    private TextFinder finder;
    public int mode;

    public Context getContext() {
        return context;
    }

    private Context context;

    public FinderThread(TextFinder finder, Context context,int mode){
        this.finder=finder;
        this.context = context;
        this.mode=mode;
    }

    @Override
    public void run() {
        File folder=new File(context.getFilesDir(),"database_backups");
        if(finder.getFileName().lastIndexOf(".")!=-1){
            String databaseName=finder.getFileName().substring(0,finder.getFileName().lastIndexOf("."))+".db.backup";
            File file=new File(folder,databaseName);
            TreeMap<String,ArrayList<String>> map;
            if(isFileExists(context,file.getName())){
                System.err.println("TEST");
                StorageUtils.importDatabase(context,databaseName.substring(0,databaseName.length()-7));
                MultiDatabaseHelper helper=new MultiDatabaseHelper(context,databaseName);
                map=helper.getTranslationsMap(finder.getStr());
            }
            else {
                System.err.println("TEST2 "+databaseName);
                map=finder.findText(context);
            }
            Bundle bundle=new Bundle();
            bundle.putSerializable("TreeMap",map);
            Message msg= TranslationWordsWatcher.handler.obtainMessage();
            msg.setData(bundle);
            msg.arg1=0;
            TranslationWordsWatcher.handler.sendMessage(msg);
        }
    }
    public boolean isFileExists(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        Log.d("FILE_DEBUG", "Проверяем путь: " + file.getAbsolutePath());

        if (file.exists()) {
            Log.d("FILE_DEBUG", "Файл найден");
            return true;
        } else {
            Log.d("FILE_DEBUG", "Файл отсутствует");
            // Попробуйте прочитать через openFileInput (если файл был сохранен этим методом)
            try (InputStream is = context.openFileInput(fileName)) {
                Log.d("FILE_DEBUG", "Файл найден через openFileInput()");
                return true;
            } catch (IOException e) {
                Log.d("FILE_DEBUG", "Файл не найден нигде");
                return false;
            }
        }
    }
}
