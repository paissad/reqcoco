package net.paissad.tools.reqcoco.parser.redmine;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.junit.Assume;

public interface RedmineTestUtil {

	String HTTPS_REDMINE_ORG_ISSUES_JSON = "https://redmine.org/issues.json";

	public static void assumeRedminePublicApiReachable() {
		Assume.assumeTrue("The Redmine public API '" + HTTPS_REDMINE_ORG_ISSUES_JSON + "' is not reachable. This test will be ignored !",
		        isRedminePublicApiReachable());
	}

	/**
	 * @return <code>true</code> if the HTTP call to https://redmine.org/issues.json is successful, <code>false</code> otherwise.
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static boolean isRedminePublicApiReachable() {

		final ResponseHandler<Boolean> handler = new ResponseHandler<Boolean>() {
			@Override
			public Boolean handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				final int statusCode = response.getStatusLine().getStatusCode();
				return statusCode >= 200 && statusCode < 300;
			}
		};
		try {
			return Request.Get(HTTPS_REDMINE_ORG_ISSUES_JSON).execute().handleResponse(handler);
		} catch (IOException e) {
			return false;
		}
	}

}
