package stubmanagement;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.jayway.restassured.RestAssured.given;

/**
 * Created by dvt on 27-02-17.
 */
public class VideosHomeStub {


    public void setupStub() {

        stubFor(post(urlEqualTo("/pingpong"))
                .withRequestBody(matching("<input>PING</input>"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<output>PONG</output>")));
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8090)
            .httpsPort(8443).notifier(new Slf4jNotifier(true)));

    @Test
    public void testPingPongPositive() {

        setupStub();

        given().
                body("<input>PING</input>").
                when().
                post("http://localhost:8090/pingpong").
                then().
                assertThat().
                statusCode(200).
                and().
                assertThat().body("output", org.hamcrest.Matchers.equalTo("PONG"));
    }
}
