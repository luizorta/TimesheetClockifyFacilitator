package br.com.mjv.excel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.mjv.dto.Atividade;
import br.com.mjv.utils.DateUtils;

public class ExcelFacadeImpl implements ExcelFacade {
	
	public byte[] updatePlanilha(String nomeColaborador, List<Atividade> atividades, int ano, int mes)
			throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook(this.getClass().getResourceAsStream("entrada-08-2020.xlsx"));
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
		List<LocalDate> datas = DateUtils.getDatasMes(ano, mes);
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
						
						Duration duration = atividade.getTotalHoras();
						long s = duration.getSeconds();
						String duracaoDia = String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
						
						cell.setCellValue(duracaoDia);
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
						cell.setCellValue(atividade.getProjeto().getName());
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

		Duration duration = DateUtils.getTotalHorasMes(atividades);
		long s = duration.getSeconds();
		String duracaoMes = String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
		
		cell.setCellValue(duracaoMes);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
		    workbook.write(bos);
		} finally {
		    bos.close();
		}
		byte[] bytes = bos.toByteArray();
		
		workbook.write(bos);
		// Closing the workbook
		workbook.close();
		
		return bytes;
	}

}
