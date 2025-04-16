
package net.project.slounik.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Pair;

import net.project.slounik.utils.SQL.MultiDatabaseHelper;
import net.project.slounik.utils.SQL.StorageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFinder {
    private String str;
    public TreeMap<String,ArrayList<String>> controlMap;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public String getStr() {
        return str;
    }

    private static String delimiter= "(?s)((?<!\\p{L})\\p{Lu}(?:['’]?\\p{Lu})+(?!\\p{L}))(.*?)(?=(?<!\\p{L})\\p{Lu}(?:['’]?\\p{Lu})+(?!\\p{L})|$)";

    public TextFinder(){

    }

    public TextFinder(String fileName, String str){
        this.fileName=fileName;
        this.str=str;
    }

    public TreeMap<String, ArrayList<String>> findText(Context context){
        TreeMap<String, ArrayList<String>> map=new TreeMap<>(new ComparatorForMap(this.str));
        try {
            BufferedReader reader;
            String tempdel;
            if((this.fileName.equals("BaseDictionary.txt")||this.fileName.equals("3словарь.txt")||this.fileName.equals("2словарь.txt")||this.fileName.equals("рус_бел.txt")||this.fileName.equals("рус_бел2.txt")||this.fileName.equals("рус_бел3.txt"))&&controlMap==null){
                AssetManager manager= context.getAssets();
                reader=new BufferedReader(new InputStreamReader(manager.open(this.fileName)));
                tempdel= delimiter;
            }
            else if(controlMap==null){
                reader=new BufferedReader(new InputStreamReader(context.openFileInput(this.fileName)));
                tempdel=reader.readLine();
            }
            else {
                boolean isFinded=false;
                for(Map.Entry<String,ArrayList<String>> entry:controlMap.entrySet()){
                    if(containsIgnoreCase(entry.getKey(),this.str)){
                        map.put(entry.getKey(),entry.getValue());
                        isFinded=true;
                    }
                }
                if(!isFinded){
                    if((this.fileName.equals("BaseDictionary.txt")||this.fileName.equals("3словарь.txt")||this.fileName.equals("2словарь.txt")||this.fileName.equals("рус_бел.txt")||this.fileName.equals("рус_бел2.txt")||this.fileName.equals("рус_бел3.txt"))){
                        AssetManager manager= context.getAssets();
                        reader=new BufferedReader(new InputStreamReader(manager.open(this.fileName)));
                        tempdel= delimiter;
                    }
                    else {
                        reader = new BufferedReader(new InputStreamReader(context.openFileInput(this.fileName)));
                        tempdel = reader.readLine();
                    }
                }
                else {
                    controlMap=map;
                    return map;
                }
            }
            StringBuilder temp= new StringBuilder();
            String line="";
            while ((line = reader.readLine()) != null){
                temp.append(line).append("\n");
            }
            reader.close();
            Pattern pattern= Pattern.compile(tempdel);
            Matcher matcher=pattern.matcher(temp.toString());
            ArrayList<String> tempList=new ArrayList<>();
            while (matcher.find()){
                String text=matcher.group(1);
                String text2= matcher.group(2);
                tempList.add(text);
                tempList.add(text2);
            }
            String[] array=tempList.toArray(new String[0]);
            MultiDatabaseHelper helper=new MultiDatabaseHelper(context,fileName.substring(0,fileName.lastIndexOf("."))+".db");
            List<Pair<String,String>> list=new ArrayList<>();
            for(int i=0;i+1<array.length;i+=2){
                list.add(new Pair<>(array[i],array[i+1]));
            }
            helper.ultraFastInsert(list);
            helper.forceClose();
            //StorageUtils.saveDatabase(context.getApplicationContext(),helper.getDatabaseName());
            String searchable=this.str;
            for(int i=0;i<array.length-1;i+=2){
                if(containsIgnoreCase(array[i],searchable)){
                    if(map.get(array[i])!=null){
                        map.get(array[i]).add(array[i+1]);
                        continue;
                    }
                    ArrayList<String> arrayList=new ArrayList<>();
                    arrayList.add(array[i+1]);
                    map.put(array[i],arrayList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        controlMap=map;
        return map;
    }

    public static void setDelimiter(String del) {
        delimiter = del;
    }

    public static String getDelimiter() {
        return delimiter;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return false;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

}
