import React, { useState, useEffect, useRef } from "react";
import jsQR from "jsqr";

export default function ScanManagerComponent() {
  const [qrCodeText, setQrCodeText] = useState("");
  const [isScanning, setIsScanning] = useState(false);
  const videoRef = useRef(null); // Reference to the video element
  const streamRef = useRef(null); // Reference to the media stream
  const [frameCount, setFrameCount] = useState(0); // For tracking the number of frames processed
  const [eventLog, setEventLog] = useState([]); // For tracking detected events

  useEffect(() => {
    const tick = () => {
      if (
        !isScanning ||
        !videoRef.current ||
        videoRef.current.readyState !== videoRef.current.HAVE_ENOUGH_DATA
      ) {
        // If not scanning or video isn't ready, try again later
        requestAnimationFrame(tick);
        return;
      }

      setFrameCount((prevFrameCount) => prevFrameCount + 1); // Increment frame count

      // Create a canvas to capture the current video frame
      const canvasElement = document.createElement("canvas");
      canvasElement.width = videoRef.current.videoWidth;
      canvasElement.height = videoRef.current.videoHeight;
      const canvas = canvasElement.getContext("2d");
      canvas.drawImage(
        videoRef.current,
        0,
        0,
        canvasElement.width,
        canvasElement.height
      );
      const imageData = canvas.getImageData(
        0,
        0,
        canvasElement.width,
        canvasElement.height
      );
      const code = jsQR(imageData.data, imageData.width, imageData.height, {
        inversionAttempts: "dontInvert",
      });

      if (code) {
        setQrCodeText(code.data);
        setIsScanning(false); // Stop scanning once QR code is found
        setEventLog((prevEventLog) => [...prevEventLog, "QR Code detected: " + code.data]); // Log event
        streamRef.current?.getTracks().forEach((track) => track.stop());
      } else {
        setEventLog((prevEventLog) => [...prevEventLog, "No QR Code detected"]);
        requestAnimationFrame(tick); // Continue scanning
      }
    };

    if (isScanning) {
      navigator.mediaDevices
        .getUserMedia({ video: { facingMode: "environment" } })
        .then((stream) => {
          streamRef.current = stream;
          videoRef.current.srcObject = stream;
          videoRef.current.play().then(() => {
            requestAnimationFrame(tick); // Start the scanning loop once the video plays
          });
        })
        .catch((error) => {
          console.error("Error accessing the camera", error);
          setIsScanning(false); // If there's an error, stop scanning
        });
    } else {
      if (streamRef.current) {
        streamRef.current.getTracks().forEach((track) => track.stop());
      }
    }
  }, [isScanning]); // Make sure useEffect depends on `isScanning` to re-trigger scanning

  const handleScanClick = () => {
    setQrCodeText("");
    setFrameCount(0);
    setEventLog([]);
    setIsScanning(true);
    setEventLog((prevEventLog) => [...prevEventLog, "Scan started"]); // Log event
  };

  const handleCancelClick = () => {
    setIsScanning(false);
    setEventLog((prevEventLog) => [...prevEventLog, "Scan canceled"]); // Log event
  };

  return (
    <div className="p-8 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700">
      <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
        Scan app
      </h5>
      <div className="mb-4">
        {qrCodeText ? (
          <p className="font-normal text-gray-700 dark:text-gray-400">
            QR Code content: {qrCodeText}
          </p>
        ) : (
          <>
            <p className="font-normal text-gray-700 dark:text-gray-400">
              {isScanning ? "Scanning..." : 'Click "Scan" to start scanning'}
            </p>
            <video
              ref={videoRef}
              style={{ width: "100%", height: "auto" }}
            ></video>
          </>
        )}
        <div className="mt-4">
          <button
            className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
            onClick={handleScanClick}
          >
            Scan
          </button>
          <button
            className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
            onClick={handleCancelClick}
          >
            Cancel
          </button>
        </div>
      </div>
      {/* Debug Box with Dark Mode */}
      <div
        className={`mt-4 p-4 bg-gray-100 border border-gray-200 rounded dark:bg-gray-700 dark:border-gray-600 dark:text-gray-300`}
      >
        <h6 className="font-bold text-gray-900 dark:text-gray-300">
          Debug Info:
        </h6>
        <p>Frame Count: {frameCount}</p>
        <div>Events:</div>
        <ul>
          {eventLog.slice(-10).map((event, index) => (
            <li key={index}>{event}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}
