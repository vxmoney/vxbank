import Link from "next/link";

export default function IdPathParams() {
  return (
    <div className="p-4">
      <h1>IdPathParams</h1>
      <p className="p-2">
        This is the staring point for the [id].js page. It will pass the first
        id with value v01
      </p>
      <p className="p-2">
        The path params are most simple to handel. Just make a folder and put
        the name inside square brakes In you case check the
        /home/bogdan/workspace/vxbank/vxbank-web-app-v2/src/app/usageExamples/[id]/page.js.
      </p>
      <p className="p-2">
        The page.js lives inside the [id] folder. Because of that the id is a
        path param variable that you can decode using the useParams() hook.
      </p>
      <p className="p-2">
        Query params are much simpler. Next js calls them search params. You can
        use the useSearchParams() hook to get them.
      </p>
      <p className="p-2">
        Check how evrything works in this example fetchPingWhoAmIError
      </p>
      <p>
        <Link href="/usageExamples/example">
          <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
            Go to path and query examples
          </button>
        </Link>
      </p>
    </div>
  );
}
