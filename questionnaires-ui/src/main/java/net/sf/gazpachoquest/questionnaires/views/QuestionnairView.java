package net.sf.gazpachoquest.questionnaires.views;

import java.util.List;

import javax.inject.Inject;

import net.sf.gazpachoquest.api.QuestionnairResource;
import net.sf.gazpachoquest.dto.QuestionnairDTO;
import net.sf.gazpachoquest.questionnaires.resource.GazpachoResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@CDIView(QuestionnairView.NAME)
public class QuestionnairView extends CustomComponent implements View {

    private static final long serialVersionUID = -4474306191162456568L;

    public static final String NAME = "questionnair";

    private static Logger logger = LoggerFactory.getLogger(LoginView.class);

    @Inject
    @GazpachoResource
    private QuestionnairResource questionnairResource;

    @Override
    public void enter(ViewChangeEvent event) {
        logger.debug("Entering {} view ", QuestionnairView.NAME);
        // explicitly log the view change - this could also be done with an
        // interceptor or a decorator
        // loggableEvent.fire(new LoggableEvent("Enter view [" + event.getViewName() + "]"));
        setSizeFull();

        List<QuestionnairDTO> questionnairs = questionnairResource.list();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        String username = (String) VaadinService.getCurrentRequest().getWrappedSession().getAttribute("username");

        Label label = new Label("Welcome " + username + " to take the questionnair: "
                + questionnairs.get(0).getLanguageSettings().getTitle());
        mainLayout.addComponent(label);
        // mainLayout.setExpandRatio(content, 1);

        setCompositionRoot(mainLayout);
    }

}