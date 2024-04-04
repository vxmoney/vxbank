"use client";
import { UserAuth } from "@/app/context/AuthContext";
import styled from "styled-components";
import ProfileCard from "./components/ProfileCard";

const Wrapper = styled.div`
  padding: 20px;
`;

export default function AccountPage() {
  const { googleSignIn, logOut, vxUserInfo, pendingLogin } = UserAuth();

  console.log(vxUserInfo);
  return (
    <Wrapper>
      <ProfileCard vxUserInfo={vxUserInfo} />
    </Wrapper>
  );
}
