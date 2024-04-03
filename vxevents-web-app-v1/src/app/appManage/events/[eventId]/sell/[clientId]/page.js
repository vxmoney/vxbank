"use client";
import { useParams } from "next/navigation";

export default function SellManagePage() {
  let { eventId, clientId } = useParams();

  return (
    <>
      <div>Hello sell page</div>
      <div>Event ID: {eventId}</div>
      <div>Client ID: {clientId}</div>
    </>
  );
}
