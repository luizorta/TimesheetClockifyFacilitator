package br.com.mjv.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.mjv.dto.Atividade;

public class Utils {
	
public static void removerDuplicados(List<Atividade> atividades) {
		
		Collections.sort(atividades);
		
		List<Atividade> novaLista = new ArrayList<Atividade>();
		
		for(Atividade atividade: atividades) {
			
			int index = Collections.binarySearch(novaLista, atividade, new DataAtividadeComparator());
			
			if(index < 0){
				novaLista.add(atividade);
			}
		}
		
		atividades = novaLista;
		
	}

}
