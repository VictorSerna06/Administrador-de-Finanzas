package org.vgomez.personalfinacemanager.file;

import org.vgomez.personalfinacemanager.transaction.Category;
import org.vgomez.personalfinacemanager.transaction.Transaction;
import org.vgomez.personalfinacemanager.transaction.TransactionType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // Atributo el cúal define el nombre con el que se guardará el archivo de texto
    private static final String FILE_NAME= "transactions.txt";

    // Guarda el archivo con todas las transacciones
    public static void saveTransaction(List<Transaction> transactions){
        Path path = Paths.get(FILE_NAME);
        try(BufferedWriter write = Files.newBufferedWriter(path)){
            for(Transaction transaction: transactions){
                write.write(transactionsToLine(transaction));
                write.newLine();
            }
        }catch(IOException e){
            System.out.println("Error al guardar transacciones: " + e.getMessage());
        }
    }

    // Devuelve una cadena de texto por cada transacción
    private static String transactionsToLine(Transaction transaction){
        return "ID: " + transaction.getId() + "|" +
                "Fecha: " + transaction.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "|" +
                "Monto: " + transaction.getAmount() + "|" +
                "Tipo: " + transaction.getType() + "|" +
                "Categoria: " + transaction.getCategory() + "|" +
                "Descripcion:" + transaction.getDescription();
    }

    // Devuelve todas las transacciones que se encuentran en archivo de texto
    public static List<Transaction> loadTransaction(){
        List<Transaction> transactions = new ArrayList<>();
        Path path = Paths.get(FILE_NAME);

        if(!Files.exists(path)){
            return transactions;
        }
        try(BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while((line = reader.readLine()) != null){
                Transaction transaction = lineToTransaction(line);
                if(transaction != null){
                    transactions.add(transaction);
                }
            }
        }catch(IOException e){
            System.out.println("Error al cargar las transacciones: " + e.getMessage());
        }
        return transactions;
    }

    // Devuelve una transacción
    private static Transaction lineToTransaction(String line){
        try{
            return getData(line);
        }catch(Exception e){
            System.out.println("Error al convertir linea a tarea: " + e.getMessage());
            return null;
        }
    }

    // Transforma cada línea del archivo de texto y utiliza los datos necesarios para crear una transacción y devolverla
    private static Transaction getData(String strData){
        String[] keyValue = strData.split("\\|");
        double amount = 0d;
        TransactionType type = null;
        Category category = null;
        String date = "";
        String description = "";

        for(String value : keyValue){
            String[] data = value.split(":");
            if(value.length() > 1){
                switch(data[0].toLowerCase()){
                    case "monto" -> amount = Double.parseDouble(data[1]);
                    case "tipo" -> type = TransactionType.valueOf(data[1].trim().toUpperCase());
                    case "categoria" -> category = Category.valueOf(data[1].trim().toUpperCase());
                    case "fecha" -> date = data[1].trim();
                    case "descripcion" -> description = data[1].trim();
                }
            }
        }
        Transaction transaction = new Transaction(amount, type, category, date, description);
        return transaction;
    }
}
