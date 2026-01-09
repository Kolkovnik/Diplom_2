import io.restassured.RestAssured;
import org.junit.Before;

public class BaseTest {
    @Before
    public void setUpUrl() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/";
    }
}