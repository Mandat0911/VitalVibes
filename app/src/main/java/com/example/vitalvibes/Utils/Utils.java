package com.example.vitalvibes.Utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
public class Utils {
    /**
     * Validates the format and logical correctness of a date in "dd/mm/yyyy" format.
     *
     * @param dob The date of birth string to validate.
     * @return True if the DOB is valid, otherwise false.
     */
    public static boolean isValidDob(String dob) {
        // Regular expression to match the format dd/mm/yyyy
        String dobPattern = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";

        // Check if the input matches the format
        if (!dob.matches(dobPattern)) {
            return false;
        }

        // Validate the actual date (e.g., no 30th February)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false); // Disable leniency to strictly validate date

        try {
            dateFormat.parse(dob);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
