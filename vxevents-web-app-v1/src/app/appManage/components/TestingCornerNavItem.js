import Link from "next/link";
import { useState, useEffect } from "react";

const TestingCornerNavItem = () => {

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
        <li className="p-2 cursor-pointer">
        <Link href="/appManage/usageExamples">TestingCorner</Link>
      </li>
      )}
    </div>
  );
};

export default TestingCornerNavItem;
