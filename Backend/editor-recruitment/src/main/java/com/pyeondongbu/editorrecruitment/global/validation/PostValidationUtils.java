package com.pyeondongbu.editorrecruitment.global.validation;

import com.pyeondongbu.editorrecruitment.domain.common.dao.PostViewRepository;
import com.pyeondongbu.editorrecruitment.domain.common.domain.PostView;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import com.pyeondongbu.editorrecruitment.domain.tag.dao.TagRepository;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.global.exception.BadRequestException;
import com.pyeondongbu.editorrecruitment.global.exception.InvalidDomainException;
import com.pyeondongbu.editorrecruitment.global.exception.SearchException;
import com.pyeondongbu.editorrecruitment.global.exception.TagException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
public class PostValidationUtils {

    private final TagRepository tagRepository;
    private final PostViewRepository postViewRepository;

    public void validateSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty() || keyword.trim().length() < 2) {
            throw new SearchException(INVALID_SEARCH_CONTENT);
        }
    }

    @Transactional
    public void validatePostView(Long postId, HttpServletRequest request) {
        isValidIp(request.getRemoteAddr());

        String postViewId = postId + ":" + request.getRemoteAddr();
        boolean isFirstView = !postViewRepository.existsById(postViewId);

        if (isFirstView) {
            postViewRepository.save(new PostView(postViewId, postId, request.getRemoteAddr()));
        }
    }

    /**
     * @param tagNames
     * @return 입력된 태그 이름들에 해당하는 모든 태그의 Set
     * @throws TagException 프론트에서 검증하지만 만약 존재하지 않는 태그가 들어올 경우
     */

    public Set<Tag> validateTagsName(List<String> tagNames) {
        final Set<Tag> tags = tagRepository.findByNameIn(tagNames);
        if (tags.size() != tagNames.size()) {
            throw new TagException(INVALID_TAG_NAME);
        }
        return tags;
    }

    public void isValidIp(String ip) {
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(ipRegex);
        Matcher matcher = pattern.matcher(ip);
        if (!matcher.matches()) {
            throw new BadRequestException(INVALID_IP_ADDRESS);
        }
    }

    public Set<Payment> validatePayments(List<PaymentDTO> paymentDTOs) {
        if (paymentDTOs == null || paymentDTOs.isEmpty()) {
            throw new InvalidDomainException(INVALID_PAYMENT);
        }
        return paymentDTOs.stream()
                .map(PaymentDTO::toEntity)
                .collect(Collectors.toSet());
    }
}
