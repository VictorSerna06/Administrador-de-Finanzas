package org.vgomez.personalfinacemanager.transactionservice;

import org.vgomez.personalfinacemanager.file.FileHandler;
import org.vgomez.personalfinacemanager.transaction.Category;
import org.vgomez.personalfinacemanager.transaction.Transaction;
import org.vgomez.personalfinacemanager.transaction.TransactionType;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransationService {

    // Contiene todas las transacciones
    private List<Transaction> transactions = FileHandler.loadTransaction();

    // Devuelve una lista de todas las transacciones
    // Si no hay transacciones devuelve una lista vacía
    public List<Transaction> listAll() {return new ArrayList<>(transactions);}

    // Agrega una nueva transacción a la lista
    // Guarda todas las transacciones en un archivo de texto
    public boolean addTransaction(Transaction t) {
        if(t != null){
            transactions.add(t);
            FileHandler.saveTransaction(transactions);
            return true;
        }
        return false;
    }

    // Elimina una transacción por el número de id
    // Actualiza el archivo después de eliminar una transacción
    public boolean removeTransaction(int id) {
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == id) {
                iterator.remove();
                FileHandler.saveTransaction(transactions);
                return true;
            }
        }
        return false;
    }

    // Devuelve una lista de todas las transacciones que coinsida con la categoría ingresada
    public List<Transaction> listByCategory(Category c) {
        List<Transaction> categoryList = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getCategory().equals(c)) {
                categoryList.add(t);
            }
        }
        return categoryList;
    }

    // Devuelve una lista con todas las transacciones que se encuentren en las fechas ingresadas (from, to)
    public List<Transaction> listByDateRange(LocalDate from, LocalDate to) {
        List<Transaction> dateRangeList = new ArrayList<>();
        if (from.isEqual(to)) {
            return dateRangeList;
        }
        if (from.isBefore(to)) {
            for (Transaction t : transactions) {
                LocalDate dateTransaction = t.getDate();
                if (!dateTransaction.isBefore(from) && !dateTransaction.isAfter(to)) {
                    dateRangeList.add(t);
                }
            }
        }
        return dateRangeList;
    }

    // Devuelve una lista de todas las transacciones que contengan la palabra clave ingresada
    public List<Transaction> searchByKeyword(String keyword) {
        String kw = keyword.trim().toLowerCase();
        List<Transaction> kwTransactionList = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getDescription().toLowerCase().contains(kw)) {
                kwTransactionList.add(t);
            }
        }
        return kwTransactionList;
    }

    // devuelve el total de Ingresos
    public double getTotalIncome() {
        double result = 0;
        for (Transaction t : transactions) {
            if (t.getType().equals(TransactionType.INGRESO)) {
                result += t.getAmount();
            }
        }
        return Math.round(result * 100.00) / 100.00;
    }

    // Devuelve el total de gastos
    public double getTotalExpense() {
        double result = 0;
        for (Transaction t : transactions) {
            if (t.getType().equals(TransactionType.GASTO)) {
                result += t.getAmount();
            }
        }
        return Math.round(result * 100.00) / 100.00;
    }

    // Devuelve la diferencia de gastos e ingresos
    public double getBalance() {
        double balance = getTotalIncome() - getTotalExpense();
        return Math.round(balance * 100.00) / 100.00;
    }
}
