import { publicEventProducts } from "@/api/publicEventProducts";
import { useEffect, useState } from "react";

import styled from "styled-components";

const MODALWIDTH = 350;

const Background = styled.div`
  position: fixed;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.75);
  top: 0;
`;

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

const CloseButton = styled.div`
  position: absolute;
  top: 15px;
  right: 15px;
  user-select: none;
  cursor: pointer;
`;

const Title = styled.h1`
  font-size: 1.5rem;
  margin-bottom: 15px;
`;

const Label = styled.div`
  font-size: 1rem;
  margin-bottom: 5px;
`;

const StyledInput = styled.input`
  padding: 5px;
`;

const ConfirmButton = styled.div`
  padding: 5px 20px;
  cursor: pointer;
  user-select: none;
  border: 2px solid white;
  margin-top: 15px;
  border-radius: 5px;
`;

const VerticalWrapper = styled.div`
  display: flex;
  flex-flow: column;
  margin-bottom: 10px;
`;

const AddProductModal = ({ onClose, vxUserInfo, eventId }) => {
  const [productName, setProductName] = useState("");
  const [productDescription, setProductDescription] = useState("");
  const [productPrice, setProductPrice] = useState(0);

  const [creationInProgress, setCreationInProgress] = useState(false);

  const createProduct = () => {
    setCreationInProgress(true);
    publicEventProducts
      .create(vxUserInfo.vxToken, {
        vxPublicEventId: Number(eventId),
        title: productName,
        description: productDescription,
        price: productPrice,
        availability: "available",
      })
      .then((res) => {
        console.log(res);
        onClose();
      });
  };

  let content = "";

  if (creationInProgress) {
    content = <div>Creating product ...</div>;
  } else {
    content = (
      <>
        <Title>Add product</Title>
        <VerticalWrapper>
          <Label>Name</Label>
          <StyledInput
            type="text"
            placeholder="Enter name"
            required
            id="name"
            value={productName}
            onChange={(e) => {
              const { value } = e.target;
              setProductName(value);
            }}
          />
        </VerticalWrapper>
        <VerticalWrapper>
          <Label>Description</Label>
          <StyledInput
            type="text"
            placeholder="Enter description"
            id="description"
            value={productDescription}
            onChange={(e) => {
              const { value } = e.target;
              setProductDescription(value);
            }}
          />
        </VerticalWrapper>
        <VerticalWrapper>
          <Label>Price</Label>
          <StyledInput
            type="number"
            placeholder="Enter price"
            required
            id="name"
            value={productPrice}
            onChange={(e) => {
              const { value } = e.target;
              setProductPrice(Number(value));
            }}
          />
        </VerticalWrapper>
        <ConfirmButton
          onClick={() => {
            if (productName && productPrice) {
              console.log("create product");
              createProduct();
            }
          }}
        >
          ADD
        </ConfirmButton>
      </>
    );
  }

  return (
    <>
      <Background onClick={onClose} />
      <Modal>
        <CloseButton onClick={onClose}> X</CloseButton>
        {content}
      </Modal>
    </>
  );
};

export default AddProductModal;
