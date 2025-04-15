package net.project.slounik.utils.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Pair;

import net.project.slounik.utils.ComparatorForMap;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MultiDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2; // Увеличиваем версию
    private final String databaseName;

    public static final String TABLE_WORDS = "words";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_TRANSLATION = "translation";

    // Новая структура таблицы с составным уникальным ключом
    private static final String CREATE_TABLE_V2 =
            "CREATE TABLE IF NOT EXISTS " + TABLE_WORDS + "(" +
                    COLUMN_WORD + " TEXT, " +
                    COLUMN_TRANSLATION + " TEXT, " +
                    "UNIQUE(" + COLUMN_WORD + ", " + COLUMN_TRANSLATION + ") ON CONFLICT IGNORE)";
    private static final String INSERT_SQL =
            "INSERT OR IGNORE INTO " + TABLE_WORDS +
                    " (" + COLUMN_WORD + ", " + COLUMN_TRANSLATION + ") VALUES (?, ?)";

    public MultiDatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
        this.databaseName = databaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_V2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Миграция данных при обновлении
        if (oldVersion < 2) {
            db.execSQL("CREATE TEMPORARY TABLE temp_words(word, translation)");
            db.execSQL("INSERT INTO temp_words SELECT word, translation FROM words");
            db.execSQL("DROP TABLE words");
            db.execSQL(CREATE_TABLE_V2);
            db.execSQL("INSERT INTO words SELECT * FROM temp_words");
            db.execSQL("DROP TABLE temp_words");
        }
    }

    // Метод для получения TreeMap
    public TreeMap<String, ArrayList<String>> getTranslationsMap(String s) {
        TreeMap<String, ArrayList<String>> map = new TreeMap<>(new ComparatorForMap(s));
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_WORDS,
                new String[]{COLUMN_WORD, COLUMN_TRANSLATION},
                null, null, null, null,
                COLUMN_WORD + " COLLATE NOCASE" // Сортировка без учета регистра
        );

        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(0);
                String translation = cursor.getString(1);

                if (!map.containsKey(word)) {
                    map.put(word, new ArrayList<>());
                }

                ArrayList<String> translations = map.get(word);
                if (!translations.contains(translation)) {
                    translations.add(translation);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return map;
    }

    // Улучшенный метод добавления перевода
    public long addTranslation(String word, String translation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_WORD, word.trim());
        values.put(COLUMN_TRANSLATION, translation.trim());

        long result = db.insertWithOnConflict(
                TABLE_WORDS,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        db.close();
        return result;
    }

    // Метод для получения всех переводов конкретного слова
    public ArrayList<String> getTranslationsForWord(String word) {
        ArrayList<String> translations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_WORDS,
                new String[]{COLUMN_TRANSLATION},
                COLUMN_WORD + " = ?",
                new String[]{word},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                translations.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return translations;
    }
    public void forceClose() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
    public void ultraFastInsert(List<Pair<String, String>> data) {
        SQLiteDatabase db = getWritableDatabase();

        // Оптимизация параметров
        db.execSQL("PRAGMA cache_size = -10000");  // 10MB кэша
        db.execSQL("PRAGMA temp_store = MEMORY");
        db.rawQuery("PRAGMA locking_mode = EXCLUSIVE",null).close();

        SQLiteStatement stmt = db.compileStatement(INSERT_SQL);
        db.beginTransaction();
        try {
            int counter = 0;
            for (Pair<String, String> item : data) {
                stmt.bindString(1, item.first);
                stmt.bindString(2, item.second);
                stmt.execute();
                stmt.clearBindings();

                if (++counter % 1000 == 0) {
                    db.yieldIfContendedSafely();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            stmt.close();
            db.rawQuery("PRAGMA locking_mode = NORMAL",null).close();
            db.close();
        }
    }
}