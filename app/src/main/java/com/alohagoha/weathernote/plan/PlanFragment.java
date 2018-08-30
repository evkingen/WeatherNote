package com.alohagoha.weathernote.plan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.alohagoha.weathernote.BaseFragment;
import com.alohagoha.weathernote.R;
import com.alohagoha.weathernote.model.note.Note;
import com.alohagoha.weathernote.model.note.NoteDataReader;
import com.alohagoha.weathernote.model.note.NoteDataSource;

import es.dmoral.toasty.Toasty;



public class PlanFragment extends BaseFragment {

    private NoteDataSource noteDataSource;
    private NoteDataReader noteDataReader;
    private NoteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plan_layout, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.plan_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menu_add:
                addElement();
                return true;
            case R.id.menu_clear:
                clearList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearList() {

    }

    @Override
    protected void initLayout(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        initDataSource();

        RecyclerView recyclerView = view.findViewById(R.id.rv_plan);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoteAdapter(noteDataReader);
        adapter.setOnMenuItemClickListener(new NoteAdapter.OnMenuItemClickListener() {
            @Override
            public void onItemEditClick(Note note) {
                editElement(note);
            }

            @Override
            public void onItemDeleteClick(Note note) {
                deleteElement(note);
            }
        });

        recyclerView.setAdapter(adapter);
    }


    private void addElement() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View aletrView = inflater.inflate(R.layout.add_recycler, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(aletrView);
        builder.setTitle(R.string.title_add);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.menu_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editTextNote = aletrView.findViewById(R.id.et_note_text);
                EditText editTextNoteTitle = aletrView.findViewById(R.id.et_note_title);

                noteDataSource.addNote(editTextNoteTitle.getText().toString(), editTextNote.getText().toString());
                dataUpdate();
            }
        });
        builder.show();
    }

    private void deleteElement(Note note) {
        noteDataSource.deleteNote(note);
        dataUpdate();
    }

    private void editElement(Note note) {
        noteDataSource.editNote(note, "Edited", "Edited title");
        dataUpdate();
    }

    private void dataUpdate() {
        noteDataReader.Refresh();
        adapter.notifyDataSetChanged();
    }

    private void initDataSource() {
        noteDataSource = new NoteDataSource(getActivity().getApplicationContext());
        noteDataSource.open();
        noteDataReader = noteDataSource.getNoteDataReader();
    }
}
