import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MainPage from './pages/MainPage';
import CreatePostPage from './pages/CreatePostPage';
import GoogleAuthRedirectHandler from './components/GoogleAuthRedirectHandler';

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/create-post" element={<CreatePostPage />} />
                <Route path="/login/oauth2/code/google" element={<GoogleAuthRedirectHandler />} />
            </Routes>
        </Router>
    );
};

export default App;
