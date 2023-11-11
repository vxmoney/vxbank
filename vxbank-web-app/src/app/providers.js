// app/providers.tsx
"use client";

import { CacheProvider } from "@chakra-ui/next-js";
import { ChakraProvider } from "@chakra-ui/react";
import theme from './theme' 

export function Providers({ children }) {
  return (
    <CacheProvider theme={theme}>
      <ChakraProvider>{children}</ChakraProvider>
    </CacheProvider>
  );
}
