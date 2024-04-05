"use client";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { UserAuth } from "@/app/context/AuthContext";
import { useVxContext } from "@/app/context/VxContext";
import styled from "styled-components";
import ProfileCard from "./components/ProfileCard";
import FundsCard from "./components/FundsCard";

import { publicEventAPI } from "@/api/publicEvent";
import { publicEventClientPayment } from "@/api/publicEventClientPayment";

const Wrapper = styled.div`
  display: flex;
  flex-flow: column;
  align-items: center;
  padding: 20px;
  row-gap: 50px;
`;

const Divider = styled.div`
  height: 1px;
  width: 50%;
  background: white;
`;

export default function AccountPage() {
  const { vxUserInfo } = UserAuth();
  let { eventId } = useParams();

  const [joinedEventData, setJoinedEventData] = useState();
  const [clientReport, setClientReport] = useState();

  useEffect(() => {
    if (vxUserInfo) {
      publicEventAPI.join(vxUserInfo.vxToken, eventId).then((result) => {
        setJoinedEventData(result.data);
        const clientId = result.data.id;
        publicEventClientPayment
          .getClientReport(vxUserInfo.vxToken, eventId, clientId)
          .then((res) => {
            console.log(res);
            setClientReport(res.data);
          });
      });
    }
  }, [vxUserInfo]);

  return (
    <Wrapper>
      <ProfileCard vxUserInfo={vxUserInfo} />
      <Divider />
      <FundsCard
        vxUserInfo={vxUserInfo}
        eventId={eventId}
        clientReport={clientReport}
      />
    </Wrapper>
  );
}
