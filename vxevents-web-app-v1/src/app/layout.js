
import { Inter } from "next/font/google";
import GlobalLayout from "./components/GlobalLayout";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata = {
  title: "Create Next App",
  description: "Generated by create next app",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={`${inter.className} bg-light dark:bg-gray-900`}>
        <GlobalLayout>{children}</GlobalLayout>
      </body>
    </html>
  );
}
