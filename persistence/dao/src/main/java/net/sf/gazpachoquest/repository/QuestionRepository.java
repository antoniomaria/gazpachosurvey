/*******************************************************************************
 * Copyright (c) 2014 antoniomariasanchez at gmail.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     antoniomaria - initial API and implementation
 ******************************************************************************/
package net.sf.gazpachoquest.repository;

import java.util.List;

import javax.persistence.QueryHint;

import net.sf.gazpachoquest.domain.core.Question;
import net.sf.gazpachoquest.repository.support.GenericRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends GenericRepository<Question> {
    @Query("select q from Section qg join qg.questions q where qg.id = :sectionId order by index(q)")
    List<Question> findBySectionId(@Param("sectionId")
    Integer sectionId);

    @Query("select q from QuestionnaireDefinition s join s.sections qg join fetch qg.questions q where s.id = :questionnaireDefinition order by index(qg),index(q)")
    List<Question> findByQuestionnaireId(@Param("questionnaireDefinition")
    Integer questionnairDefinitionId);

    @Query("select q from Question q where q.id in :questionIds")
    @QueryHints(value = { @QueryHint(name = org.eclipse.persistence.config.QueryHints.BATCH_TYPE, value = "IN"),
            @QueryHint(name = org.eclipse.persistence.config.QueryHints.BATCH, value = "q.questionOptions"),
            @QueryHint(name = org.eclipse.persistence.config.QueryHints.BATCH, value = "q.subquestions"),
            @QueryHint(name = org.eclipse.persistence.config.QueryHints.BATCH, value = "q.translations"),
            @QueryHint(name = org.eclipse.persistence.config.QueryHints.BATCH, value = "questionOptions.translations"),
            @QueryHint(name = org.eclipse.persistence.config.QueryHints.BATCH, value = "subquestions.translations"),
            @QueryHint(name = org.eclipse.persistence.config.QueryHints.BATCH, value = "subquestions.questionOptions"), }, forCounting = false)
    List<Question> findInList(@Param("questionIds")
    List<Integer> questionIds);

    @Query("select q from Section qg join qg.questions q where qg.id = :sectionId and index(q) = :position")
    Question findOneByPositionInSection(@Param("sectionId")
    Integer sectionId, @Param("position")
    Integer position);

    @Query("select index(q) from Section qg join qg.questions q where q.id = :questionId")
    Integer findPositionInSection(@Param("questionId")
    Integer questionId);

}
