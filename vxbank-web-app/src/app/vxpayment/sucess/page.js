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
} from "@chakra-ui/react";

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
        <Card>
          <CardHeader>
            <Heading size="md">Sucess report</Heading>
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
            </Stack>
          </CardBody>
        </Card>
      </Flex>
    </Flex>
  );
}
