"use client";

import { UserAuth } from "../context/AuthContext";
import ProfileComponent from "../components/ProfileComponent";

export default function Profiler() {
  const { vxUserInfo } = UserAuth();

  if (!vxUserInfo) {
    return null;
  }

  return (
    <div>
      <ProfileComponent {...vxUserInfo} />
    </div>
  );
}
