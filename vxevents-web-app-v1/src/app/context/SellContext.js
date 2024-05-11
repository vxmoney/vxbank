import React, { useState, useEffect, useContext, createContext } from "react";

const SellContext = createContext();

export const useSellContext = () => {
  return useContext(SellContext);
};

export const SellProvider = ({ children }) => {
  const [sellItemList, setSellItemList] = useState(() => {
    if (typeof localStorage !== "undefined") {
      const storedItemList = localStorage.getItem("itemList");
      return storedItemList || [];
    } else {
      return [];
    }
  });

  // create method appendItem that will append an item to the itemList
  const appendSellItem = (item) => {
    setSellItemList([...sellItemList, item]);
  };

  const value = {
    sellItemList,
    appendSellItem
  };

  return <SellContext.Provider value={value}>{children}</SellContext.Provider>;
};
