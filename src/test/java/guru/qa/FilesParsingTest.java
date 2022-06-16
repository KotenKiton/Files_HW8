package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Selenide;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selectors.byText;
import static org.assertj.core.api.Assertions.assertThat;

public class FilesParsingTest {

    ClassLoader classLoader = getClass().getClassLoader();

    @Test
    void parserPdfTest() throws Exception {
        Selenide.open("https://junit.org/junit5/docs/current/user-guide/");
        File pdfDownload = Selenide.$(byText("PDF download")).download();
        PDF pdf = new PDF(pdfDownload);
        assertThat(pdf.author).contains("Marc Philipp");

    }

    @Test
    @DisplayName("Parser XLS")
    void parserXlsTest() throws Exception {
        Selenide.open("http://romashka2008.ru/price");
        // *=  href*='prajs_ot - href содержит в себе prajs_ot
        File xlsDownload = Selenide.$(".site-main__inner a[href*='prajs_ot']").download();
        XLS xls = new XLS(xlsDownload);
        assertThat(xls.excel
                .getSheetAt(0)
                .getRow(11)
                .getCell(1)
                /*.getSheetAt(0) - дай таблицу первую ( 0 т.к нулевой индекс)
                .getRow(11) // дай 12 строчку( т.к считаем с нуля)
                .getCell(1)*/ // дай 2 столбец ( по тойже причине)
                .getStringCellValue()).contains("Сахалинская обл, Южно-Сахалинск"); // ассерт что есть текст.
    }
    /* Добавил файл CSV в ресурсы. Его нужно читать с помощью class-loader.
     Есть 2 способа получения класслодера  (1)ClassLoader classLoader = getClass().getClassLoader();
                                            или (2) через getClass.getClassLoader();(2)
     */


    @Test
    @DisplayName("Parser CSV")
    void parserCsvTest() throws Exception {
        try (InputStream is = ClassLoader
                .getSystemResourceAsStream("files/business-financial-data-mar-2022-quarter-csv.csv");
             CSVReader reader = new CSVReader(new InputStreamReader(is))) {
            List<String[]> content = reader.readAll();
            assertThat(content.get(0)).contains(
                    "Series_reference", "Period", "Data_value",
                    "Suppressed", "STATUS", "UNITS", "Magnitude",
                    "Subject", "Group", "Series_title_1", "Series_title_2",
                    "Series_title_3", "Series_title_4", "Series_title_5");

        }

    }

    @Test
    void parseZipTest() throws Exception {
        try (InputStream is = classLoader.getResourceAsStream("files/ThreeFiles.zip");
        ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry =zis.getNextEntry()) != null){
                assertThat(entry.getName()).isEqualTo("12");
            }

        }
    }


}
