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
import com.pyeondongbu.editorrecruitment.global.annotation.DistributedLock;
import com.pyeondongbu.editorrecruitment.global.config.redis.RedisKey;
import com.pyeondongbu.editorrecruitment.global.dto.ExecuteWithLockParam;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import com.pyeondongbu.editorrecruitment.global.exception.PostException;
import com.pyeondongbu.editorrecruitment.global.service.RedisLockService;
import com.pyeondongbu.editorrecruitment.global.validation.PostValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class RecruitmentPostServiceImpl implements RecruitmentPostService {

    private final RecruitmentPostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecruitmentPostDetailsRepository recruitmentPostDetailsRepository;
    private final PostValidationUtils validationUtils;

    private final RedisLockService redisLockService;

    @Override
    @Transactional
    public RecruitmentPostRes create(final RecruitmentPostReq req, final Long memberId) {
        final Member member = memberRepository.findByIdWithDetails(memberId)
                .orElseThrow(() -> new AuthException(INVALID_USER_NAME));
        final PostValidationUtils.ValidationResult validationResult = validationUtils.validateRecruitmentPostReq(req);
        final RecruitmentPost post = RecruitmentPost.builder()
                                                    .member(member)
                                                    .build();
        return createOrUpdatePost(post, req, validationResult);
    }

    @Override
    @Transactional
    public RecruitmentPostRes update(final Long postId, final RecruitmentPostReq req, final Long memberId) {
        final RecruitmentPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        final PostValidationUtils.ValidationResult validationResult = validationUtils.validateRecruitmentPostReq(req);
        return createOrUpdatePost(post, req, validationResult);
    }

    @Override
    @DistributedLock
    public RecruitmentPostRes getPost(final Long postId, final String remoteAddr) {
        final RecruitmentPost post = postRepository.findByIdWithDetails(postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));

        if (validationUtils.validatePostView(postId, remoteAddr)) {
            post.incrementViewCount();
            postRepository.save(post);
        }

        return RecruitmentPostRes.from(post);
    }

    @Override
    public List<RecruitmentPostRes> listPosts() {
        final List<RecruitmentPost> posts = postRepository.findAllWithDetails();
        return posts.stream()
                .map(RecruitmentPostRes::from)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePost(final Long postId, final Long memberId) {
        final RecruitmentPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        postRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruitmentPostRes> searchRecruitmentPosts(
            final Integer maxSubs,
            final String title,
            final List<String> skills,
            final List<String> videoTypes,
            final List<String> tagNames
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

    @Override
    @Transactional(readOnly = true)
    public List<RecruitmentPostRes> searchRecruitmentPostsByTags(
            final List<String> tagNames
    ) {
        Specification<RecruitmentPost> spec = RecruitmentPostSpecification.withTags(
                tagNames
        );

        return postRepository.findAll(spec).stream()
                .map(RecruitmentPostRes::from)
                .collect(Collectors.toList());
    }

    /**
     * Private 함수들
     */

    private RecruitmentPostRes createOrUpdatePost(
            final RecruitmentPost post,
            final RecruitmentPostReq req,
            final PostValidationUtils.ValidationResult validationResult
    ) {
        post.update(req, validationResult);
        postImagesHandler(req, post);
        RecruitmentPostDetails postDetails = post.getDetails();
        if (postDetails == null) {
            postDetails = RecruitmentPostDetails.of(post, req.getRecruitmentPostDetailsReq());
            post.setDetails(postDetails);
        } else {
            postDetails.update(req.getRecruitmentPostDetailsReq());
        }
        recruitmentPostDetailsRepository.save(postDetails);
        postRepository.save(post);
        return RecruitmentPostRes.from(post);
    }

    private void postImagesHandler(final RecruitmentPostReq req, final RecruitmentPost post) {
        post.getImages().clear();
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            List<PostImage> images = req.getImages().stream()
                    .map(url -> new PostImage(url, post))
                    .collect(Collectors.toList());
            post.addImages(images);
        }
    }
}
