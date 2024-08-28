import React from 'react';

interface FilterButtonProps {
  name: string;
  icon: string;
  isSelected: boolean;
  onClick: () => void;
  type: 'skill' | 'videoType';
}

const FilterButton: React.FC<FilterButtonProps> = ({ name, icon, isSelected, onClick, type }) => {
  return (
    <button 
      className={`filter-button ${isSelected ? 'selected' : ''} ${type}`} 
      onClick={onClick}
    >
      <img src={icon} alt={`${name} 아이콘`} className="filter-button-icon" />
      <span className="filter-button-text">{name}</span>
    </button>
  );
};

export default FilterButton;