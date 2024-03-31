"use client";
import { useParams } from "next/navigation";

export default function PagePublicEvent() {
  let { eventId } = useParams();

  return <>Hello test page {eventId}</>;
}
