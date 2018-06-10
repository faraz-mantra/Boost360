package nfkeyboard.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;

import nfkeyboard.models.KeywordModel;

import static nowfloats.nfkeyboard.database.DatabaseOpenHelper.COL_COUNT;
import static nowfloats.nfkeyboard.database.DatabaseOpenHelper.COL_WORD;
import static nowfloats.nfkeyboard.database.DatabaseOpenHelper.FTS_VIRTUAL_TABLE;

public class DatabaseTable {
    private final DatabaseOpenHelper mDatabaseOpenHelper;

    public DatabaseTable(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    public ArrayList<KeywordModel> getWordMatches(String query, String[] columns) {
        if (query.indexOf("'") > 0) {
            query = query.replace("'", "''");
        }
        //String selection = COL_WORD + " like '" + query + "%'";
        String selection = COL_WORD + " like '" + "%" + query + "%'";
        ArrayList<KeywordModel> modelList = new ArrayList<>();
        Cursor cursor = null;
        boolean isQueryWordExisting = false;
        try {
            cursor = query(selection, null, columns);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String word = cursor.getString(cursor.getColumnIndex(COL_WORD));
                    KeywordModel model = new KeywordModel();
                    model.setType(KeywordModel.DICTIONARY_WORD);
                    model.setWord(word.trim());
                    if (!modelList.contains(model)) {
                        modelList.add(model);
                    }
                    cursor.moveToNext();
                }
                mDatabaseOpenHelper.updateEntry(query);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return modelList;
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null,
                COL_COUNT + " DESC LIMIT 3");

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public void saveWordToDatabase(String word) {
        mDatabaseOpenHelper.checkAndInsertEntry(word);
    }

}
