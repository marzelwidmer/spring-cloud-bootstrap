package ch.keepcalm.cloud.service.nutrition.spring.cloud.springcloudcontractproducer;

import ch.keepcalm.cloud.service.nutrition.food.controller.FoodController;
import ch.keepcalm.cloud.service.nutrition.food.service.FoodService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMessageVerifier
@Ignore
public class BaseTestClass {

//    @Autowired
//    private EvenOddController evenOddController;
//
//    @Before
//    public void setup() {
//        StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(evenOddController);
//        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);
//    }

    @Autowired
    private FoodController foodController;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private FoodService foodService;

    @Before
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);
        given(foodService.findAllBy("")).willReturn(null);
    }
}
