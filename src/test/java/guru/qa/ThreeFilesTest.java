package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


import static com.codeborne.pdftest.assertj.Assertions.assertThat;

public class ThreeFilesTest {
    ClassLoader classLoader = getClass().getClassLoader();

    @Test
    @DisplayName("Обработка ZIP архива c вложенными pdf,xls,csv")
    void parseZipFile() throws Exception { // подключаем исключения для поимки exception ( фикс утечки памяти)
        ZipFile zipFile = new ZipFile( // создаем экземпляр zip файла для работы в рамках метода
                new File(classLoader.getResource("Files/ThreeFiles.zip").toURI()));

        Enumeration<? extends ZipEntry> entries = zipFile.entries();//Метод перечисляет все файлы в архиве.

        while (entries.hasMoreElements()) { // пока есть элементы - передавай
            ZipEntry entry = entries.nextElement();

            //оператор обьявляющий один или несколько ресурсов и позволяющий избежать учтеки ресурсов - try
            try (InputStream stream = zipFile.getInputStream(entry)) {// try with resources
                switch (entry.getName()) { // 28 - 34 получаем список файлов и работать с ними.Устоявшая конструкция.

                    case "FilePDF.pdf": { //case тут вместо if
                        PDF pdf = new PDF(stream); // Дефолтная строка из библиотеки для работыс пдф.
                        assertThat(pdf) // Ассерт J
                                .containsExactText("The free Acrobat Reader is easy to download"); //Pdf файл содержит текст
                        System.out.println("В файле " + entry.getName() + " ошибок не найдено");
                        break;
                    }

                    case "FileXLS.xls": { // если название файла FileXLS.xls то сделай шаги ниже.
                        XLS xls = new XLS(stream); // дефолтная строка из библиотеки
                        assertThat(xls.excel
                                .getSheetAt(0)
                                .getRow(11)
                                .getCell(1)
                                .getStringCellValue()).contains("Сахалинская обл, Южно-Сахалинск");
                        System.out.println("В файле " + entry.getName() + " ошибок не найдено");

                        break;

                    }
                    case "FileCSV.csv": {
                        try (CSVReader csv = new CSVReader( //
                                new InputStreamReader(stream, StandardCharsets.UTF_8))) { //указываем кодировку
                            List<String[]> content = csv.readAll();
                            assertThat(content.get(0))
                                    .contains(
                                            "Series_reference", "Period", "Data_value",
                                            "Suppressed", "STATUS", "UNITS", "Magnitude",
                                            "Subject", "Group", "Series_title_1", "Series_title_2",
                                            "Series_title_3", "Series_title_4", "Series_title_5");
                            System.out.println("В файле " + entry.getName() + " ошибок не найдено");
                            break;
                        }
                    }
                }
            }
        }
    }
}
