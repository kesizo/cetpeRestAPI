package com.kesizo.cetpe.backend.restapi.service;

import com.kesizo.cetpe.backend.restapi.model.AssessmentRubric;
import com.kesizo.cetpe.backend.restapi.model.ItemRubric;
import com.kesizo.cetpe.backend.restapi.repository.AssessmentRubricRepository;
import com.kesizo.cetpe.backend.restapi.repository.ItemRubricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("ItemRubricService")
public class ItemRubricServiceImpl implements ItemRubricService
{

    Logger logger = LoggerFactory.getLogger(ItemRubricServiceImpl.class);

    @Autowired
    private AssessmentRubricRepository _assessmentRubricRepository;

    @Autowired
    private ItemRubricRepository _itemRubricRepository;

    @Override
    public ItemRubric getItemRubricById(long id) {
        // IMPORTANT: difference between getOne abd findById
        // https://www.javacodemonk.com/difference-between-getone-and-findbyid-in-spring-data-jpa-3a96c3ff
        //return this._learningProcessRepository.getOne(id); //throws NestedException if not found
        return this._itemRubricRepository.findById(id).orElse(null);

    }

    @Override
    public List<ItemRubric> getAllItemRubrics() {
        return this._itemRubricRepository.findAll();
    }

    @Override
    public List<ItemRubric> getItemRubricsByRubricId(long rubricId) {
        return this._itemRubricRepository.findByAssessmentRubric_id(rubricId);
    }

    @Override
    public List<ItemRubric> getItemRubricsByLearningProcessId(long lprocessId) {

        List<ItemRubric> itemsRubricFromLearningProcess = new ArrayList<>();

        _assessmentRubricRepository.findByLearningProcess_id(lprocessId).forEach(
                rubric -> itemsRubricFromLearningProcess.addAll(_itemRubricRepository.findByAssessmentRubric_id(rubric.getId()))
        );

        return itemsRubricFromLearningProcess;
    }

    @Override
    public ItemRubric createItemRubric(String description, int weight, AssessmentRubric assessmentRubric) {

        ItemRubric newItemRubric = new ItemRubric();

        newItemRubric.setDescription(description);
        newItemRubric.setWeight(weight);
        newItemRubric.setAssessmentRubric(assessmentRubric);
        newItemRubric = this._itemRubricRepository.save(newItemRubric);
        return newItemRubric;
    }

    @Override
    public ItemRubric updateItemRubric(long itemRubricId, String description, int weight, AssessmentRubric assessmentRubric) {
        ItemRubric itemRubricUpdatable = this._itemRubricRepository.getOne(itemRubricId);

        if (itemRubricUpdatable!=null) {
            itemRubricUpdatable.setDescription(description);
            itemRubricUpdatable.setWeight(weight);
            itemRubricUpdatable.setAssessmentRubric(assessmentRubric);
            itemRubricUpdatable = this._itemRubricRepository.save(itemRubricUpdatable);
        }
        else {
            logger.warn("No item rubric available with id "+itemRubricId);
            itemRubricUpdatable = null;
        }
        return itemRubricUpdatable;

    }

    @Override
    public boolean deleteItemRubric(long itemRubricId) {

        boolean isDeleted = true;
        try {
            this._itemRubricRepository.deleteById(itemRubricId);
        } catch (Exception e) {
            isDeleted = false;
            logger.error(e.getMessage());
        }
        return isDeleted;
    }
}