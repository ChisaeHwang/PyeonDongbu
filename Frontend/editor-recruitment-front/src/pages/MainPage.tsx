import React from 'react';
import { Outlet } from 'react-router-dom';
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