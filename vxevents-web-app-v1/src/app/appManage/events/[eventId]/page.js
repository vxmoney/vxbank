"use client";
import { useParams } from "next/navigation";

import Link from "next/link";
import PublicEventManageDetailsComponent from "./PublicEventManageDetailsComponent";
import PublicEventManageManagersComponent from "./PublicEventManageManagersComponent";

export default function PublicEventMangePage() {
  let { eventId } = useParams();

  return (
    <>
      <PublicEventManageDetailsComponent />
      <PublicEventManageManagersComponent />
    </>
  );
}
