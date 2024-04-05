"use client";
import styled from "styled-components";
import AddFundsMOdal from "./AddFundsModal";
import { useState } from "react";

const Wrapper = styled.div`
  display: flex;
  flex-flow: column;
  align-items: center;
  margin-top: 15px;
`;

const Title = styled.h1`
  font-size: 1.5rem;
`;

const Description = styled.h1`
  font-size: 1rem;
  margin-bottom: 15px;
`;

const AddFundsButton = styled.div`
  padding: 10px 20px;
  cursor: pointer;
  user-select: none;
  border: 2px solid white;
  border-radius: 5px;
`;

const FundsCard = ({ vxUserInfo, eventId, clientReport }) => {
  const [addFundsModalOpened, setAddFundsModalOpened] = useState(false);

  const content = (
    <AddFundsButton
      onClick={() => {
        setAddFundsModalOpened(true);
      }}
    >{`Add funds -> `}</AddFundsButton>
  );

  return (
    <>
      <Wrapper>
        <Title>Funds</Title>
        <Description>{`Available balance: €${
          clientReport?.availableBalance / 100.0 ?? 0
        }`}</Description>
        {content}
      </Wrapper>
      {addFundsModalOpened && (
        <AddFundsMOdal
          onClose={() => {
            setAddFundsModalOpened(false);
          }}
          vxUserInfo={vxUserInfo}
          eventId={eventId}
        />
      )}
    </>
  );
};

export default FundsCard;
