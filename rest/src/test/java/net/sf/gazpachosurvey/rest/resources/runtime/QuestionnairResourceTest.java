package net.sf.gazpachosurvey.rest.resources.runtime;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.gazpachosurvey.domain.core.Questionnair;
import net.sf.gazpachosurvey.dto.PageDTO;
import net.sf.gazpachosurvey.dto.QuestionDTO;
import net.sf.gazpachosurvey.dto.QuestionnairDTO;
import net.sf.gazpachosurvey.dto.answers.TextAnswer;
import net.sf.gazpachosurvey.repository.dynamic.QuestionnairAnswersRepository;
import net.sf.gazpachosurvey.rest.ApplicationConfig;
import net.sf.gazpachosurvey.security.LoginService;
import net.sf.gazpachosurvey.services.QuestionnairAnswersService;
import net.sf.gazpachosurvey.test.dbunit.support.ColumnDetectorXmlDataSetLoader;
import net.sf.gazpachosurvey.types.BrowsingAction;
import net.sf.gazpachosurvey.types.RenderingMode;

import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/jpa-test-context.xml", "classpath:/datasource-test-context.xml",
        "classpath:/services-context.xml", "classpath:/components-context.xml", "classpath:/questionnair-context.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("QuestionnairResource-dataset.xml")
@DbUnitConfiguration(dataSetLoader = ColumnDetectorXmlDataSetLoader.class)
public class QuestionnairResourceTest {

    @Autowired
    private QuestionnairAnswersRepository repository;

    @Autowired
    private QuestionnairAnswersService questionnairAnswersService;

    private JerseyTestImpl jerseyTest;

    class JerseyTestImpl extends JerseyTest {
        @Override
        public URI getBaseUri() {
            return super.getBaseUri();
        }

        @Override
        protected ResourceConfig configure() {
            // enable(TestProperties.LOG_TRAFFIC);
            // enable(TestProperties.DUMP_ENTITY);
            ResourceConfig config = new ApplicationConfig();
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("contextConfigLocation", "root-test-context.xml");
            config.setProperties(properties);
            return config;
        }

        @Override
        protected void configureClient(final ClientConfig config) {
            config.register(new JacksonFeature());
        }
    }

    @Before
    public void beforeMethod() throws Exception {
        jerseyTest = new JerseyTestImpl();
        jerseyTest.setUp();
        repository.activeAllAnswers();
    }

    public void getQuestionnairsTestOldWay() throws JsonParseException, JsonMappingException, IOException {
        String invitationToken = "55GAW02QH2";

        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, QuestionnairDTO.class);

        client().register(new HttpBasicAuthFilter(LoginService.RESPONDENT_USER_NAME, invitationToken));
        String response = client().target(getBaseUri() + "runtime/questionnairs").request()
                .accept(MediaType.APPLICATION_JSON).get(String.class);

        List<QuestionnairDTO> questionnairDTOs = mapper.readValue(response, type);
        assertThat(questionnairDTOs).hasSize(1);
    }

    @Test
    public void questionnairsListTest() {
        String invitationToken = "255FXLSESX";
        client().register(new HttpBasicAuthFilter(LoginService.RESPONDENT_USER_NAME, invitationToken));
        Response response = client().target(getBaseUri() + "runtime/questionnairs").request()
                .accept(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200.getStatusCode());
        List<QuestionnairDTO> questionnairDTOs = response.readEntity(new GenericType<List<QuestionnairDTO>>() {
        });
        assertThat(questionnairDTOs).contains(QuestionnairDTO.with().id(63).build());
    }

    @Test
    public void navigateQuestionByQuestionTest() {
        String invitationToken = "255FXLSESX";
        Integer questionnairId = 63;
        RenderingMode mode = RenderingMode.QUESTION_BY_QUESTION;
        BrowsingAction action = BrowsingAction.ENTERING;

        client().register(new HttpBasicAuthFilter(LoginService.RESPONDENT_USER_NAME, invitationToken));
        PageDTO page = null;
        Response response = client()
                .target(String.format("%sruntime/questionnairs/%d/page?mode=%s&action=%s", getBaseUri(),
                        questionnairId, mode, action)).request().accept(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200.getStatusCode());
        page = response.readEntity(PageDTO.class);

        assertThat(page.getQuestions()).containsExactly(QuestionDTO.with().id(17).build());

        action = BrowsingAction.FORWARD;

        client().register(new HttpBasicAuthFilter(LoginService.RESPONDENT_USER_NAME, invitationToken));
        response = client()
                .target(String.format("%sruntime/questionnairs/%d/page?mode=%s&action=%s", getBaseUri(),
                        questionnairId, mode, action)).request().accept(MediaType.APPLICATION_JSON).get();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200.getStatusCode());
        page = response.readEntity(PageDTO.class);

        assertThat(page.getQuestions()).containsExactly(QuestionDTO.with().id(18).build());

    }

    @Test
    public void navigateGroupByGroupTest() {
        Questionnair questionnair = Questionnair.with().id(63).build();

        questionnairAnswersService.save(questionnair, "Q1", "Antonio Maria");
        questionnairAnswersService.save(questionnair, "Q2", "05");
        questionnairAnswersService.save(questionnair, "Q3", 33);
        String longAnswer = "I started to work in IECISA, 10 years ago";
        questionnairAnswersService.save(questionnair, "Q4", ArrayUtils.toObject(longAnswer.toCharArray()));
        questionnairAnswersService.save(questionnair, "Q5", "O2");
        questionnairAnswersService.save(questionnair, "Q6", "O1");

        // Radio options
        questionnairAnswersService.save(questionnair, "q7_1", "O1");
        questionnairAnswersService.save(questionnair, "q7_2", "O2");
        questionnairAnswersService.save(questionnair, "q7_3", "O3");
        questionnairAnswersService.save(questionnair, "q7_4", "O1");

        // Checkbox list
        questionnairAnswersService.save(questionnair, "q8_o1", Boolean.TRUE);
        questionnairAnswersService.save(questionnair, "q8_o2", Boolean.TRUE);
        questionnairAnswersService.save(questionnair, "q8_o3", Boolean.TRUE);
        questionnairAnswersService.save(questionnair, "q8_o4", Boolean.FALSE);

        String invitationToken = "255FXLSESX";
        Integer questionnairId = 63;
        RenderingMode mode = RenderingMode.GROUP_BY_GROUP;
        BrowsingAction action = BrowsingAction.ENTERING;

        client().register(new HttpBasicAuthFilter(LoginService.RESPONDENT_USER_NAME, invitationToken));
        PageDTO page = null;
        Response response = client()
                .target(String.format("%sruntime/questionnairs/%d/page?mode=%s&action=%s", getBaseUri(),
                        questionnairId, mode, action)).request().accept(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200.getStatusCode());
        page = response.readEntity(PageDTO.class);

        assertThat(page.getQuestions()).containsExactly(QuestionDTO.with().id(17).build(),
                QuestionDTO.with().id(18).build(), QuestionDTO.with().id(34).build());

        action = BrowsingAction.FORWARD;

        client().register(new HttpBasicAuthFilter(LoginService.RESPONDENT_USER_NAME, invitationToken));
        response = client()
                .target(String.format("%sruntime/questionnairs/%d/page?mode=%s&action=%s", getBaseUri(),
                        questionnairId, mode, action)).request().accept(MediaType.APPLICATION_JSON).get();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200.getStatusCode());
        page = response.readEntity(PageDTO.class);

        assertThat(page.getQuestions()).containsExactly(QuestionDTO.with().id(35).build(),
                QuestionDTO.with().id(36).build(), QuestionDTO.with().id(40).build());

        client().register(new HttpBasicAuthFilter(LoginService.RESPONDENT_USER_NAME, invitationToken));
        response = client()
                .target(String.format("%sruntime/questionnairs/%d/page?mode=%s&action=%s", getBaseUri(),
                        questionnairId, mode, action)).request().accept(MediaType.APPLICATION_JSON).get();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200.getStatusCode());
        page = response.readEntity(PageDTO.class);

        assertThat(page.getQuestions()).containsExactly(QuestionDTO.with().id(44).build(),
                QuestionDTO.with().id(55).build());
    }

    @Test
    public void saveAnswerTest() {
        String invitationToken = "255FXLSESX";
        Integer questionnairId = 63;
        client().register(new HttpBasicAuthFilter(LoginService.RESPONDENT_USER_NAME, invitationToken));
        String questionCode = "Q1";
        TextAnswer answer = TextAnswer.fromValue("Antonio Maria");
        Entity<TextAnswer> wrapper = Entity.json(answer);
        Response response = client()
                .target(String.format("%sruntime/questionnairs/%d/answer?questionCode=%s", getBaseUri(),
                        questionnairId, questionCode)).request().accept(MediaType.APPLICATION_JSON).post(wrapper);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200.getStatusCode());
    }

    @After
    public void tearDown() throws Exception {
        jerseyTest.tearDown();
    }

    protected Client client() {
        return jerseyTest.client();
    }

    protected URI getBaseUri() {
        return jerseyTest.getBaseUri();
    }

}
