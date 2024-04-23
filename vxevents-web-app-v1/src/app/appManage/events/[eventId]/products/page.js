"use client";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { UserAuth } from "@/app/context/AuthContext";

import styled from "styled-components";
import { publicEventProducts } from "@/api/publicEventProducts";
import AddProductModal from "./components/AddProductModal";

const Wrapper = styled.div`
  display: flex;
  flex-flow: column;
  align-items: center;
`;

const Title = styled.div`
  font-size: 1.75rem;
  margin-top: 15px;
`;

const ProductList = styled.div``;

const AddProductButton = styled.div`
  user-select: none;
  cursor: pointer;
  border: 1px solid white;
  margin-top: 15px;
  padding: 5px 10px;
  border-radius: 5px;
`;

const Products = () => {
  const { vxUserInfo } = UserAuth();
  let { eventId } = useParams();

  const [addProductModalOpened, setAddProductModalOpened] = useState(false);

  useEffect(() => {
    publicEventProducts.getAll(vxUserInfo.vxToken, eventId).then((result) => {
      console.log(result);
    });
  }, [vxUserInfo, eventId]);

  return (
    <Wrapper>
      <Title>Products</Title>
      <AddProductButton
        onClick={() => {
          setAddProductModalOpened(true);
        }}
      >
        Add Product
      </AddProductButton>
      {addProductModalOpened && (
        <AddProductModal
          onClose={() => {
            setAddProductModalOpened(false);
          }}
          vxUserInfo={vxUserInfo}
          eventId={eventId}
        />
      )}
    </Wrapper>
  );
};

export default Products;
