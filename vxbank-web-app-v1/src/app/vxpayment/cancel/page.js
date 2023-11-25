"use client";
import {
  Flex,
  Heading,
  Stack,
  Input,
  Button,
  StackDivider,
  useColorModeValue,
  Card,
  CardHeader,
  CardBody,
  Box,
  Text,
  Badge,
  Code,
  Center,
  Link,
} from "@chakra-ui/react";

export default function CancelPage({ searchParams }) {
  let { stripeSessionId, projectId, clubId, curencyId, sessionValue } =
    searchParams;

  const formBackground = useColorModeValue("gray.100", "gray.700");

  return (
    <Flex height="100vh" alignItems="center" justifyContent="center">
      <Flex direction="column" background={formBackground} p={12} rounded={6}>
        <Card>
          <CardHeader>
            <Heading size="md">
              <Badge colorScheme="red">Session canceled</Badge>
            </Heading>
          </CardHeader>
          <CardBody>
            <Stack divider={<StackDivider />} spacing={4}>
              <Box>
                <Heading size="xs" textTransform="uppercase">
                  Summary
                </Heading>
                <Text pt="2" fontSize="sm">
                  Congratulations! You have succesfuly completed the payment
                  flow.
                </Text>
              </Box>
              <Box>
                <Text fontSize="xl" fontWeight="bold">
                  stripeSessionId:
                  <Code ml={5}>{stripeSessionId}</Code>
                </Text>
              </Box>
              <Box>
                <Text fontSize="xl" fontWeight="bold">
                  projectId:
                  <Code ml={5}>{projectId}</Code>
                </Text>
              </Box>

              <Box>
                <Text fontSize="xl" fontWeight="bold">
                  clubId:
                  <Code ml={5}>{clubId}</Code>
                </Text>
              </Box>

              <Box>
                <Text fontSize="xl" fontWeight="bold">
                  curencyId:
                  <Code ml={5}>{curencyId}</Code>
                </Text>
              </Box>

              <Box>
                <Text fontSize="xl" fontWeight="bold">
                  sessionValue:
                  <Code ml={5}>{sessionValue}</Code>
                </Text>
              </Box>
              <Center>
                <Box alignItems={"center"}>
                  <Link href="/">
                    <Button>Ok</Button>
                  </Link>
                </Box>
              </Center>
            </Stack>
          </CardBody>
        </Card>
      </Flex>
    </Flex>
  );
}
