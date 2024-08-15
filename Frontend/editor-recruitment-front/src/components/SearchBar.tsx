import React, { useState } from 'react';
import '../styles/SearchBar.css'; // 스타일 임포트

const SearchBar = ({ onSearch }: { onSearch: (query: string) => void }) => {
    const [searchQuery, setSearchQuery] = useState('');

    const handleSearch = () => {
        onSearch(searchQuery);
    };

    return (
        <div className="search-bar">
            <input
                type="text"
                className="search-input"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="검색어를 입력하세요..."
            />
            <button className="search-button" onClick={handleSearch}>
                검색
            </button>
        </div>
    );
};

export default SearchBar;
