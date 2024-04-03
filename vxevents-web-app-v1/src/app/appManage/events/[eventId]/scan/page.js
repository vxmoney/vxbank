"use client";
import { useParams } from "next/navigation";
import ScanManagerComponent from "./ScanManagerComponent";

export default function ScanManagePage() {
  let { eventId } = useParams();

  return (
    <>
      <ScanManagerComponent />
    </>
  );
}
