package org.vgomez.personalfinacemanager.transaction;

import org.vgomez.personalfinacemanager.validator.Validator;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Transaction {

    // Atributos
    private static int nextId;
    private int id;
    private double amount;
    private TransactionType type;
    private Category category;
    private LocalDate date;
    private String description;

    // Constructor vacío
    public Transaction(){
        this.id = ++nextId;
    }

    // Constructor con valores
    public Transaction(double amount, TransactionType type, Category category, String date, String description){
        this();
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = dateFormat(date);
        this.description = description;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    // Devuelve la fecha en el formato correcto
    public LocalDate dateFormat(String datesrt){
        if(Validator.validateDate(datesrt)){
            try{
                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate date = LocalDate.parse(datesrt, format);
                return date;
            }catch(DateTimeParseException e){
                e.getMessage();
            }
        }
        return null;
    }

    // Devuelve una cadena de texto a partir de un valor double
    private String decimalFormat(){
        double am = amount;
        DecimalFormat amountFormat = new DecimalFormat("#.00");
        return amountFormat.format(am);
    }

    // Devuelve el valor de cada atributo del objeto
    @Override
    public String toString(){
        return "id: " + id +
                "\nMonto: $" + decimalFormat() +
                "\nTipo: " + type +
                "\nCategoria: " + category +
                "\nFecha: " + date +
                "\nDescripción: " + description +
                "\n";
    }
}
