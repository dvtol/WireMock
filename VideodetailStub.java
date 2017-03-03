package stubmanagement;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.jayway.restassured.RestAssured;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.jayway.restassured.RestAssured.given;

/**
 * Created by dvt on 27-02-17.
 */
public class VideodetailStub {

    public void setupStub() {

        stubFor(get(urlEqualTo("/an/endpoint"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withStatus(200)
                        .withBody("You've reached a valid WireMock endpoint")));
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8090)
            .httpsPort(8443).notifier(new Slf4jNotifier(true)));

    @Test
    public void testStatusCodePositive() {

        this.setupStub();

        given().
                when().
                get("http://localhost:8090/an/endpoint").
                then().
                assertThat().statusCode(200);
    }

    @Test
    public void testResponseContents() {

        setupStub();

        String response = RestAssured.get("http://localhost:8090/an/endpoint").asString();
        Assert.assertEquals("You've reached a valid WireMock endpoint", response);
    }
}


