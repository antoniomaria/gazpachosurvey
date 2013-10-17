package net.sf.gazpachosurvey.questionnaires;

import java.util.List;

import net.sf.gazpachosurvey.dto.SurveyDTO;
import net.sf.gazpachosurvey.services.SurveyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@Component
@Scope("prototype")
public class QuestionnairesUI extends UI {

    private static final long serialVersionUID = 1265851857862002747L;

    @Autowired
    private transient SurveyService surveyService;
    
    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);
        
        Button button = new Button("Click Me Again!");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                layout.addComponent(new Label("Thank you for clicking"));
            }
        });
        layout.addComponent(button);
        
        List<SurveyDTO> surveys = surveyService.findAll();
        
        for (SurveyDTO surveyDTO : surveys) {
            Label label = new Label(surveyDTO.getLanguageSettings().getTitle());
            layout.addComponent(label);
        }
    }

}
