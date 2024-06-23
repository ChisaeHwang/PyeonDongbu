package com.pyeondongbu.editorrecruitment.global.validation;

import com.pyeondongbu.editorrecruitment.domain.common.dao.PostViewRepository;
import com.pyeondongbu.editorrecruitment.domain.common.domain.PostView;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.tag.dao.TagRepository;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.global.exception.BadRequestException;
import com.pyeondongbu.editorrecruitment.global.exception.InvalidDomainException;
import com.pyeondongbu.editorrecruitment.global.exception.TagException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidationUtils {

    private final TagRepository tagRepository;
    private final PostViewRepository postViewRepository;
    private final Validator validator;


    public Boolean validatePostView(Long postId, String remoteAddr) {
        isValidIp(remoteAddr);

        String postViewId = postId + ":" + remoteAddr;
        boolean isFirstView = !postViewRepository.existsById(postViewId);

        if (isFirstView) {
            postViewRepository.save(new PostView(postViewId, postId, remoteAddr));
            return true;
        }

        return false;
    }

    public ValidationResult validateRecruitmentPostReq(RecruitmentPostReq postReq) {
        Set<ConstraintViolation<RecruitmentPostReq>> violations = validator.validate(postReq);
        if (!violations.isEmpty()) {
            throw new BadRequestException(INVALID_POST_DETAILS);
        }

        Set<Tag> tags = validateTagNames(postReq.getTagNames());
        Set<Payment> payments = validatePayments(postReq.getPayments());

        return new ValidationResult(tags, payments);
    }

    private Set<Tag> validateTagNames(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            throw new BadRequestException(INVALID_TAG_NAME);
        }

        Set<Tag> tags = tagRepository.findByNameIn(tagNames);
        if (tags.size() != tagNames.size()) {
            throw new TagException(INVALID_TAG_NAME);
        }
        return tags;
    }

    private Set<Payment> validatePayments(List<PaymentDTO> paymentDTOs) {
        if (paymentDTOs == null || paymentDTOs.isEmpty()) {
            throw new InvalidDomainException(INVALID_PAYMENT);
        }
        return paymentDTOs.stream()
                .map(PaymentDTO::toEntity)
                .collect(Collectors.toSet());
    }

    private void isValidIp(String ip) {
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(ipRegex);
        Matcher matcher = pattern.matcher(ip);
        if (!matcher.matches()) {
            throw new BadRequestException(INVALID_IP_ADDRESS);
        }
    }

    public record ValidationResult(Set<Tag> tags, Set<Payment> payments) {
    }
}
