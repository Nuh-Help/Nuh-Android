package org.isa.nuh;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

public class NeedHelpFragment extends Fragment {

    private SparseBooleanArray isCardPressed = new SparseBooleanArray(6);


    public NeedHelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_need_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button submit = view.findViewById(R.id.submit_need_help);
        submit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegistrationActivity.class);
            startActivity(intent);
        });
        initViews();
    }

    private void initViews() {
        isCardPressed.append(R.id.accomodation_card_need, false);
        isCardPressed.append(R.id.food_card_need, false);
        isCardPressed.append(R.id.clothes_card_need, false);
        isCardPressed.append(R.id.medicine_card_need, false);
        isCardPressed.append(R.id.volunteer_card_need, false);
        isCardPressed.append(R.id.other_card_need, false);

        setCardListener(R.id.accomodation_card_need, R.id.accomodation_card_edittext_need);
        setCardListener(R.id.food_card_need, R.id.food_card_edittext_need);
        setCardListener(R.id.clothes_card_need, R.id.clothes_card_edittext_need);
        setCardListener(R.id.medicine_card_need, R.id.medicine_card_edittext_need);
        setCardListener(R.id.volunteer_card_need, R.id.volunteer_card_edittext_need);
        setCardListener(R.id.other_card_need, R.id.other_card_edittext_need);
    }

    private void setCardListener(final int cardID, final int editTextID) {
        Objects.requireNonNull(getActivity()).findViewById(cardID).setOnClickListener(v -> {
            ViewGroup card = getActivity().findViewById(cardID);

            if (!isCardPressed.get(cardID)) {
                TransitionManager.beginDelayedTransition(card);
                v.findViewById(editTextID).setVisibility(View.VISIBLE);
                v.setBackgroundColor(getResources().getColor(R.color.checked_toggle));
                isCardPressed.put(cardID, true);
            } else {
                TransitionManager.beginDelayedTransition(card);
                v.findViewById(editTextID).setVisibility(View.GONE);
                v.setBackgroundColor(getResources().getColor(R.color.unchecked_toggle));
                isCardPressed.put(cardID, false);
            }
        });
    }
}
