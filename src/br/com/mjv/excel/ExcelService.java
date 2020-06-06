package br.com.mjv.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.mjv.dto.Atividade;
import br.com.mjv.utils.Util;

public class ExcelService {
	
	public void updatePlanilha(String nomeColaborador, List<Atividade> atividades, int ano, int mes) throws InvalidFormatException, IOException {

		File file = new File("entrada.xlsx");
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);

		/*
		 * Atualiza as informações do NOME DO COLABORADOR
		 */
		CellReference cellReference = new CellReference("E8");
		Row row = sheet.getRow(cellReference.getRow());
		Cell cell = row.getCell(cellReference.getCol());
		cell.setCellType(CellType.STRING);
		cell.setCellValue(nomeColaborador);

		/*
		 * Inicio das datas Domingos nao aparecem na planilha
		 * 
		 */
		List<LocalDate> datas = Util.getDatasMes(ano, mes);
		int i = 1;
		for (LocalDate data : datas) {

			try {

				for (Atividade atividade : atividades) {

					/*
					 * Adiciona o horário total do dia
					 */
					if (data.isEqual(atividade.getData())) {
						cellReference = new CellReference("B" + (i + 10));
						row = sheet.getRow(cellReference.getRow());
						cell = row.getCell(cellReference.getCol());
						cell.setCellType(CellType.STRING);
						cell.setCellValue(atividade.getTotalHoras() + ":00");
						// Sets the allignment to the created cell
						CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);

						/**
						 * Adiciona a descrição da atividade
						 */
						cellReference = new CellReference("D" + (i + 10));
						row = sheet.getRow(cellReference.getRow());
						cell = row.getCell(cellReference.getCol());
						cell.setCellType(CellType.STRING);
						cell.setCellValue(atividade.getDescricao());

						/**
						 * Adiciona a projeto
						 */
						cellReference = new CellReference("F" + (i + 10));
						row = sheet.getRow(cellReference.getRow());
						cell = row.getCell(cellReference.getCol());
						cell.setCellType(CellType.STRING);
						cell.setCellValue(atividade.getNomeProjeto());
						CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
					}

				}
			} catch (NullPointerException e) {
				System.out.println("ERRO: " + e);
			}

			i++;

		}

		/*
		 * Total de horas no mês
		 */
		cellReference = new CellReference("D40");
		row = sheet.getRow(cellReference.getRow());
		cell = row.getCell(cellReference.getCol());
		cell.setCellType(CellType.STRING);
		cell.setCellValue(Util.getTotalHorasMes(atividades) + ":00");

		// Write the output to the file
		FileOutputStream fileOut = new FileOutputStream("saida.xlsx");
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();

	}

}
