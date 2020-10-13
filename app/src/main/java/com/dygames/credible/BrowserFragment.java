package com.dygames.credible;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class BrowserFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browser, container, false);

        Spinner category_spinner = rootView.findViewById(R.id.browser_spinner);
        String[] spinnerArray = {"Photo", "Video", "Voice"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        category_spinner.setAdapter(spinnerArrayAdapter);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);


        final File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            file = new File(getContext().getExternalFilesDir(null), "Credible" + File.separator + "Photo");
        else
            file = new File(Environment.getExternalStorageState(), "Credible" + File.separator + "Photo");

        File[] files = file.listFiles();
        BrowserAdapter.Data[] data = new BrowserAdapter.Data[files.length];

        for (int i = 0; i < files.length; i++) {
            data[i] = new BrowserAdapter.Data(files[i].getName(), BitmapFactory.decodeFile(files[i].getPath()));
        }

        recyclerView.setAdapter(new BrowserAdapter(data));


        return rootView;
    }
}
