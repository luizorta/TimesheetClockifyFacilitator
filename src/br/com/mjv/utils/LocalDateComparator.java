package br.com.mjv.utils;

import java.time.LocalDate;
import java.util.Comparator;

public class LocalDateComparator implements Comparator<LocalDate> {

    public int compare(LocalDate d1, LocalDate d2) {
        return d1.compareTo(d2);
    }
}