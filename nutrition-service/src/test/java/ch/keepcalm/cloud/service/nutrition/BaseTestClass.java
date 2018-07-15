package ch.keepcalm.cloud.service.nutrition;

import ch.keepcalm.cloud.service.nutrition.food.domain.Food;
import ch.keepcalm.cloud.service.nutrition.food.service.FoodService;
import com.github.tomakehurst.wiremock.admin.NotFoundException;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;





@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMessageVerifier
public class BaseTestClass {

    private static final String OUTPUT = "target/generated-snippets";

    @Autowired
    private WebApplicationContext context;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(OUTPUT);

    @Rule
    public TestName testName = new TestName();

    @MockBean
    private FoodService service;


    @Before
    public void setup() {

        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation).operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .alwaysDo(document(getClass().getSimpleName() + "_" + testName.getMethodName()))
                .build());
        given(service.findOne(anyString())).willThrow(new NotFoundException("Invalid parameter"));
        given(service.findAllBy("Agavensirup")).willReturn(createAgavensirup());

//
//        willReturn(createPartnerListWithFavor())
//                .given(service).findPartners();
//
//        willReturn(createFavorPartner())
//                .given(service).findOne("M4856252");


    }


    @NotNull
    private List<Food> createAgavensirup() {
        Food agavensirup = new Food(new ObjectId("5b3877a712f6466db803da0e"), "Agavensirup", "Agavendicksaft", "Süssigkeiten/Zucker und Süssstoffe", 293, 0.00, 0.2);
        return Collections.singletonList(agavensirup);
    }
}