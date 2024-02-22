"use client"
import { useParams } from "next/navigation";

import Link from "next/link";
import LeagueOfLegends1v1Component from "@/app/components/LequeOfLegends1v1Component";

export default function LeagueOfLegendsGamePanel(){

    let { eventId } = useParams();

    return(
        <di>
            <h1>
                Hello panel for game id = : {eventId} 
            </h1>
            <LeagueOfLegends1v1Component />
        </di>
    )
}