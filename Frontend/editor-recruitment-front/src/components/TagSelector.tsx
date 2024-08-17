import React from 'react';

interface TagSelectorProps {
    selectedTags: string[];
    onSelect: (tags: string[]) => void;
}

const allTags = ['구인', '구직', '기타']; // 가능한 태그 목록

const TagSelector: React.FC<TagSelectorProps> = ({ selectedTags, onSelect }) => {
    const handleTagClick = (tag: string) => {
        const newTags = selectedTags.includes(tag)
            ? selectedTags.filter(t => t !== tag)
            : [...selectedTags, tag];
        onSelect(newTags);
    };

    return (
        <div className="tag-selector">
            {allTags.map(tag => (
                <button
                    key={tag}
                    className={`tag-button ${selectedTags.includes(tag) ? 'selected' : ''}`}
                    onClick={() => handleTagClick(tag)}
                >
                    {tag}
                </button>
            ))}
        </div>
    );
};

export default TagSelector;
