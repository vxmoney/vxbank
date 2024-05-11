import React, { useState, useEffect, useContext, createContext } from "react";

const SellContext = createContext();

export const useSellContext = () => {
  return useContext(SellContext);
};

export const SellProvider = ({ children }) => {
  const [sellItemList, setSellItemList] = useState([]);
  const [addItems, setAddItems] = useState(true);

  // create method appendItem that will append an item to the itemList
  const appendSellItem = (item) => {
    setSellItemList([...sellItemList, item]);
  };

  const processItem = (item) => {
    // process the item
    if (addItems === true) {
      appendSellItem(item);
    }
  }

  const value = {
    sellItemList,
    processItem,
  };

  return <SellContext.Provider value={value}>{children}</SellContext.Provider>;
};
