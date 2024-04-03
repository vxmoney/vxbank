"use client";
import { useParams } from "next/navigation";
import ShowMyCodeComponent from "./ShowMyCode";

export default function PagePublicEvent() {
  let { eventId } = useParams();

  return (
    <>
      <div>Hello test page {eventId}</div>
      <ShowMyCodeComponent />
    </>
  );
}
