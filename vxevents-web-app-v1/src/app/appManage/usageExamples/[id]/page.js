"use client";
import { useParams } from "next/navigation";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { useSearchParams } from "next/navigation";

import Link from "next/link";

export default function UsageExampleId() {
  const searchParams = useSearchParams();

  let { id } = useParams();
  const qid = searchParams.get("qid");

  const router = useRouter();

  useEffect(() => {
    if (router.isReady) {
      console.log("router.isReady", router.isReady);
    } else {
      console.log("router.notReady", router.isReady);
    }
  }, [router]);

  return (
    <div>
      <h1 className="p-2 mb-2 text-lg font-semibold text-gray-900 dark:text-white">
        Hello id page
      </h1>
      <h2 className="p-2 mb-2 text-lg font-semibold text-gray-900 dark:text-white">
        Path Parameters:
      </h2>
      <p className="p-2 mb-2 text-base text-gray-900 dark:text-white">
        This section is using the path parameter id. The id captured value is:{" "}
        {id}
      </p>
      <ul className="p-2 text-gray-500 dark:text-gray-400">
        <li className="flex items-center">
          <p className="m-0"> - Set path val to 123 </p>
          <Link href="/appManage/usageExamples/123">
            <button className="ml-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
              path id=123
            </button>
          </Link>
        </li>
        <li className="flex items-center py-2">
          <p className="m-0"> - Set path val to 123 </p>
          <Link href="/appManage/usageExamples/very_long_text_to_see_what_happens">
            <button className="ml-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
              path id=very_long_text_to_see_what_happens
            </button>
          </Link>
        </li>
      </ul>
      <p className="p-2 mb-2 text-base text-gray-900 dark:text-white">
        This section is using the query parameter qid. The qid captured value
        is:{qid || "undefined"}
      </p>
      <ul className="p-2 text-gray-500 dark:text-gray-400">
        <li className="flex items-center">
          <p className="m-0"> - Set query qid to 999 </p>
          <Link href={`/appManage/usageExamples/${id}?qid=999`}>
            <button className="ml-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
              qid=999
            </button>
          </Link>
        </li>
        <li className="flex items-center py-2">
          <p className="m-0"> - Set query qid to some_text_qid_value </p>
          <Link href={`/appManage/usageExamples/${id}?qid=some_text_qid_value`}>
            <button className="ml-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
              qid=some_text_qid_value
            </button>
          </Link>
        </li>
      </ul>
    </div>
  );
}
