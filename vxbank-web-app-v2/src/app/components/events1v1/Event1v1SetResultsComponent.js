import { UserAuth } from "../../context/AuthContext";
import { eventResultsAPI } from "@/api/eventResultEndpoint";

export default function Event1v1SetResultsComponent({
  eventId,
  userId,
  fetchResults,
  resultsData,
}) {
  const { vxUserInfo } = UserAuth();

  const handelSetResults = async () => {
    try {
     const eventResultCreateParams = {
        eventId: eventId,
        userId: userId,
      };

      const response = await eventResultsAPI.create(
        vxUserInfo?.vxToken,
        eventResultCreateParams
      );
      fetchResults();
    } catch (error) {
      console.error("Error setting results:", error);
    }
  };

  return (
    <div className="pt-2">
      <button
        className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
        onClick={handelSetResults}
      >
        Mark as winner
      </button>
    </div>
  );
}
