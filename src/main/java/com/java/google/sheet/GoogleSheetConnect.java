package com.java.google.sheet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

public class GoogleSheetConnect {

	private static final String[] characterList = { "Shank's Crew", "V3 Zoro", "Anni Zoro", "STND Law",
			"6+ V2 BlackBeard", "STND Kid", "Ace/Sabo", "Ace/Yamato", "6+ V1 Sanji", "6+ V2 Zoro", "6+ v1 Mihawk",
			"HW Law", "6+ V1 Nami", "VS Akainu", "V1 Queen", "6+ TSL", "6+ ST Sabo", "Rush Kaido" };

	private static final List<String> stndCharacters = Arrays.asList("STND Law", "STND Kid");

	private static final List<String> rushCharacters = Arrays.asList("Rush Kaido");

	private static final List<String> ltCharacters = Arrays.asList("Ace/Yamato");

	private static final String rush = "Rush ";

	private static final String stnd = "STND ";

	private static final String lt = "LT ";

	private static Credential authorize() throws Exception {

//		FileReader fr = new FileReader(
//				Thread.currentThread().getContextClassLoader().getResource("credentials.json").getPath());

		String credentialLocation = System.getProperty("user.dir") + "/credentials.json";
		FileReader fr = new FileReader(credentialLocation);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), fr);

		List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

		GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
				GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), clientSecrets, scopes)
						.setDataStoreFactory(new FileDataStoreFactory(new File("src/main/resources")))
						.setAccessType("offline").build();

		return new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, new LocalServerReceiver())
				.authorize("user");
	}

	public static String[][] getData(String spreadSheetId, String sheetName, String rangeDataToRead) throws Exception {
		Sheets sheet = new Sheets(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(),
				authorize());

		List<List<Object>> data = sheet.spreadsheets().values().get(spreadSheetId, sheetName + "!" + rangeDataToRead)
				.execute().getValues();

		return convertToArray(data);
	}

	private static String[][] convertToArray(List<List<Object>> data) {
		String[][] array = new String[data.size()][];

		int i = 0;
		for (List<Object> row : data) {
			array[i++] = row.toArray(new String[row.size()]);
		}
		return array;
	}

	public static void main(String[] args) throws Exception {

		String spreadSheetId = "1wCQrrdHvafGajGXS_SFmoLeRpqD_bnGZFp186XeNUxw";
		String sheetName = "Form responses 1";
		String range = "";
		int startingPoint = 3;
		int discordNameNum = 1;

		File file = new File(System.getProperty("user.dir") + "/output.txt");

		FileWriter fileWriter = new FileWriter(file);
		StringBuffer fileOutput = new StringBuffer();
		while (org.apache.commons.lang3.StringUtils.isEmpty(range)) {

			Scanner myObj = new Scanner(System.in); // Create a Scanner object
			System.out.println("please enter start row no: ");
			String startRow = myObj.next();
			if (StringUtils.isEmpty(startRow))
				continue;
			System.out.println("please enter end row no: ");
			String endRow = myObj.next();
			if (StringUtils.isEmpty(endRow))
				continue;
			range = "A" + startRow + ":" + "BD" + endRow;
			myObj.close();
		}

		String[][] excel = getData(spreadSheetId, sheetName, range);
		for (String[] row : excel) {
			int characterNo = 1;

			for (String character : characterList) {
				String discordName = row[discordNameNum];

				if (row.length > (characterNo * startingPoint) + 1) {
					String levelLimitBreak = row[characterNo * startingPoint - 1];
					if (org.apache.commons.lang3.StringUtils.isNotEmpty(levelLimitBreak)) {
						String limitBreak = row[(characterNo * startingPoint)];
						String stndRushLtPA = row[(characterNo * startingPoint) + 1];

						String PA = rushCharacters.contains(character) ? rush
								: stndCharacters.contains(character) ? stnd
										: ltCharacters.contains(character) ? lt : "";
						String stndRushLtLevel = org.apache.commons.lang3.StringUtils.isNotEmpty(stndRushLtPA)
								? stndRushLtPA
								: "";

//						System.out.println(discordName + "\t" + character + "\tLLB" + levelLimitBreak + "\t"
//								+ limitBreak + "\t" + PA + stndRushLtLevel);
						fileOutput.append(discordName + "\t" + character + "\tLLB" + levelLimitBreak + "\t" + limitBreak
								+ "\t" + PA + stndRushLtLevel + System.lineSeparator());
					}
					characterNo += 1;
				}
			}

		}

		fileWriter.write(fileOutput.toString());
		fileWriter.flush();
		fileWriter.close();
	}

}
