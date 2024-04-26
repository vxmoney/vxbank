import styled from "styled-components";

const Wrapper = styled.div`
  border: 1px solid white;
  padding: 5px 10px;
  border-radius: 5px;
  display: flex;
  flex-flow: row;
  column-gap: 50px;
`;

const ProductField = styled.div``;

const ProductCard = ({ product }) => {
  return (
    <Wrapper>
      <ProductField>{product.title}</ProductField>
      <ProductField>{product.description}</ProductField>
      <ProductField>€ {product.price / 100}</ProductField>
      <ProductField>{product.availability}</ProductField>
    </Wrapper>
  );
};

export default ProductCard;
