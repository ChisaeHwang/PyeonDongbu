import React from 'react';
import '../styles/FilterButtonGroup.css';

type FilterOption = {
  name: string;
  icon: React.ComponentType;
  type: 'skill' | 'videoType';
};

interface FilterButtonGroupProps {
  options: FilterOption[];
  selectedOptions: string[];
  onOptionToggle: (name: string, type: 'skill' | 'videoType') => void;
}

const FilterButtonGroup: React.FC<FilterButtonGroupProps> = ({ options, selectedOptions, onOptionToggle }) => {
  return (
    <div className="filter-button-group">
      {options.map((option) => (
        <button
          key={option.name}
          className={`filter-button ${selectedOptions.includes(option.name) ? 'active' : ''}`}
          onClick={() => onOptionToggle(option.name, option.type)}
        >
          <option.icon />
          {option.name}
        </button>
      ))}
    </div>
  );
};

export default FilterButtonGroup;