import React from 'react';
import { Outlet } from 'react-router-dom'; // 동적으로 콘텐츠 렌더링을 위한 Outlet 사용
import Header from '../components/Header';
import '../styles/MainPage.css';

const MainPage = () => {
    return (
        <div className="main-page">
            <Header />
            <div className="content">
              
                <Outlet />
            </div>
        </div>
    );
};

export default MainPage;
