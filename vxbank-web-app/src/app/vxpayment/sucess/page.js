"use client";
import { Flex, Heading, Input,Button, useColorModeValue } from "@chakra-ui/react";

export default function SuccessPage({ searchParams }) {
  let { stripeSessionId, projectId, clubId } = searchParams;

  console.log(stripeSessionId);
  console.log(projectId);
  console.log(clubId);
  //console.log(router.query);

  const formBackground = useColorModeValue("gray.100", "gray.700");

  return (
    <Flex height="100vh" alignItems="center" justifyContent="center">
      <Flex direction="column" background={formBackground} p={12} rounded={6}>
        <Heading mb={6}>Sucess</Heading>
        <Input
          placeholder="bogdan.oloeriu@gmail.com"
          variant="filled"
          mb={3}
          type="email"
        />
        <Input
          placeholder="**********"
          variant="filled"
          mb={6}
          type="password"
        />
        <Button colorScheme="teal" mb={6}>
          Log in
        </Button>
      </Flex>
    </Flex>
  );
}
