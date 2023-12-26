import { useContext, createContext, useState, useEffect } from "react";

const LocalContext = createContext();

export const LocalContextProvider = ({ children }) => {
  const [exampleToken, setExampleToken] = useState();

  return (
    <LocalContext.Provider
      value={{
        exampleToken,
        setExampleToken,
      }}
    >
      {children}
    </LocalContext.Provider>
  );
};

export const ExampleContext = () => {
  return useContext(LocalContext);
};
