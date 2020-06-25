package br.com.mjv.utils;

import java.util.Comparator;

import br.com.mjv.dto.Atividade;

public class DataAtividadeComparator implements Comparator<Atividade> {

    public int compare(Atividade a1, Atividade a2) {
    	
        return a1.getData().compareTo(a2.getData());
    }
}