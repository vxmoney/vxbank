"use client";
import Image from "next/image";
import { UserAuth } from "../context/AuthContext";
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
  Spacer,
  HStack,
  Text,
} from "@chakra-ui/react";
import { MoonIcon, SunIcon } from "@chakra-ui/icons";
import theme from "@/app/theme";
import Link from "next/link";

// this is the top page
export default function Navbar() {
  const { colorMode, toggleColorMode } = useColorMode();
  const barBackground = useColorModeValue("gray.100", "gray.900");

  const { user, googleSignIn, logout } = UserAuth();

  const handleSignIn = async () => {
    try {
      await googleSignIn();
    } catch (error) {
      console.log(error);
    }
  };

  console.log(user);

  return (
    <>
      <ColorModeScript initialColorMode={theme.config.initialColorMode} />
      <Box background={barBackground} px={4}>
        <Flex h={16} alignItems={"center"} justifyContent={"space-between"}>
          <Box>
            <HStack
              as={"nav"}
              spacing={4}
              display={{ base: "none", md: "flex" }}
            >
              <Link
                href={{
                  pathname: "/",
                }}
              >
                Home
              </Link>
              <Link
                href={{
                  pathname: "/about",
                }}
              >
                About
              </Link>
              <Link
                href={{
                  pathname: "/profile",
                }}
              >
                profile
              </Link>
            </HStack>
          </Box>
          <Flex alignItems={"center"}>
            <Stack direction={"row"} spacing={7}></Stack>
            <HStack
              as={"nav"}
              spacing={4}
              display={{ base: "none", md: "flex" }}
            >
              <Text onClick={handleSignIn}>Login</Text>
              <Text>Logout</Text>
              <Button onClick={toggleColorMode}>
                {colorMode === "light" ? <MoonIcon /> : <SunIcon />}
              </Button>
            </HStack>
          </Flex>
        </Flex>
      </Box>
    </>
  );
}
