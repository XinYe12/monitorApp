package com.example.monitorapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.monitorapp.databinding.ActivityAdminDashboardsBinding;
import com.example.monitorapp.databinding.FragmentHomeBinding;
import com.example.monitorapp.userInterfaces.ui.CustomListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "mysql";
    private Button lookupButton, addBtn, deleteBtn, updateBtn;
    private List<HashMap<String, Object>> listIns = new ArrayList<HashMap<String, Object>>();
    private ListView listView = null;
    private CustomListAdapter customListAdapter;


    private View.OnClickListener clickListener;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}