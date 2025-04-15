package net.project.slounik.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.slounik.databinding.FragmentSettingsBinding;
import net.project.slounik.ui.translation.TranslationWordsWatcher;

import net.project.slounik.utils.FinderThread;
import net.project.slounik.utils.LocaleHelper;
import net.project.slounik.utils.TextFinder;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private String currentLanguage;
    private boolean init=false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        currentLanguage=LocaleHelper.getSavedLanguage(requireContext());
        init=false;

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Spinner spinner=binding.languageSpinner;
        spinner.setAdapter(new ArrayAdapter<>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,new String[]{"Беларуская","Русский"}));
        spinner.setSelection(getPositionForLanguage(currentLanguage));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(init){
                    switch (i){
                        case 0:
                            if(!currentLanguage.equals("be-rBY")) {
                                LocaleHelper.setLocale(requireContext(), "be-rBY");
                                currentLanguage = "be-rBY";
                                System.err.println(currentLanguage +" "+LocaleHelper.getSavedLanguage(requireContext()));
                                requireActivity().recreate();
                            }
                            break;
                        case 1:
                            if(!currentLanguage.equals("ru")) {
                                LocaleHelper.setLocale(requireContext(), "ru");
                                currentLanguage = "ru";
                                System.err.println(currentLanguage +" "+LocaleHelper.getSavedLanguage(requireContext()));
                                requireActivity().recreate();
                            }
                    }
                }
                else init=true;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Spinner modeSpinner=binding.spinner;
        modeSpinner.setAdapter(new ArrayAdapter<>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,new String[]{"Беларуска-Русск","Русско-Белоруский"}));
        modeSpinner.setSelection(TranslationWordsWatcher.mode);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TranslationWordsWatcher.mode=position;
                if(TranslationWordsWatcher.threads!=null){
                    for(FinderThread thread:TranslationWordsWatcher.threads){
                        if(thread.isInterrupted()){
                            TextFinder newFinder=new TextFinder(thread.getFinder().getFileName(),thread.getFinder().getStr());
                            thread.setFinder(newFinder);
                            thread.mode=position;
                        }
                    }
                    TranslationWordsWatcher.mapMain.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int getPositionForLanguage(String language) {
        switch (language) {
            case "ru":
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("lang",currentLanguage);
        //outState.putString("delimeter",TextFinder.getDelimiter());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null){
            currentLanguage=savedInstanceState.getString("lang");
            //TextFinder.setDelimiter(savedInstanceState.getString("delimeter"," "));
        }
    }
}