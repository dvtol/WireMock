package stubmanagement;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.jayway.restassured.RestAssured.given;

/**
 * Created by dvt on 27-02-17.
 */
public class StatefullStub {


    public void setupStub() {

        stubFor(get(urlEqualTo("/todolist"))
                .inScenario("addItem")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<list>Empty</list>")));

        stubFor(post(urlEqualTo("/todolist"))
                .inScenario("addItem")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo("itemAdded")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/xml")
                        .withStatus(201)));

        stubFor(get(urlEqualTo("/todolist"))
                .inScenario("addItem")
                .whenScenarioStateIs("itemAdded")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<list><item>Item added to list</item></list>")));
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8090)
            .httpsPort(8443).notifier(new Slf4jNotifier(true)));

    @Test
    public void testStatefulMock() throws InterruptedException {

        setupStub();

        Thread.sleep(10000);

        given().
                when().
                get("http://localhost:8090/todolist").
                then().
                assertThat().
                statusCode(200).
                and().
                assertThat().body("list", org.hamcrest.Matchers.equalTo("Empty"));

        given().
                when().
                post("http://localhost:8090/todolist").
                then().
                assertThat().
                statusCode(203);

        given().
                when().
                get("http://localhost:8090/todolist").
                then().
                assertThat().
                statusCode(200).
                and().
                assertThat().body("list", org.hamcrest.Matchers.not("Empty")).
                and().
                assertThat().body("list.item", org.hamcrest.Matchers.equalTo("Item added to list"));
    }
}
