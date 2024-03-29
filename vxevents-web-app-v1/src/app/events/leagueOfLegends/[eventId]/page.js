"use client";
import { useParams } from "next/navigation";

import Link from "next/link";
import LeagueOfLegends1v1LayoutComponent from "@/app/components/leagueOfLegends/LequeOfLegends1v1LayoutComponent";

export default function LeagueOfLegendsGamePanel() {
  let { eventId } = useParams();

  return <LeagueOfLegends1v1LayoutComponent />;
}
