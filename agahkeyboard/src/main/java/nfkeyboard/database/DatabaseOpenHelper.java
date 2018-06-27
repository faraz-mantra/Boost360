package nfkeyboard.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String COL_WORD = "word";
    public static final String COL_COUNT = "count";
    private static final String TAG = "DictionaryDatabase";
    private static final String DATABASE_NAME = "DICTIONARY";
    public static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;
    private final Context mHelperContext;
    private static String WORD = "word=";
    private static final String FTS_TABLE_CREATE =
            "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                    " USING fts3 (" +
                    COL_WORD + ", " +
                    COL_COUNT + ")";
    private SQLiteDatabase mDatabase;

    DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mHelperContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDatabase = db;
        mDatabase.execSQL(FTS_TABLE_CREATE);
        loadDictionary();
    }

    private void loadDictionary() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadWords();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    private void loadWords() throws IOException {
        final Resources resources = mHelperContext.getResources();
        InputStream inputStream = null;//resources.openRawResource(R.raw.saved_words);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().indexOf(WORD) != -1) {
                    line = line.substring(line.indexOf(WORD) + WORD.length(), line.indexOf(","));
                    long id = addWords(line, 1);
                    if (id < 0) {
                        Log.e(TAG, "unable to add word: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private long addWords(String string, int count) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_WORD, string);
        initialValues.put(COL_COUNT, count);
        mDatabase = getWritableDatabase();
        return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
        onCreate(db);

    }

    public void insertEntry(String query) {
        addWords(query, 1);
    }

    public void updateEntry(String query) {
        int count = 0;
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        Cursor c = mDatabase.rawQuery("SELECT count FROM FTS WHERE word = ?", new String[]{query.trim()});
        if (c != null && c.moveToFirst()) {
            count = c.getInt(c.getColumnIndex(COL_COUNT));
        }
        count = count + 1;
        ContentValues cv = new ContentValues();
        cv.put(COL_COUNT, count);
        mDatabase.update(FTS_VIRTUAL_TABLE, cv, "word=?", new String[]{query});
    }


    public void checkAndInsertEntry(String word) {
        int count;
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        Cursor c = mDatabase.rawQuery("SELECT count FROM FTS WHERE word = ?", new String[]{word.trim()});
        if (c != null && c.moveToFirst()) {
            count = c.getInt(c.getColumnIndex(COL_COUNT));
            ContentValues cv = new ContentValues();
            cv.put(COL_COUNT, ++count);
            mDatabase.update(FTS_VIRTUAL_TABLE, cv, "word=?", new String[]{word});
        } else {
            long id = addWords(word, 1);
            if (id < 0) {
                Log.e(TAG, "unable to add word: " + word);
            }
        }
    }
}
