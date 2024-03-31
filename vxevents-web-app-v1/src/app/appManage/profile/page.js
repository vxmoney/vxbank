"use client";

import { UserAuth } from "@/app/context/AuthContext";
import ProfileComponent from "./ProfileComponent";
import StripeActivateComponent from "./StripeActivateComponent";
import StripeActivateDifferentCurrenciesComponent from "./StripeActivateDifferentCurrenciesComponent";

export default function ProfilePage() {
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
