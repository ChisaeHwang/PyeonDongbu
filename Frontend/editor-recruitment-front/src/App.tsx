import React, { Suspense, lazy } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
import MainPage from './pages/MainPage';
import CreatePostPage from './pages/recruitment/CreatePostPage';
import MyProfilePage from './pages/MyProfilePage';
import PostsPage from './pages/PostsPage';
import JobsPage from './pages/recruitment/JobsPage';
import WorkersPage from './pages/recruitment/WorkersPage';
import CommunityPage from './pages/community/CommunityPage';
import CommunityPostDetailPage from './pages/community/CommunityPostDetailPage';
import CreateCommunityPostPage from './pages/community/CreateCommunityPostPage';
import GoogleAuthRedirectHandler from './components/GoogleAuthRedirectHandler';
import PartnerMatchingPage from './pages/PartnerMatchingPage';
import PostDetailPage from './pages/recruitment/PostDetailPage';
import MyPostsPage from './pages/MyPostsPage';
import EditPostPage from './pages/recruitment/EditPostPage';
import './App.css';

const ToastContainer = lazy(() => import('react-toastify').then(module => ({ default: module.ToastContainer })));

const App = () => {
    return (
        <Router>
            <Suspense fallback={<div>Loading...</div>}>
                <ToastContainer position="top-right" autoClose={3000} hideProgressBar={false} newestOnTop={false} closeOnClick rtl={false} pauseOnFocusLoss draggable pauseOnHover />
            </Suspense>
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
                    <Route path="/post/edit/:postId" element={<EditPostPage />} />
                </Route>
            </Routes>
        </Router>
    );
};

export default App;