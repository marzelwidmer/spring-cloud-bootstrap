package ch.keepcalm.cloud.service.nutrition.spring.cloud.springcloudcontractproducer;

import ch.keepcalm.cloud.service.nutrition.food.controller.FoodController;
import ch.keepcalm.cloud.service.nutrition.food.domain.Food;
import ch.keepcalm.cloud.service.nutrition.food.service.FoodService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMessageVerifier
public class BaseTestClass {

    @Autowired
    private FoodController foodController;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private FoodService foodService;

    @Before
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);
        given(foodService.findAllBy("Agavensirup")).willReturn(createAgavensirup());
    }
    @NotNull
    private List<Food> createAgavensirup() {
        Food agavensirup = new Food(new ObjectId("5b3877a712f6466db803da0e"), "Agavensirup", "Agavendicksaft", "Süssigkeiten/Zucker und Süssstoffe", 293, 0.00, 0.2);
        return Collections.singletonList(agavensirup);
    }
}