"use client";
import { useState } from "react";
import styled from "styled-components";

import { publicEventAPI } from "@/api/publicEvent";

const Background = styled.div`
  position: fixed;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.75);
  top: 0;
`;

const MODALHEIGHT = 250;
const MODALWIDTH = 500;

const Modal = styled.div`
  position: fixed;
  width: ${MODALWIDTH}px;
  height: auto;
  background: #1e1e1e;
  border-radius: 10px;
  left: calc(50% - ${MODALWIDTH / 2.0}px);
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-flow: column;
  align-items: center;
  padding: 20px;
`;

const Title = styled.h1`
  font-size: 1.5rem;
  margin-bottom: 15px;
`;

const CloseButton = styled.div`
  position: absolute;
  top: 15px;
  right: 15px;
  user-select: none;
  cursor: pointer;
`;

const Label = styled.div`
  font-size: 1rem;
  margin-bottom: 5px;
`;

const StyledInput = styled.input`
  padding: 5px;
`;

const AddFundsButton = styled.div`
  padding: 5px 20px;
  cursor: pointer;
  user-select: none;
  border: 2px solid white;
  margin-top: 15px;
  border-radius: 5px;
`;

const AddFundsMOdal = ({ onClose, vxUserInfo, eventId }) => {
  const [fundsToAdd, setFundsToAdd] = useState(0);

  const onAddFunds = () => {
    // Value entered by user is in Euros. Need to convert to cents:
    const valueInCents = fundsToAdd * 100;
    publicEventAPI
      .clientDepositFunds(vxUserInfo?.vxToken, eventId, valueInCents)
      .then((response) => {
        console.log("funds added:", response.data);
        if (response.data.stripeSessionPaymentUrl) {
          window.open(response.data.stripeSessionPaymentUrl, "_self");
        }
      })
      .catch((error) => {
        console.error("Error creating event:", error);
        // Handle error
      });
  };
  return (
    <>
      <Background onClick={onClose} />
      <Modal>
        <CloseButton onClick={onClose}> X</CloseButton>
        <Title>Add funds</Title>
        <Label>Enter amount of funds you want to add</Label>
        <StyledInput
          type="number"
          placeholder="Enter amount"
          required
          id="fundsAmount"
          value={fundsToAdd}
          onChange={(e) => {
            const { value } = e.target;
            setFundsToAdd(value);
          }}
        />
        <AddFundsButton onClick={onAddFunds}>ADD</AddFundsButton>
      </Modal>
    </>
  );
};

export default AddFundsMOdal;
