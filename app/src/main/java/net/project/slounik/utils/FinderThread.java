package net.project.slounik.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import net.project.slounik.ui.translation.TranslationWordsWatcher;
import net.project.slounik.utils.SQL.MultiDatabaseHelper;
import net.project.slounik.utils.SQL.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class FinderThread extends Thread {
    public TreeMap<String,ArrayList<String>> map;

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
        String databaseName=finder.getFileName().substring(0,finder.getFileName().lastIndexOf(".")==-1?0:finder.getFileName().lastIndexOf("."))+".db";
        File folder=context.getDatabasePath(databaseName);
        if(finder.getFileName().lastIndexOf(".")!=-1){
            map = new TreeMap<>(new ComparatorForMap(finder.getStr()));
            if(folder.exists()){
                Thread thread=new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        MultiDatabaseHelper helper=new MultiDatabaseHelper(context, databaseName);
                        TreeMap<String,ArrayList<String>> tempMap;
                        tempMap=helper.getTranslationsMap(finder.getStr());
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("TreeMap", tempMap);
                        Message msg= TranslationWordsWatcher.handler2.obtainMessage();
                        msg.setData(bundle);
                        msg.arg1=0;
                        TranslationWordsWatcher.handler2.sendMessage(msg);
                    }
                };
                thread.start();

            }
            else {
                map=finder.findText(context);
                Bundle bundle=new Bundle();
                bundle.putSerializable("TreeMap", map);
                Message msg= TranslationWordsWatcher.handler.obtainMessage();
                msg.setData(bundle);
                msg.arg1=0;
                TranslationWordsWatcher.handler.sendMessage(msg);
            }
        }
    }
}
