export default function SuccessPage({ searchParams }) {
  let { stripeSessionId, projectId, clubId } = searchParams;
  
  console.log(stripeSessionId);
  console.log(projectId);
  console.log(clubId);
  //console.log(router.query);

  return <p>Hello vxpayment/success/page page</p>;
}
