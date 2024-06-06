import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "react-query";
import { ThemeProvider } from "styled-components";
import { GlobalStyle, theme } from "./App.styles";
import HomePage from "./pages/HomePage";
import GoogleAuthRedirectHandler from "./auth/GoogleAuthRedirectHandler";
import GoogleAuthRefreshToken from "./auth/GoogleAuthRefreshToken";
import CreatePost from "./pages/Blog/CreatePost";
import Header from "./components/Header";
import AccessTokenProvider from "./providers/AccessTokenProvider";

const queryClient = new QueryClient();

const App = (): JSX.Element => {
  return (
    <QueryClientProvider client={queryClient}>
      <AccessTokenProvider>
        <ThemeProvider theme={theme}>
          <GlobalStyle />
          <Router>
            <Header />
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login/oauth2/code/google" element={<GoogleAuthRedirectHandler />} />
              <Route path="/home" element={<GoogleAuthRefreshToken />} />
              <Route path="/create-post" element={<CreatePost />} />
            </Routes>
          </Router>
        </ThemeProvider>
      </AccessTokenProvider>
    </QueryClientProvider>
  );
};

export default App;
