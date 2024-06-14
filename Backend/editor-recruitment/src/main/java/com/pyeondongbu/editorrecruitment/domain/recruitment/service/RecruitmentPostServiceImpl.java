package com.pyeondongbu.editorrecruitment.domain.recruitment.service;

import com.pyeondongbu.editorrecruitment.domain.common.domain.specification.RecruitmentPostSpecification;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.PostImage;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import com.pyeondongbu.editorrecruitment.global.exception.PostException;
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
        final PostValidationUtils.ValidationResult validationResult = validationUtils.validateRecruitmentPostReq(req);
        final RecruitmentPost post = new RecruitmentPost(member);
        return createOrUpdatePost(post, req, validationResult);
    }

    @Override
    @Transactional
    public RecruitmentPostRes update(Long postId, RecruitmentPostReq req, Long memberId) {
        final RecruitmentPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        final PostValidationUtils.ValidationResult validationResult = validationUtils.validateRecruitmentPostReq(req);
        return createOrUpdatePost(post, req, validationResult);
    }

    @Override
    public RecruitmentPostRes getPost(Long postId, String remoteAddr) {
        final RecruitmentPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));

        validationUtils.validatePostView(postId, remoteAddr);

        post.incrementViewCount();
        postRepository.save(post);
        return RecruitmentPostRes.from(post);
    }

    @Override
    public List<RecruitmentPostRes> listPosts() {
        return postRepository.findAll().stream()
                .map(RecruitmentPostRes::from)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePost(Long postId, Long memberId) {
        final RecruitmentPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        postRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruitmentPostRes> searchRecruitmentPosts(
            Integer maxSubs,
            String title,
            List<String> skills,
            List<String> videoTypes,
            List<String> tagNames
    ) {
        Specification<RecruitmentPost> spec = RecruitmentPostSpecification.combineSpecifications(
                maxSubs,
                title,
                skills,
                videoTypes,
                tagNames
        );

        return postRepository.findAll(spec).stream()
                .map(RecruitmentPostRes::from)
                .collect(Collectors.toList());
    }

    private RecruitmentPostRes createOrUpdatePost(
            RecruitmentPost post,
            RecruitmentPostReq req,
            PostValidationUtils.ValidationResult validationResult
    ) {
        post.update(
                req.getTitle(),
                req.getContent(),
                validationResult.tags(),
                validationResult.payments()
        );
        postImagesHandler(req, post);
        final RecruitmentPostDetails postDetails = RecruitmentPostDetails.of(
                post,
                req.getRecruitmentPostDetailsReq()
        );
        recruitmentPostDetailsRepository.save(postDetails);
        post.setDetails(postDetails);
        postRepository.save(post);
        return RecruitmentPostRes.from(post);
    }

    private void postImagesHandler(RecruitmentPostReq req, RecruitmentPost post) {
        post.getImages().clear();
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            List<PostImage> images = req.getImages().stream()
                    .map(url -> new PostImage(url, post))
                    .collect(Collectors.toList());
            post.addImages(images);
        }
    }
}
