package com.coservice.service;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coservice.binding.CoResponse;
import com.coservice.entity.CitizenDetailsEntity;
import com.coservice.entity.CoTrigger;
import com.coservice.entity.DcCaseEntity;
import com.coservice.entity.EligibilityEntity;
import com.coservice.repository.CitizenDetailsRepository;
import com.coservice.repository.CoTriggerRepository;
import com.coservice.repository.DCCasesRepository;
import com.coservice.repository.EligibilityRepository;
import com.coservice.utils.EmailsUtils;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class CoTriggerServiceImpl implements CoTriggerService {
	
	@Autowired
	private CoTriggerRepository corepo;
	
	@Autowired
	private EligibilityRepository eligrepo;
	
	@Autowired
	private DCCasesRepository dcrepo;
	
	@Autowired
	private CitizenDetailsRepository crepo;
	
	@Autowired
	private EmailsUtils eutils;

	@Override
	public CoResponse processPendingTriggers() {
		//EligibilityEntity eentity = null;
		//CitizenDetailsEntity centity = null;
		Long success = 0l;
		Long failed = 0l;
		
		CoResponse response = new CoResponse();
		//get all pending triggers
		List<CoTrigger> coentities = corepo.findByTriggerStatus("Pending");
		//count total number of triggers
		response.setTotalTriggers(Long.valueOf(coentities.size()));
		if(!coentities.isEmpty()) {
			//process each pending trigger
		for(CoTrigger coentity : coentities) {
			try {
				processTrigger(coentity);
				success++;
			} catch (Exception e) {
				e.printStackTrace();
				failed++;
			}
		}
		
		//set success count
		response.setSuccessTriggers(success);
		//set failed count
		response.setFailedTriggers(failed);
		}
		
		return response;
	}
	private CitizenDetailsEntity processTrigger(CoTrigger coentity) throws Exception {
		CitizenDetailsEntity   centity = null;
		//get eligibility data based on casenum
		EligibilityEntity  eentity = eligrepo.findByCaseNum(coentity.getCaseNum());
		
		Optional<DcCaseEntity> dcoptional = dcrepo.findById(coentity.getCaseNum());
		if(dcoptional.isPresent()) {
			DcCaseEntity dcentity = dcoptional.get();
			Integer appId = dcentity.getAppId();
			Optional<CitizenDetailsEntity> coptional = crepo.findById(appId);
			if(coptional.isPresent()) {
				centity  = coptional.get();
			}
		}
		generateAndsentPdf(eentity, centity);
		
		return centity;
	}
	
    private void generateAndsentPdf(EligibilityEntity eentity, CitizenDetailsEntity centity) throws Exception {
    	
    	String holderName = null;
		String planName = null;
		String planStatus = null;
		FileOutputStream fos = null;
		File file = new  File(eentity.getCaseNum()+".pdf");
		String file1 = "citizen-email.txt";
		String subject = "HIS Plan Application";


		Document document = new Document(PageSize.A4);
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);

		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(18);
		font.setColor(Color.BLUE);

		Paragraph p = new Paragraph("Citizen Notice", font);
		p.setAlignment(Paragraph.ALIGN_CENTER);

		document.add(p);

		PdfPTable table = new PdfPTable(7);
		table.setWidthPercentage(100f);
		table.setWidths(new float[] { 1.5f, 3.5f, 3.0f, 3.0f, 1.5f, 1.5f, 3.0f });
		table.setSpacingBefore(10);

		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(5);

		Font font1 = FontFactory.getFont(FontFactory.HELVETICA);
		font1.setColor(Color.WHITE);

		cell.setPhrase(new Phrase("Holder Name", font1));

		table.addCell(cell);

		cell.setPhrase(new Phrase("PlanName", font1));
		table.addCell(cell);

		cell.setPhrase(new Phrase("PlanStatus", font1));
		table.addCell(cell);

		cell.setPhrase(new Phrase("PlanStartDate", font1));
		table.addCell(cell);

		cell.setPhrase(new Phrase("PlanEndDate", font1));
		table.addCell(cell);

		cell.setPhrase(new Phrase("BenefieAmount", font1));
		table.addCell(cell);

		cell.setPhrase(new Phrase("Denial Reason", font1));
		table.addCell(cell);
		
		holderName = eentity.getHolderName();
		planName = eentity.getPlanName();
		planStatus = eentity.getPlanStatus();

		table.addCell(holderName);
		table.addCell(planName);
		table.addCell(planStatus);
		table.addCell(eentity.getPlanStartDate());
		table.addCell(eentity.getPlanEndDate());
		table.addCell(eentity.getBenefitAmnt());
		table.addCell(eentity.getDenialReason());
		
		document.add(table);
		document.close();
		
		pdfWriter.flush();
		
		//sending email
		String mailBody = getMailBody(holderName, planName, planStatus, file1);
		
		eutils.sendEmail(centity.getEmail(), subject, mailBody, file);
		updateTrigger(eentity.getCaseNum(), file);
		file.delete();
    }
    
    private String getMailBody(String holderName, String planName, String planStatus, String filename) {
		String line = null;
		try (FileReader reader = new FileReader(filename);
				BufferedReader bufferedreader1 = new BufferedReader(reader)) {

			StringBuilder builder = new StringBuilder();
			String readLine = bufferedreader1.readLine();
			while (readLine != null) {
				builder.append(readLine);
				readLine = bufferedreader1.readLine();
			}
			bufferedreader1.close();
			line = builder.toString();
			line = line.replace("{FULLNAME}", holderName);
			line = line.replace("{PLANNAME}", planName);
			line = line.replace("{PLANSTATUS}", planStatus);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return line;
	}
    
	private void updateTrigger(Long caseNum, File file) throws Exception{
    	CoTrigger coentity = corepo.findByCaseNum(caseNum);
    	if(coentity != null) {
    		byte[] arr = new byte[(byte)file.length()];
				FileInputStream fis = new FileInputStream(file);
				fis.read(arr);
    		coentity.setCoPdf(arr);
    		coentity.setTriggerStatus("Completed");
    		corepo.save(coentity);
    		fis.close();
    	}
    }
}
