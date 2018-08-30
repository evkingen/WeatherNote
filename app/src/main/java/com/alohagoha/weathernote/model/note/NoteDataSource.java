package com.alohagoha.weathernote.model.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.alohagoha.weathernote.database.DatabaseHelper;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by shkryaba on 04/08/2018.
 */

public class NoteDataSource implements Closeable {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private NoteDataReader noteDataReader;

    public NoteDataSource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
        noteDataReader = new NoteDataReader(database);
        noteDataReader.open();
    }

    @Override
    public void close() throws IOException {
        noteDataReader.close();
        databaseHelper.close();
    }

    public Note addNote(String title, String desc) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOTE, desc);
        values.put(DatabaseHelper.COLUMN_NOTE_TITLE, title);

        long insertId = database.insert(DatabaseHelper.TABLE_NOTES, null, values);

        Note newNote = new Note();
        newNote.setDescription(desc);
        newNote.setTitle(title);
        newNote.setId(insertId);
        return newNote;
    }

    public void editNote(Note note, String desc, String title) {
        ContentValues editValues = new ContentValues();
        editValues.put(databaseHelper.COLUMN_ID, note.getId());
        editValues.put(databaseHelper.COLUMN_NOTE, note.getDescription());
        editValues.put(databaseHelper.COLUMN_NOTE_TITLE, note.getTitle());

        database.update(databaseHelper.TABLE_NOTES,
                editValues,
                databaseHelper.COLUMN_ID + "=" + note.getId(),
                null);
    }

    public void deleteNote(Note note) {
        long id = note.getId();
        database.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID +
        " = " + id, null);
    }

    public void deleteAll() {
        database.delete(DatabaseHelper.TABLE_NOTES, null, null);
    }

    public NoteDataReader getNoteDataReader() {
        return noteDataReader;
    }
}
