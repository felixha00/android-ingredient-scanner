package com.example.ingrediscan;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ingrediscan.R;


public class HomeFragment extends Fragment implements View.OnClickListener {
    ImageButton menu_button;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageButton menuButton;
        Button ingredient;


        View myView = inflater.inflate(R.layout.fragment_home, container, false);
        startItemsCheckedAnimation(myView);






        menuButton= (ImageButton) myView.findViewById(R.id.menuBtn);
        menuButton.setOnClickListener(this);

        ingredient = (Button) myView.findViewById(R.id.launch_ingredients);
        ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), IngredientActivity.class);
                startActivity(intent);
            }
        });
        return myView;


    }

    // When clicking the menu button on Home Fragment
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menuBtn:
                /*
                TextView tv = getActivity().findViewById(R.id.home_title);
                tv.setText("h");

                 */
                MenuBottomSheetDialog bottomSheet = new MenuBottomSheetDialog();
                bottomSheet.show(getActivity().getSupportFragmentManager(),"menuBottomSheet");
                break;

        }


    }

    private void startItemsCheckedAnimation(View v) {



        final TextView itemsChecked = (TextView) v.findViewById(R.id.items_checked_counter);
        int itemsCheckedVal = Integer.parseInt(itemsChecked.getText().toString());

        ValueAnimator animator = ValueAnimator.ofInt(0, itemsCheckedVal);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                itemsChecked.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }

    private void countItemsChecked(){

        TextView itemsChecked = (TextView) getActivity().findViewById(R.id.items_checked_counter);
        int itemsCheckedVal = Integer.parseInt(itemsChecked.getText().toString());

        for (int i = 0; i<itemsCheckedVal; i++){

        }

    }

}
