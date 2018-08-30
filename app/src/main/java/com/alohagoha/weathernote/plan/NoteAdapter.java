package com.alohagoha.weathernote.plan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.alohagoha.weathernote.R;
import com.alohagoha.weathernote.model.note.Note;
import com.alohagoha.weathernote.model.note.NoteDataReader;

import java.util.ArrayList;


public class NoteAdapter  extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private NoteDataReader noteDataReader;                  // Здесь нам нужен только читатель дарнных
    private OnMenuItemClickListener itemMenuClickListener;  // Слушатель, будет устанавливаться извне

    public NoteAdapter(NoteDataReader noteDataReader) {
        this.noteDataReader = noteDataReader;
    }

    // вызывается при создании новой карточки списка
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Создаём новый элемент пользовательского интерфейса
        // Через Inflater
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan_rv, parent, false);
        // Здесь можно установить всякие параметры
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Привязываем данные к карточке
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(noteDataReader.getPosition(position));
    }

    @Override
    public int getItemCount() {
        return noteDataReader.getCount();
    }

    // установка слушателя
    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener){
        this.itemMenuClickListener = onMenuItemClickListener;
    }

    // интерфейс для обработки мен
    public interface OnMenuItemClickListener {
        void onItemEditClick(Note note);
        void onItemDeleteClick(Note note);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textNote;
        private Note note;

        public ViewHolder(View itemView) {
            super(itemView);
            textNote = itemView.findViewById(R.id.tv_plan_rv);
            // при тапе на элементе - вытащим  меню
            textNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemMenuClickListener != null) {
                        showPopupMenu(textNote);
                    }
                }
            });
        }

        public void bind(Note note){
            this.note = note;
            textNote.setText(note.getTitle());
        }

        private void showPopupMenu(View view) {
            // Покажем меню на элементе
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.context_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

                // обработка выбора пункта меню
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // делегируем обработку слушателю
                    switch (item.getItemId()) {
                        case R.id.menu_edit:
                            itemMenuClickListener.onItemEditClick(note);
                            return true;
                        case R.id.menu_delete:
                            itemMenuClickListener.onItemDeleteClick(note);
                            return true;
                    }
                    return false;
                }
            });
            popup.show();
        }

    }
}