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

    /**
     * if addItems is false locate the first item in list where item.id matches the selectedItem.id and remove it from the list.
     * if no item is found, do nothing
     */
    if (addItems === false) {
      const index = sellItemList.findIndex((i) => i.id === item.id);
      if (index !== -1) {
        const updatedList = [...sellItemList];
        updatedList.splice(index, 1);
        setSellItemList(updatedList);
      }
    }
  };

  // add method that resets the selected items
  const resetSelectedItems = () => {
    setSellItemList([]);
  };

  const value = {
    sellItemList,
    processItem,
    addItems,
    setAddItems,
    resetSelectedItems
  };

  return <SellContext.Provider value={value}>{children}</SellContext.Provider>;
};
