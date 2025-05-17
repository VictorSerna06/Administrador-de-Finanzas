package org.vgomez.personalfinacemanager.financeapp;

import org.vgomez.personalfinacemanager.transaction.Category;
import org.vgomez.personalfinacemanager.transaction.Transaction;
import org.vgomez.personalfinacemanager.transaction.TransactionType;
import org.vgomez.personalfinacemanager.transactionservice.TransationService;
import org.vgomez.personalfinacemanager.validator.Validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Instancia de la clase Scanner
    private static final Scanner S = new Scanner(System.in);
    // Instancia de la clase TransactionService
    private static TransationService TRANSACTIONSERVICE = new TransationService();

    public static void main(String[] args) {
        boolean exit = false;

        // Bucle principal
        while (!exit) {

            // Muestra el menú de opciones
            showMenu();

            // Se obtiene el valor ingresado por el usuario
            String inUser = S.nextLine();

            // Se llama al método que coinside con la entrada del usuario
            switch (inUser) {
                case "1" -> addTransaction();
                case "2" -> transactionList();
                case "3" -> showIncomeExpenses();
                case "4" -> showForCategory();
                case "5" -> showForRangeDate();
                case "6" -> searchInDescription();
                case "7" -> deleteForId();
                case "8" -> balance();
                case "0" -> {
                    System.out.println("\nSaliendo del programa...");
                    exit = true;
                }
                default -> System.out.println("\nOpción invalida, intenta nuevamente.\n");
            }
        }
    }

    // Menú de opciones
    private static void showMenu() {
        System.out.println("""
                ------------- BIENVENIDO -------------
                1.- Agregar una transacción.
                2.- Ver todas las transacciones.
                3.- Ver ingresos / gastos.
                4.- Filtrar por categoría.
                5.- Filtrar por fechas.
                6.- Buscar por descripción.
                7.- Eliminar transacción por ID.
                8.- Ver resumen financiero.
                0.- Salir.
                                
                Elige una opción:
                """);
    }

    // Agrega una nueva transacción y se validan todas las entradas del usuario
    private static void addTransaction() {
        System.out.println("\nIngresa el monto de la transacción:");
        String am = S.nextLine();
        if (validateAmount(am) <= 0) {
            System.out.println("Intentalo de nuevo.\n");
            return;
        }
        double amount = validateAmount(am);

        System.out.println("""
                \nIngresa el tipo de transacción: 
                - INGRESO
                - GASTO
                """);
        String t = S.nextLine().trim();
        if (validateType(t) == null) {
            System.out.println("Intentalo de nuevo.\n");
            return;
        }
        TransactionType type = validateType(t);

        System.out.println("""
                \nIngresa la categoría de la transacción:
                - ALIMENTACION
                - TRANSPORTE
                - OCIO
                - SALARIO
                - OTROS
                """);
        String c = S.nextLine().trim();
        if (validateCategory(c) == null) {
            System.out.println("Intentalo de nuevo.\n");
            return;
        }
        Category category = validateCategory(c);

        System.out.println("\nIngresa la fecha de la transacción (dd-mm-yyyy):");
        String date = S.nextLine().trim();
        if (!Validator.validateDate(date)) {
            System.out.println("\nFormato de fecha incorrecto.\nIntentalo de nuevo.\n");
            return;
        }

        System.out.println("\nIngresa una descripción:");
        String description = S.nextLine().trim();
        if (description.isBlank()) {
            System.out.println("\nEl campo no puede estar vacío.\nIntentado de nuevo.\n");
            return;
        }

        Transaction transaction = new Transaction(amount, type, category, date, description);
        boolean newTransaction = TRANSACTIONSERVICE.addTransaction(transaction);

        if (newTransaction) {
            System.out.println("\nTransacción guardada exitosamente.\n");
        } else {
            System.out.println("\nAlgo salio mal.\n");
        }
    }


    // Se valida que el monto de la transacción sea mayor a 0 y que no contenga letras
    private static double validateAmount(String am) {
        try {
            double amount = Double.parseDouble(am);

            if (!Validator.validateAmount(amount)) {
                System.out.println("\nMonto invalido, debe ser mayor a 0.\n");
            }
            return amount;
        } catch (NumberFormatException n) {
            System.out.println("\nEl campo no puede estar vacío o contener letras.\n");
            return 0;
        }
    }

    // Se valida que el tipo de la transacción contenga solo uno de los valores permitidos
    private static TransactionType validateType(String t) {
        TransactionType type;
        if (Validator.validateType(t)) {
            if (!t.equalsIgnoreCase("ingreso") && !t.equalsIgnoreCase("gasto")) {
                System.out.println("\nTipo de transacción invalida, debe ser (INGRESO / GASTO).\n");
            }
        }
        switch (t) {
            case "ingreso" -> type = TransactionType.INGRESO;
            case "gasto" -> type = TransactionType.GASTO;
            default -> type = null;
        }
        return type;
    }

    // Se valida que la categoría de la transacción contenga solo uno de los valores permitidos
    private static Category validateCategory(String c) {
        Category category;
        if (Validator.validateCategory(c)) {
            if (!c.equalsIgnoreCase("alimentacion") && !c.equalsIgnoreCase("transporte") && !c.equalsIgnoreCase("ocio") && !c.equalsIgnoreCase("salario") && !c.equalsIgnoreCase("otros")) {
                System.out.println("\nCategoría invalida, debe ser: (ALIMENTACION / TRANSPORTE / OCIO / SALARIO / OTROS).\n");
            }
        }
        switch (c) {
            case "alimentacion" -> category = Category.ALIMENTACION;
            case "transporte" -> category = Category.TRANSPORTE;
            case "ocio" -> category = Category.OCIO;
            case "salario" -> category = Category.SALARIO;
            case "otros" -> category = Category.OTROS;
            default -> category = null;
        }
        return category;
    }

    // Muestra una lista de todas las transacciones, si la lista esta vacía muestra un mensaje
    private static void transactionList() {
        List<Transaction> lista = TRANSACTIONSERVICE.listAll();
        if (lista.isEmpty()) {
            System.out.println("\nLista de transacciones esta vacía.\n");
            return;
        }
        System.out.println("\n------- Lista de Transacciones -------\n");
        lista.forEach(System.out::println);
    }

    // Muestra todos los ingresos o todos los gastos
    // Se valida que la entrada del usuario contenga uno de los valores permitidos, también que el campo no este vacío
    private static void showIncomeExpenses() {
        System.out.println("\nIngresa 'g' para GASTOS o 'i' para INGRESOS:");
        String response = S.nextLine().trim();
        if (response.isBlank()) {
            System.out.println("\nEl campo no puede estar vacío.\nIntentalo de nuevo.");
        } else if (response.equalsIgnoreCase("g")) {
            System.out.println("\n---------- Total de gastos ----------");
            System.out.println("\n$" + TRANSACTIONSERVICE.getTotalExpense() + "\n");
        } else if (response.equalsIgnoreCase("i")) {
            System.out.println("\n--------- Total de ingresos ---------");
            System.out.println("\n$" + TRANSACTIONSERVICE.getTotalIncome() + "\n");
        } else {
            System.out.println("\nOperación invalida.\nIntentalo de nuevo.");
        }
    }

    // Muestra una lista de las transacciones filtradas por categoría
    // Se validan todos los datos ingresados
    private static void showForCategory() {
        System.out.println("""
                \nIngresa la categoría por la que quieres filtrar:
                - ALIMENTACION
                - TRANSPORTE
                - OCIO
                - SALARIO
                - OTROS """);
        String c = S.nextLine().trim();
        Category category = validateCategory(c);
        List<Transaction> listCategory = TRANSACTIONSERVICE.listByCategory(category);
        if (listCategory.isEmpty()) {
            System.out.println("No se encontró ningúna transacción con la categoría '" + c.toUpperCase() + "'.\n");
        } else {
            System.out.println("\n-------- Lista por categoría '" + c.toUpperCase() + "' --------\n");
            listCategory.forEach(System.out::println);
        }
    }

    // Muestra una lista de las transacciones filtradas por fecha (inicial y final)
    // Se validan todos los datos ingresados
    private static void showForRangeDate() {
        System.out.println("\nIngresa la fecha inicial (dd-mm-yyyy):");
        String f = S.nextLine().trim();
        if (f.isBlank()) {
            System.out.println("\nEl campo no pueden estar vacío.\n");
            return;
        }
        System.out.println("\nIngresa la fecha final (dd-mm-yyyy):");
        String t = S.nextLine().trim();
        if (t.isBlank()) {
            System.out.println("\nEl campo no pueden estar vacío.\n");
            return;
        }

        LocalDate from = transformDate(f);
        LocalDate to = transformDate(t);

        if (from == null || to == null) {
            System.out.println("\nFormato de fecha incorrecto.\n");
            return;
        }
        List<Transaction> listRangeDate = TRANSACTIONSERVICE.listByDateRange(from, to);

        if (listRangeDate.isEmpty()) {
            System.out.println(String.format("\nNo se encontraron transacciones entre (%s y %s)\n", f, t));
        } else {
            System.out.println(String.format("""
                    \n--------- Lista de transacciones --------
                    Desde: %s
                    Hasta: %s
                    """, f, t));
            listRangeDate.forEach(System.out::println);
        }
    }

    // Devuelve la fecha en el formato correcto a partir de una cadena de texto con la fecha
    // Se valida que el formato de la fecha sea correcto
    private static LocalDate transformDate(String d) {
        if (d == null) {
            return null;
        }

        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(d, format);
            return date;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // Muestra una lista de las transacciones que contengan la palabra(s) clave en su descripción
    // Se valida que el campo no se encuentre vacío
    private static void searchInDescription() {
        System.out.println("\nIngresa una o mas palabras clave para iniciar la búsqueda:");
        String wk = S.nextLine().trim();
        if (wk.isBlank()) {
            System.out.println("\nEl campo no puede estar vacío, debes ingresar una palabra.\n");
            return;
        }
        List<Transaction> listWordKey = TRANSACTIONSERVICE.searchByKeyword(wk);
        if (listWordKey.isEmpty()) {
            System.out.println(String.format("\nNo se encontró ningúna transacción que contenga '%s' en su descripción.\n", wk.toUpperCase()));
            return;
        } else {
            System.out.println(String.format("\n-------- Lista de transacciones que contienen %s --------\n", wk.toUpperCase()));
            listWordKey.forEach(System.out::println);
        }
    }

    // Elimina una transacción por id
    // Se valida que el campo no se encuentre vacío
    private static void deleteForId() {
        System.out.println("\nIngresa el ID de la transacción que quieres eliminar:");
        String idStr = S.nextLine().trim();
        if (idStr.isBlank()) {
            System.out.println("\nEl campo no puede estar vacío.\n");
            return;
        }

        int id = validateId(idStr);
        boolean isDelete = TRANSACTIONSERVICE.removeTransaction(id);

        if (id <= 0) {
            System.out.println("\nEl campo no puede contener letras o debe ser mayor a 0.\n");
            return;
        }
        if (isDelete) {
            System.out.println(String.format("\nTransacción con id: %s eliminada.\n", idStr));
        } else {
            System.out.println(String.format("\nNo se puedo eliminar la transacción con el id: %s.\n", idStr));
        }

    }

    // Transforma una cadena de texto en un valor entero
    private static int validateId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Muestra todos los ingresos, gastos y muestra el resultado
    private static void balance() {
        double b = TRANSACTIONSERVICE.getBalance();
        double gastos = TRANSACTIONSERVICE.getTotalExpense();
        double ingresos = TRANSACTIONSERVICE.getTotalIncome();
        System.out.println("\n------------ Balance ------------\n");
        System.out.println(String.format("""
                Ingresos: $%.2f
                Gastos: -$%.2f
                Total: $%.2f
                """, ingresos, gastos, b));
    }
}
