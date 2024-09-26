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
        Payment payment = validatePayment(postReq.getPayment());

        return new ValidationResult(tags, payment);
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

    private Payment validatePayment(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            throw new InvalidDomainException(INVALID_PAYMENT);
        }
        return paymentDTO.toEntity();
    }

    private void isValidIp(String ip) {
        // 로컬호스트 IP 허용
        // TO-DO 이 부분에 대한 코드 개선이 필요할 거 같음..
        if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
            return;
        }

        String ipv4Regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String ipv6Regex = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

        Pattern ipv4Pattern = Pattern.compile(ipv4Regex);
        Pattern ipv6Pattern = Pattern.compile(ipv6Regex);

        if (!ipv4Pattern.matcher(ip).matches() && !ipv6Pattern.matcher(ip).matches()) {
            throw new BadRequestException(INVALID_IP_ADDRESS);
        }
    }


    public record ValidationResult(Set<Tag> tags, Payment payment) {
    }
}
