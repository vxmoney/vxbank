"use client";
import { useParams } from "next/navigation";

import Link from "next/link";
import LeagueOfLegends1v1LayoutComponent from "@/app/components/leagueOfLegends/LequeOfLegends1v1LayoutComponent";
import PublicEventManageComponent from "./PublicEventManageComponent";

export default function PublicEventMangePage() {
  let { eventId } = useParams();

  return <PublicEventManageComponent />;
}
