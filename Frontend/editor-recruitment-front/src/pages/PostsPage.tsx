import { useState, useEffect } from 'react';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import NextArrowIcon from '../assets/NextArrowIcon';
import PrevArrowIcon from '../assets/PrevArrowIcon';
import Banner from '../components/Banner';
import { CustomArrowProps } from "react-slick";
import RecruitmentPostList from '../components/RecruitmentPostList';
import FilterButtonGroup from '../components/FilterButtonGroup';
import { FilterOptions } from '../utils/FilterOptions';
import '../styles/PostsPage.css';

// NextArrow와 PrevArrow 컴포넌트 추가
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


const PostsPage = () => {
  const [selectedSkills, setSelectedSkills] = useState<string[]>([]);
  const [selectedVideoTypes, setSelectedVideoTypes] = useState<string[]>([]);

  const handleOptionToggle = (name: string, type: 'skill' | 'videoType') => {
    if (type === 'skill') {
      setSelectedSkills(prev => 
        prev.includes(name) ? prev.filter(skill => skill !== name) : [...prev, name]
      );
    } else {
      setSelectedVideoTypes(prev => 
        prev.includes(name) ? prev.filter(videoType => videoType !== name) : [...prev, name]
      );
    }
  };

  useEffect(() => {
    console.log('선택된 스킬:', selectedSkills);
    console.log('선택된 비디오 타입:', selectedVideoTypes);
  }, [selectedSkills, selectedVideoTypes]);

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


  return (
    <div className="posts-page">
      <Banner />
      <div className='posts-container youtuber-section'>
        <h2 className="current-partners-title">현재 파트너를 찾고 있는 유튜버 분들</h2>
        <FilterButtonGroup
          options={FilterOptions}
          selectedOptions={[...selectedSkills, ...selectedVideoTypes]}
          onOptionToggle={handleOptionToggle}
        />
        <RecruitmentPostList
          skills={selectedSkills}
          videoTypes={selectedVideoTypes}
          tagNames={['크롤링', '구인']}
          sliderSettings={settings}
          variant="main"
        />
      </div>
      <div className='posts-container worker-section'>
        <h2 className="current-partners-title workers-title">현재 파트너를 찾고 있는 작업자 분들</h2>
        <RecruitmentPostList
          skills={selectedSkills}
          videoTypes={selectedVideoTypes}
          tagNames={['구직']}
          sliderSettings={settings}
            variant="main"
        />
      </div>
    </div>
  );
};

export default PostsPage;