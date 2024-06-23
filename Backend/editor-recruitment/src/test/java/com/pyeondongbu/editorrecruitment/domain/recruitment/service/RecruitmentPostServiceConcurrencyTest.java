package com.pyeondongbu.editorrecruitment.domain.recruitment.service;

import com.pyeondongbu.editorrecruitment.domain.common.dao.PostViewRepository;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.PostImageRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.type.PaymentType;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostDetailsReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.tag.dao.TagRepository;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.global.exception.PostException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.NOT_FOUND_POST_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
public class RecruitmentPostServiceConcurrencyTest {

    @Autowired
    private RecruitmentPostService postService;
    @Autowired
    private RecruitmentPostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostViewRepository postViewRepository;
    @Autowired
    private PostImageRepository postImageRepository;
    @Autowired
    private RecruitmentPostDetailsRepository postDetailsRepository;
    @Autowired
    private RedissonClient redissonClient;


    @BeforeEach
    public void setUp() {
        RecruitmentPost post = postRepository.findById(3L)
                .orElseThrow(() -> new PostException(NOT_FOUND_POST_NAME));

        post.setViewCount(0);
        postRepository.save(post);

        RKeys keys = redissonClient.getKeys();
        Iterable<String> keysToDelete = keys.getKeysByPattern("postView:3:*");
        keysToDelete.forEach(key -> redissonClient.getBucket(key).delete());
    }



    @RepeatedTest(10)
    public void testConcurrentViewFromDifferentIPs() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Long postId = postRepository.findAll().get(0).getId();

        for (int i = 0; i < threadCount; i++) {
            final String remoteAddr = "192.168.0." + i;
            executorService.submit(() -> {
                try {
                    postService.getPost(postId, remoteAddr);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        RecruitmentPost postAfter = postRepository.findById(postId).orElseThrow();
        int finalViewCount = postAfter.getViewCount();
        log.info("ViewCount : " + finalViewCount);
        assertEquals(threadCount, finalViewCount);
    }

}


