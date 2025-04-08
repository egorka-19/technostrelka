package com.example.main_screen.bottomnav.plus;
import com.example.main_screen.ChatActivity;
import com.example.main_screen.databinding.FragmentPlusBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlusFragment extends Fragment {
    public FragmentPlusBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlusBinding.inflate(inflater, container, false);
        FragmentPlusBinding.inflate(inflater, container, false);
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(PlusFragment.this.getActivity(), ChatActivity.class);
                startActivity(homeIntent);
            }
        });



        return binding.getRoot();
    }
}
