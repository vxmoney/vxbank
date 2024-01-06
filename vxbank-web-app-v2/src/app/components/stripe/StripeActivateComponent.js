import configValues from "../../../api/apiConfig";
const { frontendPort, frontendBaseUrl, frontendProtocol } = configValues;

const StripeActivateComponent = ({
  id,
  email,
  name,
  stripeConfigState,
  stripeId,
}) => {
  return (
    <div className="pl-8 pr-8">
      <div className="block  p-6 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 ">
        <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
          Stripe activate card
        </h5>
        <p className="font-normal text-gray-700 dark:text-gray-400k">
          You need to update you stripe configuration
        </p>

        <div className="flex-grow overflow-auto">
          You will be redirected to vxbank stripe configuration page in order to
          update your configuration
        </div>
      </div>
    </div>
  );
};
export default StripeActivateComponent;
