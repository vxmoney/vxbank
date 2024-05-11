"use client";
import { AuthContextProvider } from "../context/AuthContext";
import { VxProvider } from "../context/VxContext";
import { MyThemeProvider } from "../context/MyThemeContext";
import { SellProvider } from "../context/SellContext";

export default function GlobalLayout({ children }) {
  return (
    <AuthContextProvider>
      <VxProvider>
        <SellProvider>
          <MyThemeProvider>{children}</MyThemeProvider>
        </SellProvider>
      </VxProvider>
    </AuthContextProvider>
  );
}
