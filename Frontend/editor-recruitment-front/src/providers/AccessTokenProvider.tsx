import React, { createContext, useState, ReactNode } from 'react';

interface AccessTokenContextProps {
  accessToken: string | null;
  setAccessToken: (token: string) => void;
}

export const AccessTokenContext = createContext<AccessTokenContextProps | undefined>(undefined);

const AccessTokenProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [accessToken, setAccessToken] = useState<string | null>(null);

  return (
    <AccessTokenContext.Provider value={{ accessToken, setAccessToken }}>
      {children}
    </AccessTokenContext.Provider>
  );
};

export default AccessTokenProvider;
