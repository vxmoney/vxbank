"use client";

import { FiSun, FiMoon } from "react-icons/fi";
import { useState, useEffect } from "react";
import { useTheme } from "next-themes";
import Image from "next/image";

export default function ThemeSwitch() {
  const [mounted, setMounted] = useState(false);
  const { setTheme, resolvedTheme } = useTheme();

  useEffect(() => setMounted(true), []);

  if (!mounted) return null;

  if (resolvedTheme === "dark") {
    return (
      <li  className="mt-3">
        <FiSun onClick={() => setTheme("light")} />
      </li>
    );
  }

  if (resolvedTheme === "light") {
    return (
      <li className="mt-3">
        <FiMoon onClick={() => setTheme("dark")} />
      </li>
    );
  }
}
