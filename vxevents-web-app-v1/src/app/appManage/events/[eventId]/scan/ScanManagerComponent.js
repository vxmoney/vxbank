"use client";
import React, { useState, useEffect, useRef } from 'react';
import jsQR from 'jsqr';

export default function ScanManagerComponent() {
  const [qrCodeText, setQrCodeText] = useState('');
  const [isScanning, setIsScanning] = useState(false);
  const videoRef = useRef(null); // Reference to the video element
  const streamRef = useRef(null); // Reference to the media stream

  useEffect(() => {
    if (isScanning) {
      navigator.mediaDevices.getUserMedia({ video: { facingMode: "environment" } })
        .then(function(stream) {
          streamRef.current = stream;
          if (videoRef.current) {
            videoRef.current.srcObject = stream;
            videoRef.current.play();
            requestAnimationFrame(tick);
          }
        });
    } else {
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop());
      }
      setQrCodeText('');
    }

    const tick = () => {
      if (isScanning && videoRef.current && videoRef.current.readyState === videoRef.current.HAVE_ENOUGH_DATA) {
        const canvasElement = document.createElement('canvas');
        canvasElement.width = videoRef.current.videoWidth;
        canvasElement.height = videoRef.current.videoHeight;
        const canvas = canvasElement.getContext('2d');
        canvas.drawImage(videoRef.current, 0, 0, canvasElement.width, canvasElement.height);
        var imageData = canvas.getImageData(0, 0, canvasElement.width, canvasElement.height);
        var code = jsQR(imageData.data, imageData.width, imageData.height, {
          inversionAttempts: "dontInvert",
        });
        if (code) {
          setQrCodeText(code.data);
          setIsScanning(false); // Stop scanning once QR code is found
          if (streamRef.current) {
            streamRef.current.getTracks().forEach(track => track.stop());
          }
        } else {
          requestAnimationFrame(tick);
        }
      }
    };

  }, [isScanning]);

  const handleScanClick = () => {
    setIsScanning(true);
  };

  const handleCancelClick = () => {
    setIsScanning(false);
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
              {isScanning ? 'Scanning...' : 'Click "Scan" to start scanning'}
            </p>
            <video ref={videoRef} style={{ width: '100%', height: 'auto' }}></video>
          </>
        )}
        <div className="mt-4">
          <button onClick={handleScanClick} className="mr-4 p-2 bg-blue-500 text-white rounded hover:bg-blue-600">
            Scan
          </button>
          <button onClick={handleCancelClick} className="p-2 bg-red-500 text-white rounded hover:bg-red-600">
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
