import { useState, useEffect } from "react";

const ActivatePublicEventsComponent = () => {
  const [isVisible, setIsVisible] = useState(false);

  // ctrl + m to toggle visibility
  useEffect(() => {
    const handleKeyPress = (event) => {
      if (event.ctrlKey && event.key === "m") {
        setIsVisible(!isVisible); // Toggle visibility
        console.log(
          "Ctrl+M pressed, currentVisibility: ",
          isVisible ? "visible" : "hidden "
        );
      }
    };

    document.addEventListener("keydown", handleKeyPress);

    return () => {
      document.removeEventListener("keydown", handleKeyPress);
    };
  }, [isVisible]); // Add isVisible to dependency array

  const toggleVisibility = () => {
    setIsVisible(!isVisible);
  };

  // long press to toggle visibility on mobile
  useEffect(() => {
    let touchStartTime = 0;
    let touchTimeout;

    const handleTouchStart = () => {
      touchStartTime = new Date().getTime();
      touchTimeout = setTimeout(() => {
        setIsVisible(!isVisible);
      }, 5000); // Adjust the duration as needed
    };

    const handleTouchEnd = () => {
      const touchEndTime = new Date().getTime();
      const duration = touchEndTime - touchStartTime;

      if (duration < 5000) { // Adjust threshold if needed
        clearTimeout(touchTimeout);
      }
    };

    document.addEventListener('touchstart', handleTouchStart);
    document.addEventListener('touchend', handleTouchEnd);

    return () => {
      document.removeEventListener('touchstart', handleTouchStart);
      document.removeEventListener('touchend', handleTouchEnd);
    };
  }, [isVisible]); // Add isVisible to dependency array

  return (
    <div>
      {isVisible && (
        <div className="pt-8 pl-8 pr-8">
          <div className="block  p-6 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 ">
            <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
              Activate manage public events features
            </h5>
            <p className="font-normal text-gray-700 dark:text-gray-400k">
              Nothing fancy here, just a simple card
            </p>

            <div className="flex-grow overflow-auto">
              Use this to activate public events features. You can turn this off
              at any time
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ActivatePublicEventsComponent;
