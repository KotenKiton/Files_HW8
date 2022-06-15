package guru.qa;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class SelenideFilesTest {

    @Test
    void SelenideDownloadTest() throws Exception {
        // чтобы скачать файл должно быть написать href + ссылка в девтулсе.
        Selenide.open("https://github.com/junit-team/junit5/blob/main/README.md");
        File downloadedFile = Selenide.$("#raw-url").download(); //download + alt + ent = add signature это ок
        try (InputStream is = new FileInputStream(downloadedFile)) {
            assertThat(new String(is.readAllBytes(), StandardCharsets.UTF_8))
                    .contains("This repository is the home of the next generation of JUnit");
        }
    }

    // Корень для поиска файлов у класс лоадера - папка resources
    @Test
    void uploadSelenideTest() {
        Selenide.open("https://the-internet.herokuapp.com/upload");
        Selenide.$("input[type='file']") // SUPER BAD PRACTICE
                // SUPER BAD PRACTICE .uploadFile(new File("C:\\Users\\koten\\IdeaProjects\\Files_HW8\\src\\test\\resources\\Files\\1.txt"));
                .uploadFromClasspath("Files\\1.txt");
        Selenide.$("#file-submit").click();
        Selenide.$("div.example").shouldHave(Condition.text("File Uploaded!"));
        Selenide.$("#uploaded-files").shouldHave(Condition.text("1.txt"));

    }
}
