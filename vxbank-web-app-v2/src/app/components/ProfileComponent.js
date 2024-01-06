const ProfileComponent = ({ id, email, name, stripeConfigState, stripeId }) => {
  return (
    <div className="p-8 ">
  <div className="block  p-6 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 ">
    <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
      Profile component
    </h5>
    <p className="font-normal text-gray-700 dark:text-gray-400">
      Content of profile
    </p>

    <div className="flex-grow overflow-auto">
    <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
        <tbody>
          <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
            <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
              ID
            </th>
            <td className="px-6 py-4">{id}</td>
          </tr>
          <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
            <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
              Email
            </th>
            <td className="px-6 py-4">{email}</td>
          </tr>
          <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
            <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
              Name
            </th>
            <td className="px-6 py-4">{name}</td>
          </tr>
          <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
            <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
              Stripe Config State
            </th>
            <td className="px-6 py-4">{stripeConfigState}</td>
          </tr>
          <tr className="bg-white dark:bg-gray-800">
            <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
              Stripe ID
            </th>
            <td className="px-6 py-4">{stripeId}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>

  );
};
export default ProfileComponent;
