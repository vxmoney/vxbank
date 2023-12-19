"use client";
import { useParams } from "next/navigation";
import { useRouter } from "next/navigation";
import Link from "next/link";

export default function UsageExampleId() {
  let { id, qid } = useParams();
  //let {qid} = useRouter().query;
  
  return (
    <div>
      <h1 class="p-2 mb-2 text-lg font-semibold text-gray-900 dark:text-white">
        Hello id page
      </h1>
      <h2 class="p-2 mb-2 text-lg font-semibold text-gray-900 dark:text-white">
        Path Parameters:
      </h2>
      <p class="p-2 mb-2 text-base text-gray-900 dark:text-white">
        This section is using the path parameter id. The id captured value is:{" "}
        {id}
      </p>
      <ul class="p-2 text-gray-500 dark:text-gray-400">
        <li class="flex items-center">
          <p class="m-0"> - Set path val to 123 </p>
          <Link href="/usageExamples/123">
            <button class="ml-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
              path id=123
            </button>
          </Link>
        </li>
        <li class="flex items-center py-2">
          <p class="m-0"> - Set path val to 123 </p>
          <Link href="/usageExamples/very_long_text_to_see_what_happens">
            <button class="ml-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
              path id=very_long_text_to_see_what_happens
            </button>
          </Link>
        </li>
      </ul>
      <p class="p-2 mb-2 text-base text-gray-900 dark:text-white">
        This section is using the query parameter qid. The qid captured value
        is:{qid || "undefined"}
      </p>
      <ul class="p-2 text-gray-500 dark:text-gray-400">
        <li class="flex items-center">
          <p class="m-0"> - Set query qid to 999 </p>
          <Link href={`/usageExamples/${id}?qid=999`}>
            <button class="ml-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
              qid=999
            </button>
          </Link>
        </li>
        <li class="flex items-center py-2">
          <p class="m-0"> - Set query qid to some_text_qid_value </p>
          <Link href={`/usageExamples/${id}?qid=some_text_qid_value`}>
            <button class="ml-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
              qid=some_text_qid_value
            </button>
          </Link>
        </li>
      </ul>
    </div>
  );
}
