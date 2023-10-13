package com.katalon.plugin.keyword.databinding

import java.nio.file.*;
import java.text.MessageFormat
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.ss.usermodel.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ObjectRepository

public class ParallelExecution {

	private KeywordLogger logger = KeywordLogger.getInstance(ObjectRepository.class);
	String strProjectPath = RunConfiguration.getProjectDir()

	@Keyword
	public void buildTestSuiteParallelExecution(String testsuitecollectionpath) {

		String strTestSuiteCollectionPath = ('/' + testsuitecollectionpath) + '.ts'

		cleanExistingSetup(strProjectPath + strTestSuiteCollectionPath)
		deleteFilesWithSubstring(strProjectPath +'/Test Suites', '_New_')

		String executionMode = getValueBetweenXmlTags(strProjectPath + strTestSuiteCollectionPath, 'executionMode')


		String maxConcurrentInstances = getValueBetweenXmlTags(strProjectPath + strTestSuiteCollectionPath, 'maxConcurrentInstances')


		List<String> testSuiteEntity = extractAllValuesBetweenTag(strProjectPath + strTestSuiteCollectionPath, 'testSuiteEntity')

		List<String> runrunConfigurationId = extractAllValuesBetweenTag(strProjectPath + strTestSuiteCollectionPath, 'runConfigurationId')

		List<String> profileName = extractAllValuesBetweenTag(strProjectPath + strTestSuiteCollectionPath, 'profileName')

		for(int index=0;index < testSuiteEntity.size(); index++) {


			List<String> lstCaseEntity = extractAllValuesBetweenTag(((strProjectPath + '/') + testSuiteEntity.get(index)) + '.ts', 'testCaseId')
			List<String> lstDataId = extractAllValuesBetweenTag(((strProjectPath + '/') + testSuiteEntity.get(index)) + '.ts', 'testDataId')

			List<String> lstRowcount = new ArrayList<>()
			int minRowCount=1
			int rowCount
			for(int i=0;i<lstDataId.size();i++) {
				String strDriver = getValueBetweenXmlTags(((strProjectPath + '/') + lstDataId.get(i)) + '.dat', 'driver')

				if (strDriver=="InternalData") {
					rowCount = countXmlTagsInFile(((strProjectPath + '/') + lstDataId.get(i)) + '.dat', 'data')
					if(rowCount > minRowCount){
						minRowCount=rowCount
					}else{
						minRowCount=rowCount
					}
					lstRowcount.add(rowCount)
				}else if((strDriver=="ExcelFile")) {
					String strdataSourceUrl = getValueBetweenXmlTags(((strProjectPath + '/') + lstDataId.get(i)) + '.dat', 'dataSourceUrl')
					String strsheetName = getValueBetweenXmlTags(((strProjectPath + '/') + lstDataId.get(i)) + '.dat', 'sheetName')
					String filename = strProjectPath +'/' + strdataSourceUrl;
					logger.logDebug("filename: "+filename)
					rowCount = excelRowCount(filename, strsheetName)
					if(rowCount > minRowCount){
						minRowCount=rowCount
					}else{
						minRowCount=rowCount
					}

					lstRowcount.add(rowCount)
				}
				else if((strDriver=="CSV")) {
					String strdataSourceUrl = getValueBetweenXmlTags(((strProjectPath + '/') + lstDataId.get(i)) + '.dat', 'dataSourceUrl')
					String filename = strProjectPath +'/' + strdataSourceUrl;
					logger.logDebug("filename: "+filename)
					rowCount = csvRowCount(filename)
					if(rowCount > minRowCount){
						minRowCount=rowCount
					}else{
						minRowCount=rowCount
					}
					lstRowcount.add(rowCount)
				}
				else if((strDriver=="DBData")) {
					String strdataSourceUrl = getValueBetweenXmlTags(((strProjectPath + '/') + lstDataId.get(i)) + '.dat', 'query')
					/*
					 * user need to connect with database to get the total no of rows in db.
					 */
				}else{
					logger.logDebug("Not supported Data File Type")
				}
			}

			List<String> lstDuplicateFiles

			if (minRowCount < Integer.parseInt(maxConcurrentInstances)) {
				lstDuplicateFiles = createMultipalFiles(((strProjectPath + '/') + testSuiteEntity.get(index)) + '.ts', minRowCount)
			} else {
				lstDuplicateFiles = createMultipalFiles(((strProjectPath + '/') + testSuiteEntity.get(index)) + '.ts', Integer.parseInt(maxConcurrentInstances))
			}

			//nested list
			List<List<String>> lstIterationTypeValue = generateIiterationTypeAndValue(lstRowcount, maxConcurrentInstances)

			updateValuesBetweenTags(lstDuplicateFiles, lstCaseEntity, lstIterationTypeValue, Integer.toString(lstDuplicateFiles.size()))

			List<String> resultList = extractTestSuiteFromContent(lstDuplicateFiles)


			String content = ''

			String baseContent = ''

			for (int i = 1; i < resultList.size(); i++) {
				if ((i + 1) == resultList.size()) {
					baseContent = (('<TestSuiteRunConfiguration>\n<configuration>\n<groupName>Web Desktop</groupName>\n<profileName>'+profileName.get(index)+'</profileName>\n<requireConfigurationData>false</requireConfigurationData>\n<runConfigurationId>'+runrunConfigurationId.get(index)+'</runConfigurationId>\n</configuration>\n<runEnabled>true</runEnabled>\n<testSuiteEntity>' +
							resultList.get(i)) + '</testSuiteEntity>\n</TestSuiteRunConfiguration>')
				} else {
					baseContent = (('<TestSuiteRunConfiguration>\n<configuration>\n<groupName>Web Desktop</groupName>\n<profileName>'+profileName.get(index)+'</profileName>\n<requireConfigurationData>false</requireConfigurationData>\n<runConfigurationId>'+runrunConfigurationId.get(index)+'</runConfigurationId>\n</configuration>\n<runEnabled>true</runEnabled>\n<testSuiteEntity>' +
							resultList.get(i)) + '</testSuiteEntity>\n</TestSuiteRunConfiguration>\n')
				}

				content == baseContent

				content = (content + baseContent)
			}

			addNewDataIntoXMLFile(strProjectPath + strTestSuiteCollectionPath,'</TestSuiteRunConfiguration>', content)
		}

	}

	@Keyword
	public void updateInternalDataFile(String strTestSuiteCollectionPath, String dataset) {
		String strTestSuiteCollection = ('/' + strTestSuiteCollectionPath) + '.ts'
		String testSuiteEntity = getValueBetweenXmlTags(strProjectPath + strTestSuiteCollection, 'testSuiteEntity')
		String testDataId = getValueBetweenXmlTags(((strProjectPath + '/') + testSuiteEntity) + '.ts', 'testDataId')
		addNewDataIntoXMLFile(((strProjectPath + '/') + testDataId) + '.dat', '</data>', dataset)
	}

	@Keyword
	public void updateInternalDataFile(String strDataFilePath, String dataset, FailureHandling flowControl) {
		addNewDataIntoXMLFile(((strProjectPath + '/') + strDataFilePath) + '.dat', '</data>', dataset)
	}

	/*
	 * This function is responsible updating Test Case and Data files on the basic of tag Name
	 */
	public void addNewDataIntoXMLFile(String datFilePath, String tagName, String newdata) {
		try {
			addRowBelowDataTag(datFilePath, tagName, newdata);
			logger.logDebug("New row added successfully!")
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addRowBelowDataTag(String filePath, String tagName, String newRow) throws IOException {
		// Read the existing content of the .dat file
		StringBuilder fileContent = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;

		while ((line = reader.readLine()) != null) {
			fileContent.append(line).append("\n");
		}
		reader.close();

		// Find the "<data>" tag and insert the new row below it
		String existingContent = fileContent.toString();
		int dataIndex = existingContent.indexOf(tagName);

		if (dataIndex != -1) {
			// Insert the new row just below the "<data>" tag
			int insertIndex = dataIndex + tagName.length();
			String newContent = existingContent.substring(0, insertIndex) + "\n" + newRow + existingContent.substring(insertIndex);

			// Write the modified content back to the .dat file
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(newContent);
			writer.close();
		} else {
			throw new IOException("'<data>' tag not found in the .dat file.");
		}
	}

	/*
	 * This function is responsible for extracting value from the Test Suite collection, Test Suite and Data files on the basic of tag Name
	 */
	public String getValueBetweenXmlTags(String filePath, String tagName) {
		String value='';
		try {
			File file = new File(filePath); // Replace with the path to your .dat file
			InputStream inputStream = new FileInputStream(file);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			Element rootElement = document.getDocumentElement();
			NodeList nodeList = rootElement.getElementsByTagName(tagName); // Replace with your XML tag

			if (nodeList.getLength() > 0) {
				value = nodeList.item(0).getTextContent();
				logger.logDebug("Value between tags "+tagName+": " + value)
			} else {
				logger.logDebug("Tag not found in the XML.")
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}


	/*
	 * This function is responsible for counting tags from the given file
	 */

	public int countXmlTagsInFile(String filePath, String tagName) throws IOException {
		int count = 0;
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;

		Pattern pattern = Pattern.compile("<" + tagName + ">(.*)</" + tagName + ">");

		while ((line = reader.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			while (matcher.find()) {
				count++;
			}
		}
		logger.logDebug("Total tag count in XML."+ count)
		reader.close();
		return count;
	}

	/*
	 * This function is responsible to create multiple test suite for parallel execution
	 */

	public List<String> createMultipalFiles(String sourceFilePath, int numberOfDuplicates) {
		String testSuiteName='';
		Pattern pattern = Pattern.compile(".*/(.*?)\\.ts");
		Matcher matcher = pattern.matcher(sourceFilePath);
		if (matcher.find()) {
			testSuiteName = matcher.group(1);
		} else {
		}


		List<String> lstDuplicateFiles = new ArrayList<>();
		lstDuplicateFiles.add(sourceFilePath);
		try {
			Path sourcePath = Paths.get(sourceFilePath);

			// Check if the source file exists
			if (!Files.exists(sourcePath)) {
				logger.logDebug("Source file does not exist.")
				return;
			}

			// Get the parent directory of the source file
			Path parentDirectory = sourcePath.getParent();

			// Create duplicate copies of the file
			for (int i = 1; i <= numberOfDuplicates-1; i++) {
				String duplicateFileName = testSuiteName+"_New_" + i + ".ts";
				Path duplicatePath = parentDirectory.resolve(duplicateFileName);

				Files.copy(sourcePath, duplicatePath, StandardCopyOption.REPLACE_EXISTING);
				logger.logDebug("Duplicate copy " + i + " created: " + duplicatePath.toString())
				replaceTextInFile(duplicatePath.toString(), testSuiteName, testSuiteName+"_New_" + i);
				lstDuplicateFiles.add(duplicatePath.toString());
			}

			logger.logDebug("Duplicates created successfully.")
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lstDuplicateFiles;
	}

	public List<String> createMultipalTestCaseFiles(String sourceFilePath, int numberOfDuplicates) {
		String testSuiteName='';
		Pattern pattern = Pattern.compile(".*/(.*?)\\.tc");
		Matcher matcher = pattern.matcher(sourceFilePath);
		if (matcher.find()) {
			testSuiteName = matcher.group(1);
		} else {
		}


		List<String> lstDuplicateFiles = new ArrayList<>();
		lstDuplicateFiles.add(sourceFilePath);
		try {
			Path sourcePath = Paths.get(sourceFilePath);

			// Check if the source file exists
			if (!Files.exists(sourcePath)) {
				logger.logDebug("Source file does not exist.")
				return;
			}

			// Get the parent directory of the source file
			Path parentDirectory = sourcePath.getParent();

			// Create duplicate copies of the file
			for (int i = 1; i <= numberOfDuplicates-1; i++) {
				String duplicateFileName = testSuiteName+"_New_" + i + ".tc";
				Path duplicatePath = parentDirectory.resolve(duplicateFileName);

				Files.copy(sourcePath, duplicatePath, StandardCopyOption.REPLACE_EXISTING);
				logger.logDebug("Duplicate copy " + i + " created: " + duplicatePath.toString())
				replaceTextInFile(duplicatePath.toString(), testSuiteName, testSuiteName+"_New_" + i);
				lstDuplicateFiles.add(duplicatePath.toString());
			}

			logger.logDebug("Duplicates created successfully.")
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lstDuplicateFiles;
	}

	public void replaceTextInFile(String filePath, String searchText, String replacementText) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			StringBuilder content = new StringBuilder();
			String line;

			// Read the file line by line and make replacements
			while ((line = reader.readLine()) != null) {
				line = line.replace(searchText, replacementText);
				content.append(line).append("\n");
			}

			reader.close();

			// Write the updated content back to the file
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(content.toString());
			writer.close();
			logger.logDebug("Text replaced successfully.")
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This function is responsible to the Range and lstIterationTypeValue on the base of Datafile and Parallel run count
	 */
	public List<String> generateIiterationTypeAndValue(List<String> lstRowCount, String maxCount) {
		int maxIteration = Integer.parseInt(maxCount);
		List<List<Integer>> nestedList = new ArrayList<>();
		List<String> lstIterationTypeValue = new ArrayList<>();
		for(int j=0; j<lstRowCount.size(); j++) {

			int dataCount = lstRowCount.get(j);

			int quotient = dataCount / maxIteration;
			int remainder = dataCount % maxIteration;
			int count=0;

			if(dataCount <= maxIteration)
			{
				for(int i=1;i<=dataCount;i++) {
					lstIterationTypeValue.add("SPECIFIC");
					lstIterationTypeValue.add(Integer.toString(i));

				}
			}
			else
			{

				List<Integer> partitions = partitionNumber(dataCount, maxIteration)

				lstIterationTypeValue = partitionList(partitions, maxIteration)
			}
			nestedList.add(lstIterationTypeValue);
		}

		return nestedList;
	}


	/*
	 * This function is responsible to update the value for the created test suite for the parallel execution
	 */
	public void updateValuesBetweenTags(List<String> lstDuplicateFiles, List<String> testCaseEntity, List<List<String>> lstIterationTypeValue, String maxCount) {
		int iterationCount = Integer.parseInt(maxCount);

		String typeValue=null
		for(int r=0;r < lstIterationTypeValue.size(); r++)
		{
			int j=0;
			for(int i=0;i<iterationCount;i++) {
				try {
					if (lstIterationTypeValue.get(r).get(i+j+1) instanceof Integer) {
						typeValue = Integer.toString(lstIterationTypeValue.get(r).get(i+j+1));
					}
					else if (lstIterationTypeValue.get(r).get(i+j+1) instanceof String) {
						typeValue = (String) lstIterationTypeValue.get(r).get(i+j+1);
					} else {
						logger.logDebug("Unsupported data type");
					}
					String newValue = lstIterationTypeValue.get(r).get(i+j)
					updateFirstValueTag(lstDuplicateFiles.get(i),testCaseEntity.get(r), "iterationType", newValue);
					updateFirstValueTag(lstDuplicateFiles.get(i),testCaseEntity.get(r), "value", typeValue);
					logger.logDebug("File updated successfully.")
					j=j+1;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String updateTagValue(String filePath, String tagToModify, String newValue) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;
		boolean insideTag = false;

		while ((line = reader.readLine()) != null) {
			if (line.contains("<" + tagToModify + ">")) {
				insideTag = true;
				line = line.replaceAll("<" + tagToModify + ">.*?</" + tagToModify + ">", "<" + tagToModify + ">" + newValue + "</" + tagToModify + ">");
			} else if (insideTag && line.contains("</" + tagToModify + ">")) {
				insideTag = false;
			}

			content.append(line).append("\n");
		}

		reader.close();
		return content.toString();
	}

	public void updateFirstValueTag(String filePath, String testCaseName, String tagName, String newValue) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		boolean foundLine = false;
		String line;

		boolean firstValueTagFound = false;
		while ((line = reader.readLine()) != null) {
			if (line.trim().equals("<testCaseId>"+testCaseName+"</testCaseId>")) {
				// Found the specified line, set the flag to true
				foundLine = true;
			}
			// Check if the line contains a <value> tag
			if (line.contains("<"+tagName+">")) {
				// If it's the first occurrence of <value> tag, update the content
				if (foundLine && !firstValueTagFound) {
					String pattern = MessageFormat.format("<{0}>(.*?)</{0}>", tagName);
					String replacement = MessageFormat.format("<{0}>{1}</{0}>", tagName, newValue);
					if (Pattern.compile(pattern).matcher(line).find()) {
						line = line.replaceAll(pattern, replacement);
						firstValueTagFound = true;
					}
				}
			}
			content.append(line).append("\n");
		}
		reader.close();
		writeUpdatedContentToFile(filePath, content.toString())
	}

	public void writeUpdatedContentToFile(String filePath, String updatedContent) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		writer.write(updatedContent);
		writer.close();
	}

	/*
	 * This function is responsible to remove unnecessary content for the string like file extension
	 */
	public List<String> removeFileExtension(List<String> fileList, String fileExtension) {
		List<String> resultList = new ArrayList<>();

		for (String item : fileList) {
			// Check if the string contains ".ts" and remove it
			if (item.endsWith(fileExtension)) {
				resultList.add(item.substring(0, item.length() - 3)); // Remove the last 3 characters (".ts")
			} else {
				// If the string doesn't contain ".ts," add it as is to the result list
				resultList.add(item);
			}
		}

		return resultList;
	}

	/*
	 * This function is responsible to remove unnecessary content for the string like system path
	 */
	public List<String> extractTestSuiteFromContent(List<String> fileList) {
		List<String> resultList = new ArrayList<>();
		for (int i=0; i<fileList.size(); i++) {
			Pattern p1 = Pattern.compile('(.*)Test Suites(.*).ts')

			Matcher m1 = p1.matcher(fileList.get(i))

			if (m1.find()) {
				resultList.add("Test Suites"+m1.group(2));
			}

		}
		return resultList;
	}

	/*
	 * This function is responsible to remove existing content from the test suite collect file
	 */
	public void cleanExistingSetup(String strTestSuiteCollectionPath)
	{
		List<String> testSuiteEntity = extractAllValuesBetweenTag(strTestSuiteCollectionPath, 'testSuiteEntity')
		List<String> profileName = extractAllValuesBetweenTag(strTestSuiteCollectionPath, 'profileName')
		List<String> runrunConfigurationId = extractAllValuesBetweenTag(strTestSuiteCollectionPath, 'runConfigurationId')

		for(int index=0; index < testSuiteEntity.size(); index++) {
			if(testSuiteEntity.get(index).contains("_New_"))
			{
				try {
					String xmlContent = readFile(strTestSuiteCollectionPath);
					String modifiedContent = removeContentBetweenTags(xmlContent, testSuiteEntity.get(index), profileName.get(index), runrunConfigurationId.get(index))
					writeToFile(strTestSuiteCollectionPath, modifiedContent);
					logger.logDebug("Content removed from XML file successfully.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else
			{
				logger.logDebug("Not Clean for Parent Suite")
			}
		}
	}


	public String readFile(String filePath) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;
		while ((line = reader.readLine()) != null) {
			content.append(line).append("\n");
		}
		reader.close();
		return content.toString();
	}

	public void writeToFile(String filePath, String content) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		writer.write(content);
		writer.close();
	}

	public String removeContentBetweenTags(String xmlContent, String strTestSuiteName, String strProfileName, String strBrowserName) {
		String pattern = "<TestSuiteRunConfiguration>\\s*<configuration>\\s*<groupName>Web Desktop</groupName>\\s*<profileName>"+strProfileName+"</profileName>\\s*<requireConfigurationData>false</requireConfigurationData>\\s*<runConfigurationId>"+strBrowserName+"</runConfigurationId>\\s*</configuration>\\s*<runEnabled>true</runEnabled>\\s*<testSuiteEntity>"+strTestSuiteName+"</testSuiteEntity>\\s*</TestSuiteRunConfiguration>";
		Pattern regex = Pattern.compile(pattern, Pattern.DOTALL);
		Matcher matcher = regex.matcher(xmlContent);
		return matcher.replaceAll("");
	}

	public void deleteContentbetweenTags(String filePath) {
		try {
			// Read the input file
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			reader.close();

			// Define the regular expression pattern
			String regex = "</TestSuiteRunConfiguration>(.*?)</testSuiteRunConfigurations>";

			// Remove the content between the tags using regex
			Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(content.toString());
			content = new StringBuilder(matcher.replaceAll("</TestSuiteRunConfiguration>\n</testSuiteRunConfigurations>"));

			// Write the modified content back to the output file
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(content.toString());
			writer.close();
			logger.logDebug("Content between tags removed successfully.")
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Integer> partitionNumber(int number, int partitionCount) {
		List<Integer> partitions = new ArrayList()

		int remainder = number % partitionCount

		int quotient = number / partitionCount

		for (int i = 0; i < partitionCount; i++) {
			partitions.add(quotient)
		}

		for (int i = 0; i < remainder; i++) {
			partitions.set(i, partitions.get(i) + 1)
		}

		return partitions
	}

	public List<String> partitionList(List<Integer> myList, int partitionSize) {
		List<String> lstIterationTypeValue = new ArrayList()

		int listSize = myList.size()

		int count = 0

		int int1

		int int2

		for (int i = 0; i < listSize; i++) {
			if (i == 0) {
				lstIterationTypeValue.add("RANGE");
				int1 = 1
				int2 = (myList.get(i) + count)
				lstIterationTypeValue.add((int1 + '-') + int2)

				count = int2
			} else if (myList.get(i) == 1) {
				lstIterationTypeValue.add("SPECIFIC");
				lstIterationTypeValue.add(count + 1)
				count = count+1
			} else {
				lstIterationTypeValue.add("RANGE");
				int1 = (1 + int2)
				int2 = (myList.get(i) + count)
				lstIterationTypeValue.add((int1 + '-') + int2)

				count = int2
			}
		}

		return lstIterationTypeValue
	}

	public int excelRowCount(String filePath, String sheetName)
	{
		try {
			FileInputStream fis = new FileInputStream(filePath);
			Workbook workbook = WorkbookFactory.create(fis);

			// Get the sheet by name
			Sheet sheet = workbook.getSheet(sheetName);

			if (sheet != null) {
				int  rowCount = sheet.getLastRowNum(); // Plus 1 to account for 0-based indexing
				return rowCount;

			} else {
				System.err.println("Sheet '" + sheetName + "' not found.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int csvRowCount(String filePath)
	{
		int rowCount = 0;

		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath))
			String line;
			while (br.readLine() != null) {
				rowCount++;
			}
			return rowCount-1;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> extractAllValuesBetweenTag(String filePath, String tagName) {
		List<String> values = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath))
			String line;
			StringBuilder fileContent = new StringBuilder();
			while ((line = br.readLine()) != null) {
				fileContent.append(line);
			}

			// Construct the regex pattern to match the content between the tags
			String regex = "<" + tagName + ">(.*?)</" + tagName + ">";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(fileContent.toString());

			// Find all matches and add the content between tags to the list
			while (matcher.find()) {
				values.add(matcher.group(1));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return values;
	}

	public void deleteFilesWithSubstring(String folderPath, String substringToDelete) {
		File folder = new File(folderPath);

		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.isFile() && file.getName().contains(substringToDelete)) {
						if (file.delete()) {
							logger.logDebug("Deleted file: " + file.getAbsolutePath());
						} else {
							logger.logDebug("Failed to delete file: " + file.getAbsolutePath());
						}
					} else if (file.isDirectory()) {
						// Recursive call to handle subdirectories
						deleteFilesWithSubstring(file.getAbsolutePath(), substringToDelete);
					}
				}
			}
		}
	}

}