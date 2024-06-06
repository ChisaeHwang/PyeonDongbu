package com.pyeondongbu.editorrecruitment.domain.recruitment.service;

import com.pyeondongbu.editorrecruitment.domain.common.dao.PostViewRepository;
import com.pyeondongbu.editorrecruitment.domain.common.domain.PostView;
import com.pyeondongbu.editorrecruitment.domain.common.domain.specification.RecruitmentPostDetailsSpecification;
import com.pyeondongbu.editorrecruitment.domain.common.domain.specification.RecruitmentPostSpecification;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostTagReq;
import com.pyeondongbu.editorrecruitment.domain.tag.dao.TagRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.PostImage;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.PostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostUpdateReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.PostRes;
import com.pyeondongbu.editorrecruitment.global.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class RecruitmentPostServiceImpl implements RecruitmentPostService {

    private final RecruitmentPostRepository postRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final PostViewRepository postViewRepository;
    private final RecruitmentPostDetailsRepository recruitmentPostDetailsRepository;

    @Override
    @Transactional
    public PostRes create(final RecruitmentPostReq req, final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(INVALID_USER_NAME));
        final Set<Tag> tags = validateTagsName(req.getTagNames());
        final Set<Payment> payments = validatePayments(req.getPayments());
        final RecruitmentPost post = RecruitmentPost.of(
                req.getTitle(),
                req.getContent(),
                member,
                tags,
                payments
        );
        postImagesHandler(req, post);
        RecruitmentPostDetails postDetails = RecruitmentPostDetails.of(
                post,
                req.getRecruitmentPostDetailsReq()
        );
        recruitmentPostDetailsRepository.save(postDetails);
        postRepository.save(post);
        return PostRes.of(post);
    }

    @Override
    public PostRes getPost(Long postId, HttpServletRequest request) {
        final RecruitmentPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        validatePostView(postId, request, post);
        return PostRes.of(post);
    }

    @Override
    public List<PostRes> listPosts() {
        return postRepository.findAll().stream()
                .map(PostRes::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostRes update(Long postId, RecruitmentPostUpdateReq req, Long memberId) {
        final RecruitmentPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        final Set<Tag> tags = validateTagsName(req.getTagNames());
        final Set<Payment> payments = validatePayments(req.getPayments());
        post.update(
                req.getTitle(),
                req.getContent(),
                tags,
                payments
        );
        postImagesHandler(req, post);
        RecruitmentPostDetails postDetails = RecruitmentPostDetails.of(
                post,
                req.getRecruitmentPostDetailsReq()
        );
        recruitmentPostDetailsRepository.save(postDetails);
        postRepository.save(post);
        return PostRes.of(post);
    }

    @Override
    public void deletePost(Long postId, Long memberId) {
        final RecruitmentPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        postRepository.delete(post);
    }

    @Override
    public List<PostRes> searchPosts(String keyword) {
        validateSearchKeyword(keyword);
        return postRepository.searchByKeyword(keyword)
                .stream()
                .map(PostRes::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostRes> searchRecruitmentPosts(
            Integer maxSubs,
            String skill,
            String videoType,
            String tagName
    ) {
        Specification<RecruitmentPost> spec = createSpecification(
                maxSubs,
                skill,
                videoType,
                tagName
        );
        List<RecruitmentPost> posts = postRepository.findAll(spec);
        return posts.stream()
                .map(PostRes::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostRes> searchPostsByTags(RecruitmentPostTagReq req) {
        final Set<Tag> tags = validateTagsName(req.getTagNames());
        final Set<Long> tagIds = tags.stream()
                .map(Tag::getId).
                collect(Collectors.toSet());
        final List<RecruitmentPost> posts = postRepository.findByTagIds(tagIds);
        return posts.stream()
                .map(PostRes::of)
                .collect(Collectors.toList());
    }

    @Transactional
    private void postImagesHandler(PostReq req, RecruitmentPost post) {
        post.getImages().clear();
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            List<PostImage> images = req.getImages().stream()
                    .map(url -> new PostImage(url, post))
                    .collect(Collectors.toList());
            post.addImages(images);
        }
    }

    private Specification<RecruitmentPost> createSpecification(
            Integer maxSubs,
            String skill,
            String videoType,
            String tagName
    ) {
        List<Specification<RecruitmentPost>> specs = new ArrayList<>();

        if (maxSubs != null) {
            specs.add(RecruitmentPostSpecification.byDetails(RecruitmentPostDetailsSpecification.equalMaxSubs(maxSubs)));
        }
        if (skill != null) {
            specs.add(RecruitmentPostSpecification.byDetails(RecruitmentPostDetailsSpecification.containsSkill(skill)));
        }
        if (videoType != null) {
            specs.add(RecruitmentPostSpecification.byDetails(RecruitmentPostDetailsSpecification.containsVideoType(videoType)));
        }
        if (tagName != null) {
            specs.add(RecruitmentPostSpecification.withTag(tagName));
        }

        return specs.stream().reduce(Specification::and).orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
    }

    private void validateSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty() || keyword.trim().length() < 2) {
            throw new SearchException(INVALID_SEARCH_CONTENT);
        }
    }

    @Transactional
    private void validatePostView(Long postId, HttpServletRequest request, RecruitmentPost post) {

        isValidIp(request.getRemoteAddr());

        String postViewId = postId + ":" + request.getRemoteAddr();
        boolean isFirstView = !postViewRepository.existsById(postViewId);

        if (isFirstView) {
            postViewRepository.save(new PostView(
                            postViewId,
                            postId,
                            request.getRemoteAddr())
            );
            post.incrementViewCount();
            postRepository.save(post);
        }
    }

    /**
     * @param tagNames
     * @return 입력된 태그 이름들에 해당하는 모든 태그의 Set
     * @throws TagException 프론트에서 검증하지만 만약 존재하지 않는 태그가 들어올 경우
     */
    private Set<Tag> validateTagsName(List<String> tagNames) {
        final Set<Tag> tags = tagRepository.findByNameIn(tagNames);
        if (tags.size() != tagNames.size()) {
            throw new TagException(INVALID_TAG_NAME);
        }
        return tags;
    }

    private Set<Payment> validatePayments(Set<PaymentDTO> paymentDTOs) {
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
        if(!matcher.matches()){
            throw new BadRequestException(INVALID_IP_ADDRESS);
        }
    }


}
