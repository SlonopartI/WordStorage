package net.project.slounik.ui.translation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.project.slounik.utils.ComparatorForMap;
import net.project.slounik.utils.FinderThread;
import net.project.slounik.utils.RecyclerViewAdapter;
import net.project.slounik.utils.TextFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TranslationWordsWatcher implements TextWatcher {
    private static RecyclerView view;
    public static TreeMap<String,ArrayList<String>> mapMain;
    public static CopyOnWriteArrayList<FinderThread> threads;
    public static FindingHandler handler;
    public static MapHandler handler2;
    public static int mode=0;
    private TextFinder finder;
    public TranslationWordsWatcher(){
        this.finder=new TextFinder();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mapMain=new TreeMap<String,ArrayList<String>>(new ComparatorForMap(s.toString()));
        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        if(threads==null){
            threads=new CopyOnWriteArrayList<>();
            if(handler==null)handler=new FindingHandler();
            if(handler2==null)handler2=new MapHandler();
            finder.setStr(s.toString());
            if(new File(view.getContext().getFilesDir(),"userTranslations.txt").exists()){
                TextFinder finder2=new TextFinder("userTranslations.txt",finder.getStr());
                FinderThread thread=new FinderThread(finder2, view.getContext(),mode);
                threads.add(thread);
                thread.start();
            }
            switch (mode){
                case(0):{
                    finder.setFileName("BaseDictionary.txt");
                    FinderThread mainThread=new FinderThread(finder, view.getContext(),mode);
                    threads.add(mainThread);
                    mainThread.start();
                    FinderThread thread=new FinderThread(new TextFinder("3словарь.txt",s.toString()),view.getContext(),mode);
                    threads.add(thread);
                    thread.start();
                    FinderThread thread2=new FinderThread(new TextFinder("2словарь.txt",s.toString()),view.getContext(),mode);
                    threads.add(thread2);
                    thread2.start();
                    break;
                }
                case(1):{
                    finder.setFileName("рус_бел.txt");
                    FinderThread thread3=new FinderThread(finder,view.getContext(),mode);
                    threads.add(thread3);
                    thread3.start();
                    FinderThread thread4=new FinderThread(new TextFinder("рус_бел2.txt",s.toString()),view.getContext(),mode);
                    threads.add(thread4);
                    thread4.start();
                    FinderThread thread5=new FinderThread(new TextFinder("рус_бел3.txt",s.toString()),view.getContext(),mode);
                    threads.add(thread5);
                    thread5.start();
                    break;
                }
            }
            for(String filename:view.getContext().fileList()){
                if((!filename.endsWith(".db")||!filename.endsWith(".db.backup"))) {
                    FinderThread thread = new FinderThread(new TextFinder(filename, s.toString()), view.getContext(), mode);
                    threads.add(thread);
                    thread.start();
                }

            }

        }
        else {
            if(handler==null)handler=new FindingHandler();
            for(FinderThread thread:threads){
                if(thread.isInterrupted()) {
                    TextFinder textFinder;
                    if(thread.mode==mode){
                        textFinder = thread.getFinder();
                        textFinder.setStr(s.toString());
                        threads.add(new FinderThread(textFinder, view.getContext(),mode));
                        threads.remove(thread);
                        threads.get(threads.size() - 1).start();
                        return;
                    }
                    else{
                        thread.getFinder().controlMap=null;
                        threads.remove(thread);
                    }
                    switch (mode){
                        case(0):{
                            finder.setFileName("BaseDictionary.txt");
                            FinderThread mainThread=new FinderThread(finder, view.getContext(),mode);
                            threads.add(mainThread);
                            mainThread.start();
                            FinderThread thread1=new FinderThread(new TextFinder("3словарь.txt",s.toString()),view.getContext(),mode);
                            threads.add(thread1);
                            thread1.start();
                            FinderThread thread2=new FinderThread(new TextFinder("2словарь.txt",s.toString()),view.getContext(),mode);
                            threads.add(thread2);
                            thread2.start();
                            break;
                        }
                        case(1):{
                            finder.setFileName("рус_бел.txt");
                            FinderThread thread3=new FinderThread(finder,view.getContext(),mode);
                            threads.add(thread3);
                            thread3.start();
                            FinderThread thread4=new FinderThread(new TextFinder("рус_бел2.txt",s.toString()),view.getContext(),mode);
                            threads.add(thread4);
                            thread4.start();
                            FinderThread thread5=new FinderThread(new TextFinder("рус_бел3.txt",s.toString()),view.getContext(),mode);
                            threads.add(thread5);
                            thread5.start();
                            break;
                        }
                    }
                    for(String filename:view.getContext().fileList()){
                        if((!filename.endsWith(".db")||!filename.endsWith(".db.backup"))) {
                            FinderThread thread7 = new FinderThread(new TextFinder(filename, s.toString()), view.getContext(), mode);
                            threads.add(thread7);
                            thread7.start();
                        }

                    }

                }
                else {
                    thread.interrupt();
                    TextFinder textFinder;
                    if(thread.mode==mode){
                        textFinder = thread.getFinder();
                        textFinder.setStr(s.toString());
                        threads.add(new FinderThread(textFinder, view.getContext(),mode));
                        threads.remove(thread);
                        threads.get(threads.size() - 1).start();
                        return;
                    }
                    else {
                        thread.getFinder().controlMap=null;
                        threads.remove(thread);
                    }
                        switch (mode){
                            case(0):{
                                finder.setFileName("BaseDictionary.txt");
                                FinderThread mainThread=new FinderThread(finder, view.getContext(),mode);
                                threads.add(mainThread);
                                mainThread.start();
                                FinderThread thread1=new FinderThread(new TextFinder("3словарь.txt",s.toString()),view.getContext(),mode);
                                threads.add(thread1);
                                thread1.start();
                                FinderThread thread2=new FinderThread(new TextFinder("2словарь.txt",s.toString()),view.getContext(),mode);
                                threads.add(thread2);
                                thread2.start();
                                break;
                            }
                            case(1):{
                                finder.setFileName("рус_бел.txt");
                                FinderThread thread3=new FinderThread(finder,view.getContext(),mode);
                                threads.add(thread3);
                                thread3.start();
                                FinderThread thread4=new FinderThread(new TextFinder("рус_бел2.txt",s.toString()),view.getContext(),mode);
                                threads.add(thread4);
                                thread4.start();
                                FinderThread thread5=new FinderThread(new TextFinder("рус_бел3.txt",s.toString()),view.getContext(),mode);
                                threads.add(thread5);
                                thread5.start();
                                break;
                            }
                        }
                        for(String filename:view.getContext().fileList()){
                            if((!filename.endsWith(".db")||!filename.endsWith(".db.backup"))) {
                                FinderThread thread7 = new FinderThread(new TextFinder(filename, s.toString()), view.getContext(), mode);
                                threads.add(thread7);
                                thread7.start();
                            }

                        }
                }

            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
    public static class FindingHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.arg1==0){

                TreeMap<String,ArrayList<String>> treeMap= (TreeMap<String, ArrayList<String>>) msg.getData().getSerializable("TreeMap");
                assert treeMap != null;
                for(Map.Entry<String,ArrayList<String>> entry:treeMap.entrySet()){
                    if(mapMain.containsKey(entry.getKey())){
                        for(String s1:entry.getValue()){
                            if(!mapMain.get(entry.getKey()).contains(s1))mapMain.get(entry.getKey()).add(s1);
                        }
                    }
                    else mapMain.put(entry.getKey(), entry.getValue());
                }
                view.setAdapter(new RecyclerViewAdapter(mapMain));
            }
        }

    }
    public static class MapHandler extends Handler {


        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.arg1==0){
                Bundle bundle=new Bundle();
                TreeMap<String,ArrayList<String>> map= (TreeMap<String, ArrayList<String>>) msg.getData().getSerializable("TreeMap");
                bundle.putSerializable("TreeMap",map);
                Message msg2= TranslationWordsWatcher.handler.obtainMessage();
                msg2.setData(bundle);
                msg2.arg1=0;
                TranslationWordsWatcher.handler.sendMessage(msg2);
            }
        }

    }
    public void setView(RecyclerView v) {
        view = v;
    }
}
