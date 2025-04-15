package net.project.slounik.utils.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StorageUtils {
    private static final String BACKUP_DIR = "database_backups";
    private static final String BACKUP_EXT = ".backup";

    // Экспорт БД во внутреннее хранилище
    public static boolean exportDatabase(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        File backupDir = new File(context.getFilesDir(), BACKUP_DIR);

        if (!dbFile.exists()) return false;

        // Создаем директорию если нужно
        if (!backupDir.exists() && !backupDir.mkdirs()) {
            return false;
        }

        File backupFile = new File(backupDir, dbName + BACKUP_EXT);

        try (InputStream in = new FileInputStream(dbFile);
             OutputStream out = new FileOutputStream(backupFile)) {

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

    // Импорт БД из внутреннего хранилища
    public static boolean importDatabase(Context context, String dbName) {
        File backupDir = new File(context.getFilesDir(), BACKUP_DIR);
        File backupFile = new File(backupDir, dbName + BACKUP_EXT);
        File dbFile = context.getDatabasePath(dbName);

        if (!backupFile.exists()) return false;

        // Закрыть все соединения с БД
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                dbFile.getAbsolutePath(),
                null,
                SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS
        );
        db.close();

        try (InputStream in = new FileInputStream(backupFile);
             OutputStream out = new FileOutputStream(dbFile)) {

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

    // Получение списка доступных бэкапов
    public static String[] getBackupList(Context context) {
        File backupDir = new File(context.getFilesDir(), BACKUP_DIR);
        File[] files = backupDir.listFiles((dir, name) -> name.endsWith(BACKUP_EXT));

        if (files == null) return new String[0];

        String[] result = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            result[i] = name.substring(0, name.length() - BACKUP_EXT.length());
        }
        return result;
    }

    // Удаление бэкапа
    public static boolean deleteBackup(Context context, String dbName) {
        File backupFile = new File(
                new File(context.getFilesDir(), BACKUP_DIR),
                dbName + BACKUP_EXT
        );
        return backupFile.delete();
    }

    // Проверка существования бэкапа
    public static boolean backupExists(Context context, String dbName) {
        File backupFile = new File(
                new File(context.getFilesDir(), BACKUP_DIR),
                dbName + BACKUP_EXT
        );
        return backupFile.exists();
    }
}