import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MainPage from './pages/MainPage';
import CreatePostPage from './pages/CreatePostPage';
import MyProfilePage from './pages/MyProfilePage';
import PostsPage from './pages/PostsPage';
import GoogleAuthRedirectHandler from './components/GoogleAuthRedirectHandler';
import './App.css';

const App = () => {
    return (
        <Router>
            <Routes>
                 <Route path="/" element={<MainPage />}>
                    <Route index element={<PostsPage />} />
                    <Route path="/login/oauth2/code/google" element={<GoogleAuthRedirectHandler />} />
                    <Route path="posts" element={<CreatePostPage />} />
                    <Route path="profile" element={<MyProfilePage />} />
                </Route>
            </Routes>
        </Router>
    );
};

export default App;
