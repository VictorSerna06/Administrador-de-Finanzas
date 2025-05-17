package org.vgomez.personalfinacemanager.validator;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {

    // Verifica que el monto sea mayo a 0
    public static boolean validateAmount(double amount) {
        if (amount > 0) return true;
        return false;
    }

    // Verifica que la fecha este en el formato correcto
    public static boolean validateDate(String datestr) {
        if (datestr != null || !datestr.isBlank()) {
            try {
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                formato.parse(datestr);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        }
        return false;
    }

    // Verifica que no sea un campo vacío o nulo
    public static boolean validateCategory(String category){
        if(category != null || !category.isBlank()) return true;
        return false;
    }

    // Verifica que no sea un campo vacío o nulo
    public static boolean validateType(String type){
        if(type != null || !type.isBlank()) return true;
        return false;
    }
}
