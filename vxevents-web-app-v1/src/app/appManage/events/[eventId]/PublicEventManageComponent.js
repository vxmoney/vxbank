"use client";
import { useParams } from "next/navigation";

export default function PublicEventManageComponent() {
  const { eventId } = useParams();

  return (
    <div>
      <h1>Event ID: {eventId}</h1>
    </div>
  );
}
