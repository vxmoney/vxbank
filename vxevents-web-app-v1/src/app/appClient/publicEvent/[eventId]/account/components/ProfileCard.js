"use client";
import styled from "styled-components";

const Wrapper = styled.div`
  display: flex;
  flex-flow: column;
  align-items: center;
  margin-top: 15px;
`;

const Name = styled.h1`
  font-size: 2rem;
  margin-bottom: 15px;
`;
const Email = styled.h2`
  font-size: 1.5rem;
`;
const ClientId = styled.h3`
  font-size: 1.15rem;
`;

const ProfileCard = ({ vxUserInfo }) => {
  return (
    <Wrapper>
      <Name>{vxUserInfo?.name}</Name>
      <Email>{vxUserInfo?.email}</Email>
      <ClientId>{vxUserInfo?.id}</ClientId>
    </Wrapper>
  );
};

export default ProfileCard;
