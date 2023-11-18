"use client";
import Image from "next/image";

import {
  Button,
  Flex,
  Heading,
  Input,
  ColorModeScript,
  useColorMode,
  useColorModeValue,
  Box,
  Stack,
  Divider,
} from "@chakra-ui/react";
import Link from "next/link";
import { MoonIcon, SunIcon } from "@chakra-ui/icons";
import theme from "./theme";

// this is the top page
export default function Page() {
  const { colorMode, toggleColorMode } = useColorMode();
  const barBackground = useColorModeValue("gray.100", "gray.900");

  return (
    <main className="p-4">
      <h1>Home page</h1>
    </main>
  );
}
