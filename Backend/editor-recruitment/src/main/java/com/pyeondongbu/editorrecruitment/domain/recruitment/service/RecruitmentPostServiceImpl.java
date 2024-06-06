package com.pyeondongbu.editorrecruitment.domain.recruitment.service;


import com.pyeondongbu.editorrecruitment.domain.common.domain.specification.RecruitmentPostSpecification;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostTagReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.PostImage;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.global.exception.*;
import com.pyeondongbu.editorrecruitment.global.validation.PostValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class RecruitmentPostServiceImpl implements RecruitmentPostService {

    private final RecruitmentPostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecruitmentPostDetailsRepository recruitmentPostDetailsRepository;
    private final PostValidationUtils validationUtils;

    @Override
    @Transactional
    public RecruitmentPostRes create(final RecruitmentPostReq req, final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(INVALID_USER_NAME));
        final RecruitmentPost post = new RecruitmentPost(member);
        return createOrUpdatePost(post, req);
    }

    @Override
    @Transactional
    public RecruitmentPostRes update(Long postId, RecruitmentPostReq req, Long memberId) {
        final RecruitmentPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        return createOrUpdatePost(post, req);
    }

    @Override
    public RecruitmentPostRes getPost(Long postId, HttpServletRequest request) {
        final RecruitmentPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        validationUtils.validatePostView(postId, request);
        return RecruitmentPostRes.of(post);
    }

    @Override
    public List<RecruitmentPostRes> listPosts() {
        return postRepository.findAll().stream()
                .map(RecruitmentPostRes::of)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePost(Long postId, Long memberId) {
        final RecruitmentPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        postRepository.delete(post);
    }

    @Override
    public List<RecruitmentPostRes> searchRecruitmentPosts(Integer maxSubs, String title, List<String> skills, List<String> videoTypes, List<String> tagNames) {
        Specification<RecruitmentPost> spec = createSpecification(maxSubs, title, skills, videoTypes, tagNames);
        List<RecruitmentPost> posts = postRepository.findAll(spec);
        return posts.stream()
                .map(RecruitmentPostRes::of)
                .collect(Collectors.toList());
    }

    /**
     * Private 함수들
     */

    private RecruitmentPostRes createOrUpdatePost(RecruitmentPost post, RecruitmentPostReq req) {
        final Set<Tag> tags = validationUtils.validateTagsName(req.getTagNames());
        final Set<Payment> payments = validationUtils.validatePayments(req.getPayments());
        post.update(req.getTitle(), req.getContent(), tags, payments);
        postImagesHandler(req, post);
        RecruitmentPostDetails postDetails = RecruitmentPostDetails.of(post, req.getRecruitmentPostDetailsReq());
        recruitmentPostDetailsRepository.save(postDetails);
        post.setDetails(postDetails);
        postRepository.save(post);
        return RecruitmentPostRes.of(post);
    }

    @Transactional
    private void postImagesHandler(RecruitmentPostReq req, RecruitmentPost post) {
        post.getImages().clear();
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            List<PostImage> images = req.getImages().stream()
                    .map(url -> new PostImage(url, post))
                    .collect(Collectors.toList());
            post.addImages(images);
        }
    }

    private Specification<RecruitmentPost> createSpecification(Integer maxSubs, String title, List<String> skills, List<String> videoTypes, List<String> tagNames) {
        Specification<RecruitmentPost> spec = Specification.where(null);

        if (maxSubs != null) {
            spec = spec.and(RecruitmentPostSpecification.withMaxSubs(maxSubs));
        }
        if (title != null && !title.trim().isEmpty()) {
            spec = spec.and(RecruitmentPostSpecification.containsTitle(title));
        }
        if (skills != null && !skills.isEmpty()) {
            spec = spec.and(RecruitmentPostSpecification.containsSkills(skills));
        }
        if (videoTypes != null && !videoTypes.isEmpty()) {
            spec = spec.and(RecruitmentPostSpecification.containsVideoTypes(videoTypes));
        }
        if (tagNames != null && !tagNames.isEmpty()) {
            spec = spec.and(RecruitmentPostSpecification.withTags(tagNames));
        }

        return spec;
    }
}
