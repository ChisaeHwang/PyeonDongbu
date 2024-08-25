import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../styles/RecruitmentPostList.css';

interface PaymentDTO {
    type: string;
    amount: number;
}

interface RecruitmentPostDetailsRes {
    maxSubs: number;
    videoTypes: string[];
    skills: string[];
    remarks: string;
}

interface RecruitmentPost {
    id: number;
    title: string;
    content: string;
    memberName: string;
    createdAt: string;
    modifiedAt: string;
    viewCount: number;
    images: string[];
    tagNames: string[];
    payments: PaymentDTO[];
    recruitmentPostDetailsRes: RecruitmentPostDetailsRes;
}

interface RecruitmentPostListProps {
    maxSubs?: number;
    title?: string;
    skills?: string[];
    videoTypes?: string[];
    tagNames?: string[];
}

const RecruitmentPostList: React.FC<RecruitmentPostListProps> = ({
    maxSubs,
    title,
    skills,
    videoTypes,
    tagNames
}) => {
    const [posts, setPosts] = useState<RecruitmentPost[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchPosts = async () => {
            setLoading(true);
            setError(null);
            try {
                const response = await axios.get('http://localhost:8080/api/recruitment/posts/search/by-details', {
                    params: {
                        maxSubs,
                        title,
                        skills: skills?.join(','),
                        videoTypes: videoTypes?.join(','),
                        tagNames: tagNames?.join(',')
                    }
                });
                setPosts(response.data.data);
            } catch (error) {
                console.error('구인 게시글을 불러오는데 실패했습니다.', error);
                setError('게시글을 불러오는데 실패했습니다. 다시 시도해주세요.');
            } finally {
                setLoading(false);
            }
        };

        fetchPosts();
    }, [maxSubs, title, skills, videoTypes, tagNames]);

    if (loading) {
        return <div className="loading">로딩 중...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="recruitment-post-list">
            {posts.map((post) => (
                <div key={`${post.id}-${post.createdAt}`} className="recruitment-post-item">
                    <div className="post-image">
                        {post.images && post.images.length > 0 && (
                            <img src={post.images[0]} alt={`게시글 이미지`} />
                        )}
                    </div>
                    <h3 className="post-title">{post.title}</h3>
                    <div className="post-remarks">
                        {post.recruitmentPostDetailsRes && post.recruitmentPostDetailsRes.remarks && 
                         post.recruitmentPostDetailsRes.remarks.split(',').map((remark, index) => (
                            <span key={`${post.id}-${index}`} className="remark">#{remark.trim()}</span>
                        ))}
                    </div>
                </div>
            ))}
            {posts.length === 0 && <div className="no-posts">게시글이 없습니다.</div>}
        </div>
    );
};

export default RecruitmentPostList;