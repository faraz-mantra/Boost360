package nowfloats.nfkeyboard.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;

import nowfloats.nfkeyboard.models.KeywordModel;

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
        String selection = COL_WORD + " like '" + query + "%'";
        ArrayList<String> list = new ArrayList<>();
        ArrayList<KeywordModel> modelList = new ArrayList<>();
        Cursor cursor = null;
        boolean isQueryWordExisting = false;
        try {
            cursor = query(selection, null, columns);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String word = cursor.getString(cursor.getColumnIndex(COL_WORD));
                    /*if (word.equalsIgnoreCase(query)) {
                        isQueryWordExisting = true;
                    } else {
                        KeywordModel model = new KeywordModel();
                        model.setType(KeywordModel.DICTIONARY_WORD);
                        model.setWord(cursor.getString(cursor.getColumnIndex(COL_WORD)));
                        modelList.add(model);
                    }*/
                    KeywordModel model = new KeywordModel();
                    model.setType(KeywordModel.DICTIONARY_WORD);
                    model.setWord(word);
                    modelList.add(model);
                    cursor.moveToNext();
                }

                /*if (!TextUtils.isEmpty(query)) {
                    KeywordModel model = new KeywordModel();
                    model.setWord(query);
                    if (isQueryWordExisting) {
                        model.setType(KeywordModel.DICTIONARY_WORD);
                    } else {
                        model.setType(KeywordModel.NEW_WORD);
                    }
                    modelList.add(0, model);
                }*/
                mDatabaseOpenHelper.updateEntry(query);
            }/* else {
                //mDatabaseOpenHelper.insertEntry(query);
                KeywordModel model = new KeywordModel();
                model.setType(KeywordModel.NEW_WORD);
                model.setWord(query);
                //list.add(query);
                modelList.add(model);
            }*/
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
        mDatabaseOpenHelper.insertEntry(word);
    }

}
