"use client";
import { useParams } from "next/navigation";

export default function UsageExampleId() {
  let { id } = useParams();
  return (
    <div>
      <h1>Hello id = {id}</h1>
    </div>
  );
}
