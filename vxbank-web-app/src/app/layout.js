import { Providers } from "./providers";
import Navbar from "./components/Navbar";
import { AuthContextProvider } from "./context/AuthContext";

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <Providers>
          <AuthContextProvider>
            <Navbar />
            {children}
          </AuthContextProvider>
        </Providers>
      </body>
    </html>
  );
}
