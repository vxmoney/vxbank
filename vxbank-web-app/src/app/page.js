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
import Link from "next/link";
import { MoonIcon, SunIcon } from "@chakra-ui/icons";
import theme from "./theme";

// this is the top page
export default function Page() {
  const { colorMode, toggleColorMode } = useColorMode();
  const barBackground = useColorModeValue("gray.100", "gray.900");

  return (
    <>
      <ColorModeScript initialColorMode={theme.config.initialColorMode} />
      <Box background={barBackground} px={4}>
        <Flex h={16} alignItems={"center"} justifyContent={"space-between"}>
          <Box>Main home page</Box>
          <Flex alignItems={"center"}>
            <Stack direction={"row"} spacing={7}></Stack>
            <Link href='/vxpayment/sucess'>Success</Link>
            <Button onClick={toggleColorMode}>
              {colorMode === "light" ? <MoonIcon /> : <SunIcon />}
            </Button>
          </Flex>
        </Flex>
      </Box>
    </>
  );
}
