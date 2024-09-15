import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/CommunityPage.css';
import { AiOutlineSearch } from 'react-icons/ai';

interface CommunityPost {
    id: number;
    title: string;
    content: string;
    memberName: string;
    createdAt: string;
    modifiedAt: string;
    viewCount: number;
}

const CommunityPage: React.FC = () => {
    const [posts, setPosts] = useState<CommunityPost[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('all');
    const searchInputRef = useRef<HTMLInputElement>(null);
    const navigate = useNavigate();
    const [popularPosts, setPopularPosts] = useState<CommunityPost[]>([]);

    useEffect(() => {
        // TODO: API로 게시글 데이터를 가져오는 로직 구현
        // 임시 데이터
        const tempPosts: CommunityPost[] = [
            {
                id: 1,
                title: '유튜브 편집 팁 공유합니다',
                content: '안녕하세요, 오늘은 제가 유튜브 영상을 편집할 때 사용하는 꿀팁을 공유하려고 합니다...',
                memberName: '편집왕',
                createdAt: '2023-05-15T10:00:00',
                modifiedAt: '2023-05-15T10:00:00',
                viewCount: 120
            },
            {
                id: 2,
                title: '초보 유튜버를 위한 장비 추천',
                content: '유튜브를 시작하려는 분들을 위해 제가 사용하는 장비들을 추천해드리려고 합니다...',
                memberName: '장비덕후',
                createdAt: '2023-05-14T15:30:00',
                modifiedAt: '2023-05-14T15:30:00',
                viewCount: 85
            },
            {
                id: 3,
                title: '썸네일 제작 노하우',
                content: '클릭을 유도하는 매력적인 썸네일을 만드는 방법을 알려드립니다...',
                memberName: '썸네일러',
                createdAt: '2023-05-13T09:15:00',
                modifiedAt: '2023-05-13T09:15:00',
                viewCount: 200
            },
            {
                id: 4,
                title: '3D 모델링 기초 강좌',
                content: '3D 모델링을 처음 시작하시는 분들을 위한 기초 강좌입니다...',
                memberName: '모델링고수',
                createdAt: '2023-05-12T14:00:00',
                modifiedAt: '2023-05-12T14:00:00',
                viewCount: 150
            },
            {
                id: 1,
                title: '유튜브 편집 팁 공유합니다',
                content: '안녕하세요, 오늘은 제가 유튜브 영상을 편집할 때 사용하는 꿀팁을 공유하려고 합니다...',
                memberName: '편집왕',
                createdAt: '2023-05-15T10:00:00',
                modifiedAt: '2023-05-15T10:00:00',
                viewCount: 120
            },
            {
                id: 6,
                title: '초보 유튜버를 위한 장비 추천',
                content: '유튜브를 시작하려는 분들을 위해 제가 사용하는 장비들을 추천해드리려고 합니다...',
                memberName: '장비덕후',
                createdAt: '2023-05-14T15:30:00',
                modifiedAt: '2023-05-14T15:30:00',
                viewCount: 85
            },
            {
                id: 7,
                title: '썸네일 제작 노하우',
                content: '클릭을 유도하는 매력적인 썸네일을 만드는 방법을 알려드립니다...',
                memberName: '썸네일러',
                createdAt: '2023-05-13T09:15:00',
                modifiedAt: '2023-05-13T09:15:00',
                viewCount: 200
            },
            {
                id: 8,
                title: '3D 모델링 기초 강좌',
                content: '3D 모델링을 처음 시작하시는 분들을 위한 기초 강좌입니다...',
                memberName: '모델링고수',
                createdAt: '2023-05-12T14:00:00',
                modifiedAt: '2023-05-12T14:00:00',
                viewCount: 150
            },
        ];
        setPosts(tempPosts);

        // 인기 게시글 데이터 (임시)
        const tempPopularPosts: CommunityPost[] = [
            {
                id: 101,
                title: '1만 구독자 달성 노하우',
                content: '제가 1만 구독자를 달성하기까지의 여정과 노하우를 공유합니다...',
                memberName: '인기유튜버',
                createdAt: '2023-05-10T12:00:00',
                modifiedAt: '2023-05-10T12:00:00',
                viewCount: 1500
            },
            {
                id: 102,
                title: '영상 편집 초보 탈출하기',
                content: '영상 편집 초보자들이 꼭 알아야 할 팁들을 정리했습니다...',
                memberName: '편집전문가',
                createdAt: '2023-05-09T11:30:00',
                modifiedAt: '2023-05-09T11:30:00',
                viewCount: 1200
            },
            {
                id: 103,
                title: '유튜브 수익화 방법 총정리',
                content: '유튜브로 수익을 창출하는 다양한 방법들을 소개합니다...',
                memberName: '수익왕',
                createdAt: '2023-05-08T10:00:00',
                modifiedAt: '2023-05-08T10:00:00',
                viewCount: 2000
            },
            {
                id: 104,
                title: '인기 있는 유튜브 주제 찾기',
                content: '트렌디하고 인기 있는 유튜브 주제를 찾는 방법을 알려드립니다...',
                memberName: '트렌드헌터',
                createdAt: '2023-05-07T09:00:00',
                modifiedAt: '2023-05-07T09:00:00',
                viewCount: 1800
            },
            {
                id: 105,
                title: '유튜브 알고리즘 공략법',
                content: '유튜브 알고리즘을 이해하고 효과적으로 활용하는 방법을 설명합니다...',
                memberName: '알고리즘마스터',
                createdAt: '2023-05-06T08:30:00',
                modifiedAt: '2023-05-06T08:30:00',
                viewCount: 2200
            },
        ];
        setPopularPosts(tempPopularPosts);
    }, []);

    const categories = ['편집', '유튜버', '썸네일러', '모델링'];

    const handleSearch = () => {
        // 검색 로직 구현
        console.log('Searching for:', searchTerm);
    };

    const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter') {
            handleSearch();
        }
    };

    const handleSearchIconClick = () => {
        if (searchInputRef.current) {
            searchInputRef.current.focus();
        }
    };

    const handlePostClick = (postId: number) => {
        navigate(`/community/post/${postId}`);
    };

    return (
        <div className="community-page">
            <div className="content-wrapper">
                <div className="popular-posts">
                    <h3>현재 인기 게시글</h3>
                    {popularPosts.slice(0, 5).map((post) => (
                        <div key={post.id} className="popular-post-item" onClick={() => handlePostClick(post.id)}>
                            <h4>{post.title}</h4>
                            <p>{post.content.slice(0, 15)}...</p>
                            <span className="popular-post-author">{post.memberName}</span>
                        </div>
                    ))}
                </div>
                <div className="main-content">
                    <div className="community-search-bar">
                        <AiOutlineSearch className="search-icon" onClick={handleSearchIconClick} />
                        <input
                            ref={searchInputRef}
                            type="text"
                            placeholder="검색어를 입력하세요"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            onKeyPress={handleKeyPress}
                            className="search-input"
                        />
                    </div>
                    <div className="category-bar">
                        <button
                            className={selectedCategory === 'all' ? 'active' : ''}
                            onClick={() => setSelectedCategory('all')}
                        >
                            전체
                        </button>
                        {categories.map((category) => (
                            <button
                                key={category}
                                className={selectedCategory === category ? 'active' : ''}
                                onClick={() => setSelectedCategory(category)}
                            >
                                {category}
                            </button>
                        ))}
                    </div>
                    <div className="post-list">
                        {posts.map((post) => (
                            <div 
                                key={post.id} 
                                className="post-item" 
                                onClick={() => handlePostClick(post.id)}
                            >
                                <h3>{post.title}</h3>
                                <p>{post.content}</p>
                                <div className="post-info">
                                    <span className="post-author">{post.memberName}</span>
                                    <span className="post-date">{new Date(post.createdAt).toLocaleDateString()}</span>
                                    <span className="post-views">조회수: {post.viewCount}</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CommunityPage;