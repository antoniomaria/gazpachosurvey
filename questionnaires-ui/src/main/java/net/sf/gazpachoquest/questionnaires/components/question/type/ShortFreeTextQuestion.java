package net.sf.gazpachoquest.questionnaires.components.question.type;

import javax.inject.Inject;

import net.sf.gazpachoquest.dto.answers.Answer;
import net.sf.gazpachoquest.dto.answers.NoAnswer;
import net.sf.gazpachoquest.dto.answers.TextAnswer;
import net.sf.gazpachoquest.questionnaires.components.question.AbstractQuestionComponent;
import net.sf.gazpachoquest.questionnaires.events.AnswerSavedEvent;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.cdiproperties.annotation.LabelProperties;
import org.vaadin.addon.cdiproperties.annotation.TextFieldProperties;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class ShortFreeTextQuestion extends AbstractQuestionComponent implements TextChangeListener {

    private static final long serialVersionUID = 4485291720274483361L;

    private static Logger logger = LoggerFactory.getLogger(ShortFreeTextQuestion.class);

    @Inject
    @TextFieldProperties(maxLength = 50, immediate = true)
    private TextField answerField;

    @Inject
    @LabelProperties(contentMode = ContentMode.HTML)
    private Label questionTitle;

    public ShortFreeTextQuestion() {
        super();
    }

    @Override
    public void init() {
        questionTitle.setValue(String.format("<b>%d</b>. %s", questionDTO.getNumber(), questionDTO
                .getLanguageSettings().getTitle()));
        content.addComponent(questionTitle);

        answerField.addTextChangeListener(this);
        if (questionDTO.getAnswer() instanceof TextAnswer) {
            answerField.setValue(((TextAnswer) questionDTO.getAnswer()).getValue());
        }
        content.addComponent(answerField);
    }

    @Override
    public void textChange(TextChangeEvent event) {
        String text = event.getText();
        logger.debug("Text Change Event fired in ShortFreeTextQuestion with text = {}", text);
        String questionCode = questionDTO.getCode();
        Answer answer = null;
        if (StringUtils.isBlank(text)) {
            answer = NoAnswer.create();
        } else {
            answer = TextAnswer.fromValue(text);
        }
        answerSavedEvent.fire(AnswerSavedEvent.with().questionCode(questionCode).answer(answer).build());
    }
}
