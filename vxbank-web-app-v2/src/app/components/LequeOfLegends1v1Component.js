"use client"
import { useParams } from "next/navigation";

export default function LeagueOfLegends1v1Component(){

    let { eventId } = useParams();

    return (
        <di>
            <h1>
                Hello nested id = : {eventId} 
            </h1>
        </di>
    )
}