package net.project.slounik.ui.dictionary;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.project.slounik.MainActivity;
import net.project.slounik.databinding.FragmentDictionaryBinding;
import net.project.slounik.utils.SimpleAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class DictionaryFragment extends Fragment {

    public static CopyOnWriteArrayList<String> dictionariesList;

    private FragmentDictionaryBinding binding;
    public static String regex=null;

    public static ActivityResultLauncher<Intent> launcher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDictionaryBinding.inflate(inflater, container, false);
        binding.addFAD.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    regex=binding.editTextText.getText().toString();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + binding.getRoot().getContext().getPackageName()));
                    startActivity(intent);
                }
            } else {
                ActivityCompat.requestPermissions((Activity) binding.getRoot().getContext(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PackageManager.PERMISSION_GRANTED);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    regex=binding.editTextText.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    String number=binding.editTextNumber.getText().toString();
                    MainActivity.pageIndex=Integer.parseInt(number);
                    launcher.launch(intent);
                }
            }
        });
        dictionariesList=new CopyOnWriteArrayList<>();
        File directory=binding.getRoot().getContext().getFilesDir();
        dictionariesList.addAll(Arrays.asList(directory.list()));
        Spinner spinner=binding.dictionariesSpinner;
        ArrayAdapter<String> adapter=new ArrayAdapter<>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapter.addAll(dictionariesList);
        spinner.setAdapter(adapter);
        binding.dictionariesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.dictionariesRecyclerView.setAdapter(new SimpleAdapter(dictionariesList));
        binding.deleteFAD.setOnClickListener(l->{
            if(spinner.getSelectedItem()!=null){
                if(!binding.getRoot().getContext().deleteFile((String) spinner.getSelectedItem())){
                    Toast.makeText(binding.getRoot().getContext(),"File is not existing",Toast.LENGTH_SHORT).show();
                };
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