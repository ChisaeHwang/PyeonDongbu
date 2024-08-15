import React from 'react';
import '../styles/CategoryTabs.css';

const categories = ['구인', '구직', '커뮤니티'];

const CategoryTabs = ({ onSelect, selectedCategory }: { onSelect: (category: string) => void; selectedCategory: string }) => {
    return (
        <div className="category-tabs">
            {categories.map((category) => (
                <button
                    key={category}
                    className={`category-tab ${selectedCategory === category ? 'active' : ''}`}
                    onClick={() => onSelect(category)}
                >
                    {category}
                </button>
            ))}
        </div>
    );
};

export default CategoryTabs;
