import VxTopNav from "../ui/vxpayment/vxtopnav";
export default function Layout({ children }) {
  return (
    <div>
      <VxTopNav />
      <div>{children}</div>
    </div>
  );
}
