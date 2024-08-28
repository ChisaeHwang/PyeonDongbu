import React, { useState, useEffect } from 'react';
import Banner from '../components/Banner';
import RecruitmentPostList from '../components/RecruitmentPostList';
import FilterButtonGroup from '../components/FilterButtonGroup';
import '../styles/PostsPage.css';

import premierProIcon from '../assets/premierProIcon';
import photoshopIcon from '../assets/photoshopIcon';
import afterEffectsIcon from '../assets/afterEffectsIcon';
import illustratorIcon from '../assets/illustratorIcon';
import chatIcon from '../assets/chatIcon';
import radioIcon from '../assets/radioIcon';
import gameIcon from '../assets/gameIcon';
import foodIcon from '../assets/foodIcon';
import educationIcon from '../assets/educationIcon';
import reviewIcon from '../assets/reviewIcon';

type FilterOption = {
    name: string;
    icon: React.ComponentType; 
    type: 'skill' | 'videoType';
};

const filterOptions: FilterOption[] = [
  { name: '프리미어 프로', icon: premierProIcon, type: 'skill' },
  { name: '포토샵', icon: photoshopIcon, type: 'skill' },
  { name: '에프터 이펙트', icon: afterEffectsIcon, type: 'skill' },
  { name: '어도비 일러스트', icon: illustratorIcon, type: 'skill' },
  { name: '저스트 채팅', icon: chatIcon, type: 'videoType' },
  { name: '보이는 라디오', icon: radioIcon, type: 'videoType' },
  { name: '게임', icon: gameIcon, type: 'videoType' },
  { name: '먹방', icon: foodIcon, type: 'videoType' },
  { name: '교육/강의', icon: educationIcon, type: 'videoType' },
  { name: '리뷰/정보', icon: reviewIcon, type: 'videoType' },
];

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

  return (
    <div className="posts-page">
      <Banner />
      <div className='posts-container youtuber-section'>
        <h2 className="current-partners-title">현재 파트너를 찾고 있는 유튜버 분들</h2>
        <FilterButtonGroup
          options={filterOptions}
          selectedOptions={[...selectedSkills, ...selectedVideoTypes]}
          onOptionToggle={handleOptionToggle}
        />
        <RecruitmentPostList
          skills={selectedSkills}
          videoTypes={selectedVideoTypes}
          tagNames={['크롤링', '구인']}
        />
      </div>
      <div className='posts-container worker-section'>
        <h2 className="current-partners-title workers-title">현재 파트너를 찾고 있는 작업자 분들</h2>
        <RecruitmentPostList
          skills={selectedSkills}
          videoTypes={selectedVideoTypes}
          tagNames={['구직']}
        />
      </div>
    </div>
  );
};

export default PostsPage;