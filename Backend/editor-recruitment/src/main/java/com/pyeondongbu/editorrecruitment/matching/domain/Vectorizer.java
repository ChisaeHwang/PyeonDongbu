package com.pyeondongbu.editorrecruitment.matching.domain;

import com.pyeondongbu.editorrecruitment.domain.common.domain.Details;
import com.pyeondongbu.editorrecruitment.global.exception.DetailsException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_POST_DETAILS;

@Component
public class Vectorizer {

    private Map<String, Integer> skillIndex;
    private Map<String, Integer> videoTypeIndex;

    public Vectorizer(List<String> allSkills, List<String> allVideoTypes) {
        this.skillIndex = new HashMap<>();
        this.videoTypeIndex = new HashMap<>();

        int index = 0;
        for (String skill : allSkills) {
            skillIndex.put(skill, index++);
        }
        for (String videoType : allVideoTypes) {
            videoTypeIndex.put(videoType, index++);
        }
    }

    public double[] vectorize(Details details) {

        if (details == null) {
            throw new DetailsException(INVALID_POST_DETAILS);
        }

        double[] vector = new double[skillIndex.size() + videoTypeIndex.size() + 1];

        for (String skill : details.getSkills()) {
            if (skillIndex.containsKey(skill)) {
                vector[skillIndex.get(skill)] = 1;
            }
        }
        for (String videoType : details.getVideoTypes()) {
            if (videoTypeIndex.containsKey(videoType)) {
                vector[videoTypeIndex.get(videoType)] = 1;
            }
        }
        return vector;
    }
}
