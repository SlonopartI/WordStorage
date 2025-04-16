package net.project.slounik.utils.SQL;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class StorageUtils {
    public static boolean saveDatabase(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        if (!dbFile.exists()) return false;

        try (FileInputStream in = new FileInputStream(dbFile);
             FileOutputStream out = new FileOutputStream(new File(new File(context.getFilesDir(),"datB"),dbName))) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loadDatabase(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        if (dbFile.exists() && !dbFile.delete()) return false;

        try (FileInputStream in = new FileInputStream((new File(new File(context.getFilesDir(),"datB"),dbName)));
             FileOutputStream out = new FileOutputStream(dbFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isDatabaseSaved(Context context, String dbName) {
        return new File(context.getFilesDir(), dbName).exists();
    }
}