package com.alohagoha.weathernote;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.alohagoha.weathernote.model.SelectedCity;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import es.dmoral.toasty.Toasty;

public class CreateActionFragment extends BaseFragment {
    //объявление переменных
    private EditText editTextCountry;
    private TextInputLayout textInputLayout;
    private RecyclerView recyclerView;
    OnHeadlineSelectedListener mCallback;
    private LinearLayout linearLayout;
    private ArrayList<SelectedCity> cityList;

    private Button btnTab;

    public interface OnHeadlineSelectedListener {
        void onArticleSelected(ArrayList<String> position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toast.makeText(getContext(), "onAttachAction", Toast.LENGTH_SHORT).show();

        try {
            mCallback = (OnHeadlineSelectedListener) getBaseActivity().getAnotherFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getBaseActivity().getAnotherFragment().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //обращаемся к layout который будет содержать наш фрагмент
        return inflater.inflate(R.layout.create_action_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initLayout(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        initCountryList();

        btnTab = view.findViewById(R.id.btn_tab);
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.inflate(R.menu.btn_tab_menu);
                popupMenu.show();
            }
        });

        recyclerView = view.findViewById(R.id.list_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        CustomAdapter customAdapter = new CustomAdapter(getContext(), cityList, mCallback, getBaseActivity());
        recyclerView.setAdapter(customAdapter);

        //инициализация edittext и листенер на ключи при взаимодействии с ним, когда мы нашимаем enter у нас опускается клавиатура и запускается WeatherFragment
        editTextCountry = (EditText) view.findViewById(R.id.et_country);

        editTextCountry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (editTextCountry.getText().toString().length() > 5) {
                        showError();
                    } else
                        hideError();

//                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                    String country = editTextCountry.getText().toString().trim();
//                    ArrayList<String> arrayList = new ArrayList<>();
//                    arrayList.add(country);
//                    mCallback.onArticleSelected(arrayList);
                    return true;
                }
                return false;
            }
        });

        textInputLayout = view.findViewById(R.id.text_input);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_action_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_add:
                Toasty.success(getContext(), "Success menu!!").show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showError() {
        textInputLayout.setError("Error!!");
    }

    private void hideError() {
        textInputLayout.setError("");
    }

    private void initCountryList() {
        cityList = new ArrayList<>();
        SelectedCity selectedCity1 = new SelectedCity();
        selectedCity1.setCity("Moscow");
        selectedCity1.setSelected(false);

        SelectedCity selectedCity2 = new SelectedCity();
        selectedCity2.setCity("St. Peterburg");
        selectedCity2.setSelected(false);

        SelectedCity selectedCity3 = new SelectedCity();
        selectedCity3.setCity("Kazan");
        selectedCity3.setSelected(false);

        cityList.add(selectedCity1);
        cityList.add(selectedCity2);
        cityList.add(selectedCity3);
    }
}
