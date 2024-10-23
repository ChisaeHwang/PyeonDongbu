package com.pyeondongbu.editorrecruitment.domain.community.service;

import com.pyeondongbu.editorrecruitment.domain.common.domain.specification.CommunityPostSpecification;
import com.pyeondongbu.editorrecruitment.domain.community.dao.CommunityPostRepository;
import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import com.pyeondongbu.editorrecruitment.domain.community.dto.request.CommunityPostReq;
import com.pyeondongbu.editorrecruitment.domain.community.dto.response.CommunityPostRes;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.global.annotation.DistributedLock;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import com.pyeondongbu.editorrecruitment.global.exception.PostException;
import com.pyeondongbu.editorrecruitment.global.service.RedisLockService;
import com.pyeondongbu.editorrecruitment.global.validation.PostValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {

    private final CommunityPostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostValidationUtils validationUtils;

    private final RedisLockService redisLockService;


    @Override
    @Transactional
    public CommunityPostRes create(final CommunityPostReq req, final Long memberId) {
        final Member member = memberRepository.findByIdWithDetails(memberId)
                .orElseThrow(() -> new AuthException(INVALID_USER_NAME));
        final PostValidationUtils.ValidationResult validationResult = validationUtils.validateCommunityPostReq(req);
        final CommunityPost post = CommunityPost.builder()
                .member(member)
                .build();
        return createOrUpdatePost(post, req, validationResult);
    }

    @Override
    @Transactional
    public CommunityPostRes update(final Long postId, final CommunityPostReq req, final Long memberId) {
        final CommunityPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        final PostValidationUtils.ValidationResult validationResult = validationUtils.validateCommunityPostReq(req);
        return createOrUpdatePost(post, req, validationResult);
    }

    @Override
    @Transactional(readOnly = true)
    @DistributedLock
    public CommunityPostRes getPost(final Long postId, final String remoteAddr, final Long memberId) {
        final CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));

        if (validationUtils.validatePostView(postId, remoteAddr)) {
            post.incrementViewCount();
            postRepository.save(post);
        }

        boolean isAuthor = false;
        if (memberId != null && post.getMember().getId().equals(memberId)) {
            isAuthor = true;
        }

        return CommunityPostRes.from(post, isAuthor);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityPostRes getPostForEdit(final Long postId, final Long memberId) {
        final CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));

        if (!post.getMember().getId().equals(memberId)) {
            throw new AuthException(INVALID_AUTHORITY);
        }

        return CommunityPostRes.from(post, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostRes> getMyPosts(final Long memberId) {
        final List<CommunityPost> posts = postRepository.findByMemberId(memberId);
        return posts.stream()
                .map(CommunityPostRes::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostRes> listPosts() {
        final List<CommunityPost> posts = postRepository.findAll();
        return posts.stream()
                .map(CommunityPostRes::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostRes> getPopularPosts(int limit) {
        final Pageable topN = PageRequest.of(0, limit);
        final List<CommunityPost> posts = postRepository.findTopByOrderByViewCountDesc(topN);
        return posts.stream()
                .map(CommunityPostRes::from)
                .collect(Collectors.toList());
    }

    // To-DO 필요 없는 로직, 나중에 필요 없는 거 확실하면 삭제
    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostRes> searchPostsByTags(List<String> tagNames) {
        List<CommunityPost> posts = postRepository.findByTagNames(tagNames);
        return posts.stream()
                .map(CommunityPostRes::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommunityPostRes> searchCommunityPosts(
            final String search,
            final List<String> tagNames,
            final Pageable pageable
    ) {
        Specification<CommunityPost> spec = CommunityPostSpecification.combineSpecifications(
                search,
                tagNames
        );

        return postRepository.findAll(spec, pageable)
                .map(CommunityPostRes::from);
    }

    @Override
    @Transactional
    public void deletePost(final Long postId, final Long memberId) {
        final CommunityPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        postRepository.delete(post);
    }

    /**
     * Private 함수들
     */

    private CommunityPostRes createOrUpdatePost(
            final CommunityPost post,
            final CommunityPostReq req,
            final PostValidationUtils.ValidationResult validationResult
    ) {
        post.update(req, validationResult);
        postRepository.save(post);
        return CommunityPostRes.from(post);
    }

}
