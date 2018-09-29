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
import android.widget.EditText;

import java.util.Objects;


/**
 * Fragment for showing options for giving help.
 * Used in {@link MainActivity}.
 * @author Hamza Muric
 */
public class GiveHelpFragment extends Fragment implements SPController {

    // Int to Boolean map-like array for storing current pressed state of categories.
    private SparseBooleanArray isCardPressed = new SparseBooleanArray(6);

    /**
     * Empty constructor.
     * Required public constructor.
     */
    public GiveHelpFragment() {
        // Required empty public constructor
    }

    /**
     * Called when fragment view is creating.
     * @param inflater inflater for fragment layout.
     * @param container container of layout
     * @param savedInstanceState saved previous state of views in activity.
     * @return inflated fragment view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_give_help, container, false);
    }

    /**
     * Called when fragment view is finished creating (it's created).
     * @param view this fragment view
     * @param savedInstanceState saved previous state of views in activity.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button submit = view.findViewById(R.id.submit_give_help);
        submit.setOnClickListener(v -> submitChanges());
        initSparseArray();
        initViews();
    }

    /**
     * Called when submit button is pressed.
     * Stores current categories state in {@link SharedPreferences}
     * and calls submitChangesToServer method from {@link MainActivity}.
     */
    private void submitChanges() {
        SharedPreferences.Editor editor = Objects.requireNonNull(getActivity())
                .getSharedPreferences(HELP_CATEGORIES, Context.MODE_PRIVATE).edit();

        boolean isPressed;
        isPressed = isCardPressed.get(R.id.accomodation_card_give);
        editor.putBoolean(ACCOMODATION_GIVE, isPressed);
        editor.putString(ACCOMODATION_GIVE_TEXT, isPressed
                ? ((EditText) getActivity().findViewById(R.id.accomodation_card_edittext_give)).getText().toString()
                : null);
        isPressed = isCardPressed.get(R.id.food_card_give);
        editor.putBoolean(FOOD_GIVE, isPressed);
        editor.putString(FOOD_GIVE_TEXT, isPressed
                ? ((EditText) getActivity().findViewById(R.id.food_card_edittext_give)).getText().toString()
                : null);
        isPressed = isCardPressed.get(R.id.clothes_card_give);
        editor.putBoolean(CLOTHES_GIVE, isPressed);
        editor.putString(CLOTHES_GIVE_TEXT, isPressed
                ? ((EditText) getActivity().findViewById(R.id.clothes_card_edittext_give)).getText().toString()
                : null);
        isPressed = isCardPressed.get(R.id.medicine_card_give);
        editor.putBoolean(MEDICINE_GIVE, isPressed);
        editor.putString(MEDICINE_GIVE_TEXT, isPressed
                ? ((EditText) getActivity().findViewById(R.id.medicine_card_edittext_give)).getText().toString()
                : null);
        isPressed = isCardPressed.get(R.id.other_card_give);
        editor.putBoolean(OTHER_GIVE, isPressed);
        editor.putString(OTHER_GIVE_TEXT, isPressed
                ? ((EditText) getActivity().findViewById(R.id.other_card_edittext_give)).getText().toString()
                : null);

        editor.apply();

        Context parent = getActivity();
        if (parent instanceof MainActivity) {
            ((MainActivity) parent).submitChangesToServer();
        }
    }

    /*
     * Called in onViewCreated.
     * Initializes SparseBooleanArray for storing categories current pressed state.
     */
    private void initSparseArray() {
        SharedPreferences sp = Objects.requireNonNull(getActivity())
                .getSharedPreferences(HELP_CATEGORIES, Context.MODE_PRIVATE);

        isCardPressed.append(R.id.accomodation_card_give, sp.getBoolean(ACCOMODATION_GIVE, false));
        isCardPressed.append(R.id.food_card_give, sp.getBoolean(FOOD_GIVE, false));
        isCardPressed.append(R.id.clothes_card_give, sp.getBoolean(CLOTHES_GIVE, false));
        isCardPressed.append(R.id.medicine_card_give, sp.getBoolean(MEDICINE_GIVE, false));
        isCardPressed.append(R.id.other_card_give, sp.getBoolean(OTHER_GIVE, false));

    }

    /*
     * Called in onViewCreated.
     * Initializes card listeners and sets them to checked/unchecked state.
     */
    private void initViews() {
        setCardListener(R.id.accomodation_card_give, R.id.accomodation_card_edittext_give);
        setCardListener(R.id.food_card_give, R.id.food_card_edittext_give);
        setCardListener(R.id.clothes_card_give, R.id.clothes_card_edittext_give);
        setCardListener(R.id.medicine_card_give, R.id.medicine_card_edittext_give);
        setCardListener(R.id.other_card_give, R.id.other_card_edittext_give);

        setCardChecked(R.id.accomodation_card_give, R.id.accomodation_card_edittext_give, ACCOMODATION_GIVE_TEXT);
        setCardChecked(R.id.food_card_give, R.id.food_card_edittext_give, FOOD_GIVE_TEXT);
        setCardChecked(R.id.clothes_card_give, R.id.clothes_card_edittext_give, CLOTHES_GIVE_TEXT);
        setCardChecked(R.id.medicine_card_give, R.id.medicine_card_edittext_give, MEDICINE_GIVE_TEXT);
        setCardChecked(R.id.other_card_give, R.id.other_card_edittext_give, OTHER_GIVE_TEXT);

    }

    // Sets categories cards to checked/unchecked state.
    private void setCardChecked(final int cardID, final int editTextID, String editTextSPKey) {
        ViewGroup card = Objects.requireNonNull(getActivity()).findViewById(cardID);

        if (isCardPressed.get(cardID)) {
            card.findViewById(editTextID).setVisibility(View.VISIBLE);
            card.setBackgroundColor(getResources().getColor(R.color.checked_toggle));
            String reasonText = getActivity()
                    .getSharedPreferences(HELP_CATEGORIES, Context.MODE_PRIVATE)
                    .getString(editTextSPKey, null);
            EditText editText = getActivity().findViewById(editTextID);
            if (reasonText != null) {
                editText.setText(reasonText);
            } else {
                editText.setText("");
            }
            isCardPressed.put(cardID, true);
        } else {
            card.findViewById(editTextID).setVisibility(View.GONE);
            card.setBackgroundColor(getResources().getColor(R.color.unchecked_toggle));
            isCardPressed.put(cardID, false);
        }
    }

    // Sets onClickListeners for categories cards.
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
