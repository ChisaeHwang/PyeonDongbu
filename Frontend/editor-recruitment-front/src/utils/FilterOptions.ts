import React from 'react';
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

export type FilterOption = {
    name: string;
    icon: React.ComponentType; 
    type: 'skill' | 'videoType';
};

export const FilterOptions: FilterOption[] = [
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