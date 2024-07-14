package com.pyeondongbu.editorrecruitment.domain.community.service;

import com.pyeondongbu.editorrecruitment.domain.community.dao.CommunityPostRepository;
import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import com.pyeondongbu.editorrecruitment.domain.community.dto.request.CommunityPostReq;
import com.pyeondongbu.editorrecruitment.domain.community.dto.response.CommunityPostRes;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.global.annotation.DistributedLock;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import com.pyeondongbu.editorrecruitment.global.exception.PostException;
import com.pyeondongbu.editorrecruitment.global.service.RedisLockService;
import com.pyeondongbu.editorrecruitment.global.validation.PostValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_USER_NAME;
import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.NOT_FOUND_POST_NAME;

@Service
@RequiredArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {

    private final CommunityPostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostValidationUtils validationUtils;
    private final RedisLockService redisLockService;


    @Override
    public CommunityPostRes create(CommunityPostReq request, Long memberId) {
        final Member member = memberRepository.findByIdWithDetails(memberId)
                .orElseThrow(() -> new AuthException(INVALID_USER_NAME));
        final CommunityPost post = CommunityPost.builder()
                .member(member)
                .build();

        return createOrUpdatePost(post, request);
    }

    @Override
    public CommunityPostRes update(Long postId, CommunityPostReq request, Long memberId) {
        final CommunityPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        return createOrUpdatePost(post, request);
    }

    @Override
    @DistributedLock
    public CommunityPostRes getPost(Long postId, String remoteAddr) {
        final CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));

        if (validationUtils.validatePostView(postId, remoteAddr)) {
            post.incrementViewCount();
            postRepository.save(post);
        }

        return CommunityPostRes.from(post);
    }

    @Override
    public List<CommunityPostRes> listPosts() {
        final List<CommunityPost> posts = postRepository.findAll();
        return posts.stream()
                .map(CommunityPostRes::from)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePost(Long postId, Long memberId) {
        final CommunityPost post = postRepository.findByMemberIdAndId(memberId, postId)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));
        postRepository.delete(post);
    }

    /**
     * Private 함수들
     */

    private CommunityPostRes createOrUpdatePost(
            final CommunityPost post,
            final CommunityPostReq req
    ) {
        post.update(req);
        postRepository.save(post);
        return CommunityPostRes.from(post);
    }

}
