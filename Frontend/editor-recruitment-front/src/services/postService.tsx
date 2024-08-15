// src/services/postService.tsx

// 예시 서비스 함수
export const getPosts = () => {
    // 여기서 실제 API 호출 로직이 들어갈 수 있습니다.
    return [
        { id: 1, title: '구인 공고 1', content: '구인 내용을 여기에 씁니다.' },
        { id: 2, title: '구직 공고 1', content: '구직 내용을 여기에 씁니다.' },
        { id: 3, title: '커뮤니티 글 1', content: '커뮤니티 글 내용을 여기에 씁니다.' }
    ];
};

// 빈 export로 모듈화 강제
export {};
