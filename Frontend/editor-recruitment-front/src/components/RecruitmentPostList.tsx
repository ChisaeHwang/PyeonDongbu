import React, { useState, useEffect, useCallback } from 'react';
import { debounce } from 'lodash';
import Slider from 'react-slick';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import '../styles/RecruitmentPostList.css';
import NextArrowIcon from '../assets/NextArrowIcon';
import PrevArrowIcon from '../assets/PrevArrowIcon';
import { CustomArrowProps } from "react-slick";

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
    skills: string[];
    videoTypes: string[];
    tagNames: string[];
}

const RecruitmentPostItem: React.FC<{ post: RecruitmentPost }> = ({ post }) => (
    <div className="recruitment-post-item">
        <div className="post-image">
            {post.images && post.images.length > 0 && (
                <img 
                    src={post.images[0]} 
                    alt={`게시글 이미지`} 
                    loading="lazy"
                    onError={(e) => {
                        const target = e.target as HTMLImageElement;
                        target.src = '/path/to/placeholder-image.jpg';
                    }}
                />
            )}
        </div>
        <h3 className="post-title">{post.title}</h3>
        <div className="post-remarks">
            {post.recruitmentPostDetailsRes && post.recruitmentPostDetailsRes.remarks && 
             post.recruitmentPostDetailsRes.remarks.split(',').map((remark, index) => (
                <span key={`${post.id}-${index}`} className="remark">{remark.trim()}</span>
            ))}
        </div>
    </div>
);

const NextArrow = (props: CustomArrowProps) => (
    <div {...props} className="slick-next custom-arrow-wrapper">
      <div className="custom-arrow">
        <NextArrowIcon />
      </div>
    </div>
  );
  
  const PrevArrow = (props: CustomArrowProps) => (
    <div {...props} className="slick-prev custom-arrow-wrapper">
      <div className="custom-arrow">
        <PrevArrowIcon />
      </div>
    </div>
  );

const RecruitmentPostList: React.FC<RecruitmentPostListProps> = ({ skills, videoTypes, tagNames }) => {
    const [posts, setPosts] = useState<RecruitmentPost[]>([]);
    const [filteredPosts, setFilteredPosts] = useState<RecruitmentPost[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchPosts = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await fetch(`http://localhost:8080/api/recruitment/posts/search/by-details?skills=${skills.join(',')}&videoTypes=${videoTypes.join(',')}&tagNames=${tagNames.join(',')}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) {
                throw new Error('서버 응답이 실패했습니다');
            }
            const data = await response.json();
            setPosts(data.data);
            setFilteredPosts(data.data);
        } catch (error) {
            console.error('구인 게시글을 불러오는데 실패했습니다.', error);
            setError('게시글을 불러오는데 실패했습니다. 다시 시도해주세요.');
        } finally {
            setLoading(false);
        }
    }, [skills, videoTypes, tagNames]);

    useEffect(() => {
        fetchPosts();
    }, [fetchPosts]);

    useEffect(() => {
        const filterPosts = () => {
            const filtered = posts.filter(post => 
                (!skills.length || post.recruitmentPostDetailsRes.skills.some(skill => skills.includes(skill))) &&
                (!videoTypes.length || post.recruitmentPostDetailsRes.videoTypes.some(type => videoTypes.includes(type))) &&
                (!tagNames.length || post.tagNames.some(tag => tagNames.includes(tag)))
            );
            setFilteredPosts(filtered);
        };

        const debouncedFilterPosts = debounce(filterPosts, 300);
        debouncedFilterPosts();

        return () => {
            debouncedFilterPosts.cancel();
        };
    }, [posts, skills, videoTypes, tagNames]);

    const settings = {
        dots: false,
        infinite: false,
        speed: 500,
        slidesToShow: 6,  // 1920x1080에서 더 많은 아이템 표시
        slidesToScroll: 3,
        nextArrow: <NextArrow />,
        prevArrow: <PrevArrow />,
        responsive: [
            {
                breakpoint: 1600,
                settings: {
                    slidesToShow: 5,
                    slidesToScroll: 3,
                }
            },
            {
                breakpoint: 1400,
                settings: {
                    slidesToShow: 4,
                    slidesToScroll: 2,
                }
            },
            {
                breakpoint: 1100,
                settings: {
                    slidesToShow: 3,
                    slidesToScroll: 2,
                }
            },
            {
                breakpoint: 800,
                settings: {
                    slidesToShow: 2,
                    slidesToScroll: 1,
                }
            },
            {
                breakpoint: 480,
                settings: {
                    slidesToShow: 1,
                    slidesToScroll: 1
                }
            }
        ]
    };

    if (loading) {
        return <div className="loading">로딩 중...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="recruitment-post-list-container">
            <Slider {...settings}>
                {filteredPosts.map((post) => (
                    <div key={post.id}>
                        <RecruitmentPostItem post={post} />
                    </div>
                ))}
            </Slider>
            {filteredPosts.length === 0 && <div className="no-posts">게시글이 없습니다.</div>}
        </div>
    );
};

export default RecruitmentPostList;