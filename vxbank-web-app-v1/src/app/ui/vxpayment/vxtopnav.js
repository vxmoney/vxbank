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
} from "@chakra-ui/react";
import { MoonIcon, SunIcon } from "@chakra-ui/icons";
import theme from "@/app/theme";
import Link from "next/link";

// this is the top page
export default function VxTopNav() {
  const { colorMode, toggleColorMode } = useColorMode();
  const barBackground = useColorModeValue("gray.100", "gray.900");

  return (
    <>
      <ColorModeScript initialColorMode={theme.config.initialColorMode} />
      <Box background={barBackground} px={4}>
        <Flex h={16} alignItems={"center"} justifyContent={"space-between"}>
          <Box>
            <Link
              href={{
                pathname: "/",
              }}
            >
              Home
            </Link>
          </Box>
          <Flex alignItems={"center"}>
            <Stack direction={"row"} spacing={7}></Stack>
            <Button onClick={toggleColorMode}>
              {colorMode === "light" ? <MoonIcon /> : <SunIcon />}
            </Button>
          </Flex>
        </Flex>
      </Box>
    </>
  );
}
