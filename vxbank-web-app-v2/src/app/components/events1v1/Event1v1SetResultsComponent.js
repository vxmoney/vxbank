import { UserAuth } from "../../context/AuthContext";
import { eventResultsAPI } from "@/api/eventResultEndpoint";

export default function Event1v1SetResultsComponent({
  eventId,
  userId,
  fetchResults,
  resultsData,
  participantResponse,
}) {
  const { vxUserInfo } = UserAuth();

  console.log("resultsData", resultsData);
  let participantList = participantResponse.participantList;
  console.log("participantList", participantList);

  const handelSetResults = async () => {
    try {
      const eventResultCreateParams = {
        vxEventId: eventId,
        vxUserId: vxUserInfo.id,
        participantId: userId,
        participantFinalResultPlace: "firstPlace",
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

  function currentUserIsParticipant() {
    return participantList.find(
      (participant) => participant.vxUserId === vxUserInfo.id
    );
  }

  function currentUserMarkedWinner() {
    return resultsData?.eventResultList.find(
      (result) => result.participantId === vxUserInfo.id
    );
  }

  let showButton = true;
  if (currentUserMarkedWinner()) {
    showButton = false;
  }
  if (!currentUserIsParticipant()) {
    showButton = false;
  }

  let markWinnerButton = !showButton ? null : (
    <button
      className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
      onClick={handelSetResults}
    >
      Mark as winner
    </button>
  );

  return <div className="pt-2">{markWinnerButton}</div>;
}
