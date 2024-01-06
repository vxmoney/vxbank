"use client";

import { UserAuth } from "../context/AuthContext";
import ProfileComponent from "../components/ProfileComponent";
import StripeActivateComponent from "../components/stripe/StripeActivateComponent";

export default function Profiler() {
  const { vxUserInfo } = UserAuth();

  if (!vxUserInfo) {
    return null;
  }

  return (
    <div>
      <ProfileComponent {...vxUserInfo} />
      {vxUserInfo.stripeConfigState !== "active" && (
        <StripeActivateComponent {...vxUserInfo} />
      )}
    </div>
  );
}
