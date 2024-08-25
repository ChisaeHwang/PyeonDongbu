import React from 'react';
import '../styles/Banner.css'; // 배너 스타일 파일을 만들어야 합니다

const Banner: React.FC = () => {
    return (
        <div className="banner">
            <img src={"https://ifh.cc/g/8lVGGo.jpg"} alt="배너 이미지" className="banner-image" />
        </div>
    );
};

export default Banner;