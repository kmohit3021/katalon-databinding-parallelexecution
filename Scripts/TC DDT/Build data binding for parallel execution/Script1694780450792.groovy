import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

String strProjectPath = RunConfiguration.getProjectDir()

String strTestSuiteCollectionPath = ('/' + GlobalVariable.TEST_SUITE_COLLECTION_NAME) + '.ts'

CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.cleanExistingSetup'(strProjectPath + strTestSuiteCollectionPath)

String executionMode = CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.getValueBetweenXmlTags'(strProjectPath + 
    strTestSuiteCollectionPath, 'executionMode')

println('executionMode-----\n' + executionMode)

String maxConcurrentInstances = CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.getValueBetweenXmlTags'(strProjectPath + 
    strTestSuiteCollectionPath, 'maxConcurrentInstances')

println('maxConcurrentInstances-----\n' + maxConcurrentInstances)

String testSuiteEntity = CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.getValueBetweenXmlTags'(strProjectPath + 
    strTestSuiteCollectionPath, 'testSuiteEntity')

println('testSuiteEntity-----\n' + testSuiteEntity)

String testDataId = CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.getValueBetweenXmlTags'(((strProjectPath + 
    '/') + testSuiteEntity) + '.ts', 'testDataId')

println('testDataId-----\n' + testDataId)

CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.addNewDataIntoXMLFile'(((strProjectPath + '/') + testDataId) + 
    '.dat', '</data>', '<data>HelloMr MohitSharma</data>')

String rowcount = CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.countXmlTagsInFile'(((strProjectPath + '/') + 
    testDataId) + '.dat', 'data')

println('rowcount-----\n' + rowcount)

List<String> lstDuplicateFiles

if (Integer.parseInt(rowcount) < Integer.parseInt(maxConcurrentInstances)) {
    lstDuplicateFiles = CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.createMultipalFiles'(((strProjectPath + 
        '/') + testSuiteEntity) + '.ts', Integer.parseInt(rowcount))
} else {
    lstDuplicateFiles = CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.createMultipalFiles'(((strProjectPath + 
        '/') + testSuiteEntity) + '.ts', Integer.parseInt(maxConcurrentInstances))
}

println('Test Suite for Parallel Exection----- \n' + lstDuplicateFiles)

List<String> lstIterationTypeValue = CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.generateIiterationTypeAndValue'(
    rowcount, maxConcurrentInstances)

println('lstIterationTypeValue---- \n' + lstIterationTypeValue)

CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.updateValuesBetweenTags'(lstDuplicateFiles, lstIterationTypeValue, 
    Integer.toString(lstDuplicateFiles.size()))

List<String> resultList = CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.extractTestSuiteFromContent'(
    lstDuplicateFiles)

println('resultList---- \n' + resultList)

println(resultList.size())

//if (rowcount < maxConcurrentInstances) {
String content = ''

String baseContent = ''

for (int i = 1; i < resultList.size(); i++) {
    if ((i + 1) == resultList.size()) {
        baseContent = (('<TestSuiteRunConfiguration>\n<configuration>\n<groupName>Web Desktop</groupName>\n<profileName>default</profileName>\n<requireConfigurationData>false</requireConfigurationData>\n<runConfigurationId>Chrome</runConfigurationId>\n</configuration>\n<runEnabled>true</runEnabled>\n<testSuiteEntity>' + 
        resultList.get(i)) + '</testSuiteEntity>\n</TestSuiteRunConfiguration>')
    } else {
        baseContent = (('<TestSuiteRunConfiguration>\n<configuration>\n<groupName>Web Desktop</groupName>\n<profileName>default</profileName>\n<requireConfigurationData>false</requireConfigurationData>\n<runConfigurationId>Chrome</runConfigurationId>\n</configuration>\n<runEnabled>true</runEnabled>\n<testSuiteEntity>' + 
        resultList.get(i)) + '</testSuiteEntity>\n</TestSuiteRunConfiguration>\n')
    }
    
    content == baseContent

    content = (content + baseContent)
}

println('After content----' + content)

CustomKeywords.'com.katalon.plugin.keyword.databinding.ParallelExecution.addNewDataIntoXMLFile'(strProjectPath + strTestSuiteCollectionPath, 
    '</TestSuiteRunConfiguration>', content)

