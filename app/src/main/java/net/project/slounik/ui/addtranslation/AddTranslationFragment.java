package net.project.slounik.ui.addtranslation;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.project.slounik.databinding.FragmentAddtranslationBinding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;

public class AddTranslationFragment extends Fragment {

    private FragmentAddtranslationBinding binding;

    public AddTranslationFragment() {
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddtranslationBinding.inflate(inflater, container, false);
        binding.addTranslation.setOnClickListener(view -> {
            String word= binding.editTextText2.getText().toString();
            String translation=binding.editTextTextMultiLine.getText().toString();
            try {
                BufferedWriter writer=new BufferedWriter((new OutputStreamWriter(requireActivity().openFileOutput("userTranslations.txt", Context.MODE_APPEND))));
                if(!new File("userTranslations.txt").exists()){
                    writer.write("(.+ )((?:[^\\n])+)\n");
                }
                writer.write(word+" ");
                writer.write(translation+"\n");
                writer.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return binding.getRoot();
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