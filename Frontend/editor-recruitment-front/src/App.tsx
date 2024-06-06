import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import GoogleAuthRedirectHandler from "./auth/GoogleAuthRedirectHandler";
import HomePage from "./home/HomePage";
import GoogleAuthRefreshToken from "./auth/GoogleAuthRefreshToken"; // Import 추가

function App() {
  return (
    <Router>
      <Routes>
        {/* 라우트 설정 */}
        <Route path="/" element={<HomePage />} />
        <Route
          path="/login/oauth2/code/google"
          element={<GoogleAuthRedirectHandler />}
        />
        <Route path="/home" element={<GoogleAuthRefreshToken />} />
        {/* 기타 라우트 */}
      </Routes>
    </Router>
  );
}

export default App;
