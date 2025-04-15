package net.project.slounik.ui.translation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.slounik.databinding.FragmentTranslationBinding;

public class TranslationFragment extends Fragment {

    private FragmentTranslationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTranslationBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        EditText wordField = binding.wordField;
        TranslationWordsWatcher watcher=new TranslationWordsWatcher();
        watcher.setView(binding.translationRecyclerView);
        wordField.addTextChangedListener(watcher);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}