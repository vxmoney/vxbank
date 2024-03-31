"use client";

import { UserAuth } from "@/app/context/AuthContext";

export default function ManageProfile() {
  const { vxUserInfo } = UserAuth();

  if (!vxUserInfo) {
    return null;
  }

  return (
    <div>
      Hello profile page
      
    </div>
  );
}
