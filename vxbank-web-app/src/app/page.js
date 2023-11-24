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
  Text,
} from "@chakra-ui/react";
import Link from "next/link";
import { MoonIcon, SunIcon } from "@chakra-ui/icons";
import theme from "./theme";
import { UserAuth } from "./context/AuthContext";

// this is the top page
export default function Page() {
  const { colorMode, toggleColorMode } = useColorMode();
  const barBackground = useColorModeValue("gray.100", "gray.900");
  const { user, googleSignIng, logOut  } = UserAuth();

  const handleSignIn = async () =>{
    try {
      await googleSignIng()
    } catch (error) {
      console.log(error);
    }
  }

  const handleSignOut = async () =>{
    try{
      await logOut();
    }catch (error){
      console.log(error)
    }
  }



  console.log(user);

  return (
    <>
      <ColorModeScript initialColorMode={theme.config.initialColorMode} />
      <Box background={barBackground} px={4}>
        <Flex h={16} alignItems={"center"} justifyContent={"space-between"}>
          <Box>Main home page</Box>
          <Flex alignItems={"center"}>
            <Stack direction={"row"} spacing={7}></Stack>
            <Link
              href={{
                pathname: "/vxpayment/sucess",
                query: {
                  stripeSessionId: "testSessionId",
                  projectId: "chessoutId",
                  clubId: "leuvenId",
                  curencyId: "eur",
                  sessionValue: 2500,
                },
              }}
            >
              Success
            </Link>
            <Divider mr={2} />

            <Link
              href={{
                pathname: "/vxpayment/cancel",
                query: {
                  stripeSessionId: "testSessionId",
                  projectId: "chessoutId",
                  clubId: "leuvenId",
                  curencyId: "eur",
                  sessionValue: 2500,
                },
              }}
            >
              Cancel
            </Link>
            <Divider mr={2} />
            <Box>
              <Button onClick={handleSignIn}>Login</Button>
            </Box>
            
            <Divider mr={2} />
            <Box>
              <Button onClick={handleSignOut}>Logout</Button>
            </Box>
               <Divider mr={2} />
            <Button onClick={toggleColorMode}>
              {colorMode === "light" ? <MoonIcon /> : <SunIcon />}
            </Button>
          </Flex>
        </Flex>
      </Box>
    </>
  );
}
