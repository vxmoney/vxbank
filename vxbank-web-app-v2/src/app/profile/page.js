"use client";

import { UserAuth } from "../context/AuthContext";
import ProfileComponent from "../components/ProfileComponent";
import StripeActivateComponent from "../components/stripe/StripeActivateComponent";
import StripeActivateDifferentCurrenciesComponent from "../components/stripe/StripeActivateDifferentCurrenciesComponent";

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

      {vxUserInfo.stripeConfigState === "active" && (
        <StripeActivateDifferentCurrenciesComponent {...vxUserInfo} />
      )}
    </div>
  );
}
