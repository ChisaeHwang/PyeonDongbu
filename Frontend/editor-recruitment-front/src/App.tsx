import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MainPage from './pages/MainPage';
import CreatePostPage from './pages/recruitment/CreatePostPage';
import MyProfilePage from './pages/MyProfilePage';
import PostsPage from './pages/PostsPage';
import JobsPage from './pages/recruitment/JobsPage';
import WorkersPage from './pages/recruitment/WorkersPage';
import CommunityPage from './pages/community/CommunityPage';
import CommunityPostDetailPage from './pages/community/CommunityPostDetailPage';
import CreateCommunityPostPage from './pages/community/CreateCommunityPostPage'; // 새로 추가
import GoogleAuthRedirectHandler from './components/GoogleAuthRedirectHandler';
import PartnerMatchingPage from './pages/PartnerMatchingPage';
import PostDetailPage from './pages/recruitment/PostDetailPage';
import MyPostsPage from './pages/MyPostsPage';
import './App.css';

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<MainPage />}>
                    <Route index element={<PostsPage />} />
                    <Route path="/login/oauth2/code/google" element={<GoogleAuthRedirectHandler />} />
                    <Route path="posts" element={<CreatePostPage />} />
                    <Route path="mypage" element={<MyProfilePage />} />
                    <Route path="jobs" element={<JobsPage />} />
                    <Route path="workers" element={<WorkersPage />} />
                    <Route path="community" element={<CommunityPage />} />
                    <Route path="/community/post/:postId" element={<CommunityPostDetailPage />} />
                    <Route path="/community/create" element={<CreateCommunityPostPage />} />
                    <Route path="match" element={<PartnerMatchingPage />} />
                    <Route path="myposts" element={<MyPostsPage />} />
                    <Route path="/post/:postId" element={<PostDetailPage />} />
                </Route>
            </Routes>
        </Router>
    );
};

export default App;