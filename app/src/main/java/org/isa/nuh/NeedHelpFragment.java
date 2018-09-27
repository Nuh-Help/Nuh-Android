package org.isa.nuh;

import android.content.Context;
import android.content.SharedPreferences;
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

public class NeedHelpFragment extends Fragment implements SPController {

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
        submit.setOnClickListener(v -> submitChanges());
        initSparseArray();
        initViews();
    }

    private void submitChanges() {
        // proveri koje su kategorije kliknute

        SharedPreferences.Editor editor = Objects.requireNonNull(getActivity())
                .getSharedPreferences(HELP_CATEGORIES, Context.MODE_PRIVATE).edit();

        editor.putBoolean(ACCOMODATION_NEED, isCardPressed.get(R.id.accomodation_card_need));
        editor.putBoolean(FOOD_NEED, isCardPressed.get(R.id.food_card_need));
        editor.putBoolean(CLOTHES_NEED, isCardPressed.get(R.id.clothes_card_need));
        editor.putBoolean(MEDICINE_NEED, isCardPressed.get(R.id.medicine_card_need));
        editor.putBoolean(VOLUNTEER_NEED, isCardPressed.get(R.id.volunteer_card_need));
        editor.putBoolean(OTHER_NEED, isCardPressed.get(R.id.other_card_need));

        editor.apply();

        // TODO: ubmit changes to server

    }

    private void initSparseArray() {
        SharedPreferences sp = Objects.requireNonNull(getActivity())
                .getSharedPreferences(HELP_CATEGORIES, Context.MODE_PRIVATE);

        isCardPressed.append(R.id.accomodation_card_need, sp.getBoolean(ACCOMODATION_NEED, false));
        isCardPressed.append(R.id.food_card_need, sp.getBoolean(FOOD_NEED, false));
        isCardPressed.append(R.id.clothes_card_need, sp.getBoolean(CLOTHES_NEED, false));
        isCardPressed.append(R.id.medicine_card_need, sp.getBoolean(MEDICINE_NEED, false));
        isCardPressed.append(R.id.volunteer_card_need, sp.getBoolean(VOLUNTEER_NEED, false));
        isCardPressed.append(R.id.other_card_need, sp.getBoolean(OTHER_NEED, false));

    }

    private void initViews() {
        setCardListener(R.id.accomodation_card_need, R.id.accomodation_card_edittext_need);
        setCardListener(R.id.food_card_need, R.id.food_card_edittext_need);
        setCardListener(R.id.clothes_card_need, R.id.clothes_card_edittext_need);
        setCardListener(R.id.medicine_card_need, R.id.medicine_card_edittext_need);
        setCardListener(R.id.volunteer_card_need, R.id.volunteer_card_edittext_need);
        setCardListener(R.id.other_card_need, R.id.other_card_edittext_need);

        setCardChecked(R.id.accomodation_card_need, R.id.accomodation_card_edittext_need);
        setCardChecked(R.id.food_card_need, R.id.food_card_edittext_need);
        setCardChecked(R.id.clothes_card_need, R.id.clothes_card_edittext_need);
        setCardChecked(R.id.medicine_card_need, R.id.medicine_card_edittext_need);
        setCardChecked(R.id.volunteer_card_need, R.id.volunteer_card_edittext_need);
        setCardChecked(R.id.other_card_need, R.id.other_card_edittext_need);

    }

    private void setCardChecked(final int cardID, final int editTextID) {
        ViewGroup card = Objects.requireNonNull(getActivity()).findViewById(cardID);

        if (isCardPressed.get(cardID)) {
            card.findViewById(editTextID).setVisibility(View.VISIBLE);
            card.setBackgroundColor(getResources().getColor(R.color.checked_toggle));
            isCardPressed.put(cardID, true);
        } else {
            card.findViewById(editTextID).setVisibility(View.GONE);
            card.setBackgroundColor(getResources().getColor(R.color.unchecked_toggle));
            isCardPressed.put(cardID, false);
        }
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
