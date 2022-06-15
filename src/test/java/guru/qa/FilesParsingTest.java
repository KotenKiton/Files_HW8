package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;

import java.io.File;

import static com.codeborne.selenide.Selectors.byText;
import static org.assertj.core.api.Assertions.assertThat;

public class FilesParsingTest {

    @Test
    void parserPdfTest() throws Exception {
        Selenide.open("https://junit.org/junit5/docs/current/user-guide/");
        File pdfDownload = Selenide.$(byText("PDF download")).download();
        PDF pdf = new PDF(pdfDownload);
        assertThat(pdf.author).contains("Marc Philipp");

    }

}
