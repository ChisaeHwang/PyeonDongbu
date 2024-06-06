import React from "react";

const HomePage: React.FC = () => {
  const handleLogin = () => {
    window.location.href = `http://localhost:8080/auth/google`;
  };

  return (
    <div>
      <h1>Welcome to the Home Page</h1>
      <button onClick={handleLogin}>Login with Google</button>
    </div>
  );
};

export default HomePage;
