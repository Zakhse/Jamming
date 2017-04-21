package ru.zakhse.spinner;

import javafx.event.*;
import javafx.scene.control.*;
import javafx.util.StringConverter;

/**
 * Converter for {@link Spinner<Integer>}
 *
 * @author Zakhse
 * @version 1.0
 */
public class IntegerStringConverter extends StringConverter<Integer> {

    private SpinnerValueFactory.IntegerSpinnerValueFactory spinnerFactory;

    /**
     * Creates an {@link IntegerStringConverter} for specified {@link Spinner<Integer>}
     * Restricts values in spinner to its min and max and resets the value to the previous one when invalid text is
     * committed.
     *
     * @param spinner the {@link Spinner<Integer>} providing user-edited numbers
     * @throws NullPointerException if {@code spinner} is {@code null}
     */
    private IntegerStringConverter(Spinner<Integer> spinner) {
        if (spinner == null)
            throw new NullPointerException("spinner");
        spinnerFactory = (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();

        final EventHandler<ActionEvent> oldHandler = spinner.getEditor().getOnAction();
        spinner.getEditor().setOnAction(t -> {
            try {
                fromString(spinner.getEditor().getText());
            } catch (IllegalArgumentException e) {spinner.getEditor().setText(spinner.getValue().toString());}

            if (oldHandler != null) oldHandler.handle(t);
        });
    }

    /**
     * Creates an {@link IntegerStringConverter} for the specified {@link Spinner}.
     * Sets the new {@link IntegerStringConverter} on its {@link SpinnerValueFactory.IntegerSpinnerValueFactory}.
     *
     * @param spinner the {@link Spinner} to create an {@link IntegerStringConverter} for
     * @return the new {@link IntegerStringConverter}
     * @throws NullPointerException if {@code spinner} is {@code null}
     */
    public static IntegerStringConverter createFor(Spinner<Integer> spinner) {
        final SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                (SpinnerValueFactory.IntegerSpinnerValueFactory) spinner.getValueFactory();

        final IntegerStringConverter converter = new IntegerStringConverter(spinner);

        factory.setConverter(converter);
        return converter;
    }

    /**
     * Converts the specified {@link String} into its {@link Integer} value.
     * A {@code null}, empty, or otherwise invalid argument returns zero
     * and also executes the editor reset callback, if any.
     *
     * @param s the {@link String} to convert
     * @return the {@link Integer} value of {@code s}
     * @throws IllegalArgumentException if value can't be parsed to {@link Integer} or is out of min and max bounds
     */
    @Override
    public Integer fromString(String s) throws IllegalArgumentException {
        if (s == null || s.isEmpty())
            return 0;

        Integer kek;
        kek = Integer.valueOf(s);
        if (kek > spinnerFactory.getMax() || kek < spinnerFactory.getMin())
            throw new IllegalArgumentException("Value is out of bounds.");
        return kek;
    }

    /**
     * Converts the specified {@link Integer} into its {@link String} form.
     * A {@code null} argument is converted into the literal string "0".
     *
     * @param value the {@link Integer} to convert
     * @return the {@link String} form of {@code value}
     */
    @Override
    public String toString(Integer value) {
        if (value == null) return "0";
        return Integer.toString(value);
    }
}
