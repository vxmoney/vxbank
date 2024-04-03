"use client";
import { useParams } from "next/navigation";


export default function ScanManagePage() {
  let { eventId } = useParams();

  return (
    <>
      Hello scan manage page {eventId}
    </>
  );
}
